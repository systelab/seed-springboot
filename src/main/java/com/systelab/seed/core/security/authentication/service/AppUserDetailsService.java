package com.systelab.seed.core.security.authentication.service;

import com.systelab.seed.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service(value = "userManagementService")
public class AppUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) {
        return userRepository.findByLogin(s)
                .map(myUser -> new User(myUser.getLogin(), myUser.getPassword(), AuthorityUtils.createAuthorityList(myUser.getRole().toString())))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("The username %s doesn't exist", s)));
    }
}