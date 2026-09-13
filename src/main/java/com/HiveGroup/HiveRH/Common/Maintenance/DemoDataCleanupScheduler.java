package com.HiveGroup.HiveRH.Common.Maintenance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "hiverh.demo-cleanup", name = "enabled", havingValue = "true")
public class DemoDataCleanupScheduler {

    private static final List<String> OPERATIONAL_TABLES = List.of(
            "certificate",
            "work_request",
            "work_schedule",
            "payroll_detail",
            "payroll",
            "payroll_period",
            "vacation",
            "license",
            "employee_assignment",
            "employee"
    );

    private static final List<String> CATALOG_TABLES = List.of(
            "payroll_concept",
            "position",
            "department",
            "branch"
    );

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${hiverh.demo-cleanup.max-records:5000}")
    private long maxRecords;

    @Value("${hiverh.demo-cleanup.include-catalog-data:true}")
    private boolean includeCatalogData;

    @Value("${hiverh.demo-cleanup.preserved-account-users:admin}")
    private String preservedAccountUsers;

    @Scheduled(cron = "${hiverh.demo-cleanup.daily-cron:0 0 4 * * *}", zone = "${hiverh.demo-cleanup.zone:UTC}")
    @Transactional
    public void cleanDaily() {
        cleanDemoData("daily schedule");
    }

    @Scheduled(
            initialDelayString = "${hiverh.demo-cleanup.threshold-initial-delay-ms:300000}",
            fixedDelayString = "${hiverh.demo-cleanup.threshold-check-delay-ms:900000}"
    )
    @Transactional
    public void cleanWhenRecordLimitIsExceeded() {
        long totalRecords = countManagedRecords();

        if (totalRecords <= maxRecords) {
            return;
        }

        cleanDemoData("record limit exceeded: " + totalRecords + "/" + maxRecords);
    }

    @Transactional
    public void cleanDemoData(String reason) {
        List<String> preservedUsers = parsePreservedUsers();

        log.info("Starting demo data cleanup. Reason: {}. Preserved users: {}", reason, preservedUsers);

        int deletedRows = 0;
        deletedRows += deleteFromTables(OPERATIONAL_TABLES);

        if (includeCatalogData) {
            deletedRows += deleteFromTables(CATALOG_TABLES);
        }

        deletedRows += deleteDemoAccounts(preservedUsers);

        log.info("Finished demo data cleanup. Deleted rows: {}", deletedRows);
    }

    private long countManagedRecords() {
        long total = 0;

        for (String table : OPERATIONAL_TABLES) {
            total += countRows(table);
        }

        if (includeCatalogData) {
            for (String table : CATALOG_TABLES) {
                total += countRows(table);
            }
        }

        total += countRows("account");

        return total;
    }

    private long countRows(String table) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `" + table + "`", Long.class);
        return count == null ? 0 : count;
    }

    private int deleteFromTables(List<String> tables) {
        int deletedRows = 0;

        for (String table : tables) {
            deletedRows += jdbcTemplate.update("DELETE FROM `" + table + "`");
        }

        return deletedRows;
    }

    private int deleteDemoAccounts(List<String> preservedUsers) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("users", preservedUsers);
        return namedParameterJdbcTemplate.update(
                "DELETE FROM `account` WHERE `user` NOT IN (:users)",
                parameters
        );
    }

    private List<String> parsePreservedUsers() {
        List<String> users = Arrays.stream(preservedAccountUsers.split(","))
                .map(String::trim)
                .filter(user -> !user.isBlank())
                .distinct()
                .toList();

        if (users.isEmpty()) {
            return List.of("admin");
        }

        return users;
    }
}
