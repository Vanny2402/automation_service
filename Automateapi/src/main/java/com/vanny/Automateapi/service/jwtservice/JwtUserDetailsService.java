package com.vanny.Automateapi.service.jwtservice;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vanny.Automateapi.model.jwtmodel.UserDao;
import com.vanny.Automateapi.model.jwtmodel.UserDto;
import com.vanny.Automateapi.repository.jwtrepository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    // Constructor injection for dependencies
    public JwtUserDetailsService(UserRepository userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDao user = userDao.findByUsername(username);
        if (user == null) {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new JwtUserDetails(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public UserDao save(UserDto user) {
        UserDao newUser = new UserDao();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));  // Encode password
        newUser.setAuthorities(user.getAuthorities());
        return userDao.save(newUser);
    }
}
