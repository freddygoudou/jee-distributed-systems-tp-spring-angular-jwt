package ma.enset.digitalbanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.UserDTO;
import ma.enset.digitalbanking.dtos.requests.RegisterRequest;
import ma.enset.digitalbanking.entities.AppRole;
import ma.enset.digitalbanking.entities.AppUser;
import ma.enset.digitalbanking.exceptions.UserException;
import ma.enset.digitalbanking.repositories.AppRoleRepository;
import ma.enset.digitalbanking.repositories.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser saveNewUser(String username, String password, String email) {
        if (userRepository.findByUsername(username) != null) {
            throw new UserException("Cet utilisateur existe déjà: " + username);
        }
        AppUser user = AppUser.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .active(true)
                .build();
        return userRepository.save(user);
    }

    @Override
    public AppUser register(RegisterRequest request) {
        AppUser user = saveNewUser(request.getUsername(), request.getPassword(), request.getEmail());
        addRoleToUser(user.getUsername(), "USER");
        return user;
    }

    @Override
    public AppRole saveNewRole(String roleName) {
        return roleRepository.findById(roleName)
                .orElseGet(() -> roleRepository.save(AppRole.builder().roleName(roleName).build()));
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        AppUser user = loadUserByUsername(username);
        AppRole role = roleRepository.findById(roleName)
                .orElseThrow(() -> new UserException("Rôle introuvable: " + roleName));
        if (user.getRoles().stream().noneMatch(r -> r.getRoleName().equals(roleName))) {
            user.getRoles().add(role);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AppUser loadUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserException("Utilisateur introuvable: " + username);
        }
        return user;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        AppUser user = loadUserByUsername(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserException("L'ancien mot de passe est incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void adminResetPassword(String username, String newPassword) {
        AppUser user = loadUserByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> listUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserProfile(String username) {
        return toDTO(loadUserByUsername(username));
    }

    private UserDTO toDTO(AppUser user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setRoles(user.getRoles().stream().map(AppRole::getRoleName).collect(Collectors.toList()));
        return dto;
    }
}
