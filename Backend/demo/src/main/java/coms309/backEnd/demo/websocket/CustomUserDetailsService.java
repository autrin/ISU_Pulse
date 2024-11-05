package coms309.backEnd.demo.websocket;

import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Load user by username (used for authentication)
    @Override
    public UserDetails loadUserByUsername(String netId) throws UsernameNotFoundException {
        User user = userRepository.findUserByNetId(netId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with net id: " + netId)
                );

        return new org.springframework.security.core.userdetails.User(
                user.getNetId(),
                user.getHashedPassword(),
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserType()));
    }
}