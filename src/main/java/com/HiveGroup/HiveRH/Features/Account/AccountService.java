package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountService {
    private AccountRepository accountRepository;

    public UserDetails loadUserByUsername(Long id){
        AccountEntity account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cuenta inexistente", "AccountEntity"));

        return User.builder()
                .username(account.getUser())
                .password(account.getPassword())
                .build();
    }
}
