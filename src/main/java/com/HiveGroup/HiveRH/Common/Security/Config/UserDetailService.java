package com.HiveGroup.HiveRH.Common.Security.Config;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;

import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final AccountRepository accountRepository;


    @Override
    public UserDetails loadUserByUsername(@NonNull String identifier)
            throws UsernameNotFoundException {
        return
                accountRepository
                        .findByUserOrEmail(identifier, identifier).orElseThrow(() ->
                                new EntityNotFoundException("username not found", "AccountEntity"));
    }
}
