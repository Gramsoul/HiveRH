package com.HiveGroup.HiveRH.Features.Account;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountEntity toEntity(NewAccountDTO account);
    AccountDTO toDTO(AccountEntity account);
}
