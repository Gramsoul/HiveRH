package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Branch.BranchEntity;
import com.HiveGroup.HiveRH.Features.Branch.BranchRepository;
import com.HiveGroup.HiveRH.Features.Department.DepartamentRepository;
import com.HiveGroup.HiveRH.Features.Department.DepartmentEntity;
import com.HiveGroup.HiveRH.Features.Employee.DTO.EmployeeResponseDTO;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private DepartamentRepository departamentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void deleteByDniRejectsProtectedAdminAccount() {
        EmployeeEntity employee = activeEmployee("40111222");
        employee.setAccount(account("admin", RolEnum.ADMIN));

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.deleteByDni("40111222")
        );

        assertEquals("La cuenta admin principal no puede ser dada de baja", exception.getMessage());
        verify(employeeRepository, never()).save(employee);
        verify(accountRepository, never()).save(employee.getAccount());
    }

    @Test
    void deleteByDniTerminatesEmployeeAndDisablesLinkedAccount() {
        EmployeeEntity employee = activeEmployee("40111222");
        AccountEntity account = account("40111222", RolEnum.EMPLOYEE);
        EmployeeAssignmentEntity assignment = activeAssignment(employee);
        employee.setAccount(account);
        employee.setAssignments(List.of(assignment));

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        EmployeeResponseDTO response = employeeService.deleteByDni("40111222");

        assertEquals(EmployeeStatus.TERMINATED, employee.getStatus());
        assertNotNull(employee.getTerminationDate());
        assertEquals(AccountStatus.INACTIVE, account.getStatus());
        assertFalse(assignment.isActive());
        assertEquals(employee.getTerminationDate(), assignment.getEndDate());
        assertEquals(EmployeeStatus.TERMINATED, response.status());
        verify(accountRepository).save(account);
        verify(employeeRepository).save(employee);
    }

    private EmployeeEntity activeEmployee(String dni) {
        return EmployeeEntity.builder()
                .id_employee(1L)
                .name("Ada")
                .lastName("Lovelace")
                .phoneNumber("3515550101")
                .genre(GenreEnum.FEMALE)
                .dni(dni)
                .city("Cordoba")
                .address("Bv. Testing 456")
                .birthdate(LocalDate.of(1995, 4, 12))
                .hireDate(LocalDate.of(2026, 1, 10))
                .baseSalary(1500000.0)
                .status(EmployeeStatus.ACTIVE)
                .build();
    }

    private AccountEntity account(String user, RolEnum rol) {
        return AccountEntity.builder()
                .id_account(10L)
                .user(user)
                .email(user + "@hiverh.com")
                .password("encoded")
                .rol(rol)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private EmployeeAssignmentEntity activeAssignment(EmployeeEntity employee) {
        BranchEntity branch = BranchEntity.builder()
                .id_branch(1L)
                .branchName("Casa Central")
                .city("Cordoba")
                .address("Av. Siempre Viva 123")
                .isActive(true)
                .build();
        DepartmentEntity department = DepartmentEntity.builder()
                .id_department(1L)
                .departmentName("People")
                .isActive(true)
                .build();
        PositionEntity position = PositionEntity.builder()
                .id_position(1L)
                .positionName("Analista")
                .isActive(true)
                .build();

        EmployeeAssignmentEntity assignment = new EmployeeAssignmentEntity();
        assignment.setEmployee(employee);
        assignment.setBranch(branch);
        assignment.setDepartment(department);
        assignment.setPosition(position);
        assignment.setStartDate(LocalDate.of(2026, 1, 10));
        assignment.setActive(true);
        return assignment;
    }
}
