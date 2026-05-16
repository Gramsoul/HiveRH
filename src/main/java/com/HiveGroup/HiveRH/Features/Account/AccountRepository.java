package com.HiveGroup.HiveRH.Features.Account;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<@NonNull AccountEntity, @NonNull Long> {
}
