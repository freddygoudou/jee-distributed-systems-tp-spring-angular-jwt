package ma.enset.digitalbanking.services;

import ma.enset.digitalbanking.dtos.UserDTO;
import ma.enset.digitalbanking.dtos.requests.RegisterRequest;
import ma.enset.digitalbanking.entities.AppRole;
import ma.enset.digitalbanking.entities.AppUser;

import java.util.List;

public interface UserService {
    AppUser saveNewUser(String username, String password, String email);
    AppUser register(RegisterRequest request);
    AppRole saveNewRole(String roleName);
    void addRoleToUser(String username, String roleName);
    AppUser loadUserByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    void adminResetPassword(String username, String newPassword);
    List<UserDTO> listUsers();
    UserDTO getUserProfile(String username);
}
