package com.systelab.service;

import com.systelab.model.user.User;
import com.systelab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AppUserDetailsService implements UserDetailsService {
      @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(s);


        if(user == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", s));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        System.out.println("WITH "+authorities.size()+" AUTHORITIES");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), authorities);

        return userDetails;
    }
}