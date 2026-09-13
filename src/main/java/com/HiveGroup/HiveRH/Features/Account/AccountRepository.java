package com.HiveGroup.HiveRH.Features.Account;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<@NonNull AccountEntity, @NonNull Long> {

    Optional<@NonNull AccountEntity> findByUserOrEmail(String user, String email);

}
