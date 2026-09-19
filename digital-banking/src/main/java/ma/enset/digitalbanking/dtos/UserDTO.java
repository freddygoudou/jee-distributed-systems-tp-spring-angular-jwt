package ma.enset.digitalbanking.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String userId;
    private String username;
    private String email;
    private boolean active;
    private List<String> roles;
}
