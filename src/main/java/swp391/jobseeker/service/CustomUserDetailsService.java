package swp391.jobseeker.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        swp391.jobseeker.domain.User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Sai email hoặc mật khẩu");
        }

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.isActivated(),
                true, true, true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())));
    }
}
