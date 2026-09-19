package ma.enset.digitalbanking.security;

import lombok.AllArgsConstructor;
import ma.enset.digitalbanking.entities.AppUser;
import ma.enset.digitalbanking.services.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser;
        try {
            appUser = userService.loadUserByUsername(username);
        } catch (RuntimeException e) {
            throw new UsernameNotFoundException("Utilisateur introuvable: " + username);
        }
        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(appUser.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRoleName()))
                        .collect(Collectors.toList()))
                .accountLocked(!appUser.isActive())
                .build();
    }
}
