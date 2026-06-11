package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Common.Security.Config.SecurityConfiguration;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountService {
    private AccountRepository accountRepository;
    private AccountMapper accountMapper;
    private PasswordEncoder passwordEncoder;


    public UserDetails loadUserByUsername(Long id){
        AccountEntity account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cuenta inexistente", "AccountEntity"));

        return User.builder()
                .username(account.getUser())
                .password(account.getPassword())
                .build();
    }

    public AccountDTO save(NewAccountDTO newAccountDTO){
        AccountEntity entity = accountMapper.toEntity(newAccountDTO);
        entity.setPassword(
                passwordEncoder.encode(entity.getPassword())
        );
        accountRepository.save(entity);

        return accountMapper.toDTO(entity);
    }


}
