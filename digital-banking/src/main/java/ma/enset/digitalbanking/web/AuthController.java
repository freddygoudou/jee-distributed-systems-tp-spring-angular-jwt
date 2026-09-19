package ma.enset.digitalbanking.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ma.enset.digitalbanking.dtos.UserDTO;
import ma.enset.digitalbanking.dtos.requests.ChangePasswordRequest;
import ma.enset.digitalbanking.dtos.requests.RegisterRequest;
import ma.enset.digitalbanking.security.JwtService;
import ma.enset.digitalbanking.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Login, profil et gestion du mot de passe")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Authentification et obtention d'un token JWT")
    public Map<String, Object> login(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtService.generateToken(authentication);
        return Map.of(
                "accessToken", token,
                "username", authentication.getName());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Création d'un nouvel utilisateur (rôle USER)")
    public UserDTO register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return userService.getUserProfile(request.getUsername());
    }

    @GetMapping("/profile")
    @Operation(summary = "Profil de l'utilisateur authentifié")
    public UserDTO profile(Principal principal) {
        return userService.getUserProfile(principal.getName());
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changer son mot de passe")
    public Map<String, String> changePassword(Principal principal,
                                               @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(principal.getName(), request.getOldPassword(), request.getNewPassword());
        return Map.of("message", "Mot de passe modifié avec succès");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les utilisateurs (ADMIN)")
    public List<UserDTO> users() {
        return userService.listUsers();
    }

    @PostMapping("/users/{username}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Réinitialiser le mot de passe d'un utilisateur (ADMIN)")
    public Map<String, String> resetPassword(@PathVariable String username,
                                             @RequestBody Map<String, String> body) {
        userService.adminResetPassword(username, body.get("newPassword"));
        return Map.of("message", "Mot de passe réinitialisé pour " + username);
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }
}
