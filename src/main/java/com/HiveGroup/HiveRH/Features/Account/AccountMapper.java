package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Features.Account.DTO.AccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.NewAccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.ResponseAccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountEntity toEntity(NewAccountDTO account);

    @Mapping(source = "id_account", target = "id")
    AccountDTO toDTO(AccountEntity account);

    ResponseAccountDTO toResponse(AccountEntity account);
}
