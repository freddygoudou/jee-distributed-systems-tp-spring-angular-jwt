package ma.enset.digitalbanking.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_role")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AppRole {
    @Id
    private String roleName;
}
