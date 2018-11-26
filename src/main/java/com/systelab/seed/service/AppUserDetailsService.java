package com.systelab.seed.service;

import com.systelab.seed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service(value = "userService")
public class AppUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByLogin(s)
                .map((myUser) -> new User(myUser.getLogin(), myUser.getPassword(),
                        AuthorityUtils.createAuthorityList(myUser.getRole().toString())))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("The username %s doesn't exist", s)));
    }
}