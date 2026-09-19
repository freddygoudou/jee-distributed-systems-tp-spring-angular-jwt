package ma.enset.digitalbanking.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.enset.digitalbanking.enums.OperationType;

import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date operationDate;

    private double amount;

    @Enumerated(EnumType.STRING)
    private OperationType type;

    private String description;

    /** Utilisateur authentifié ayant effectué l'opération */
    private String createdBy;

    @ManyToOne
    private BankAccount bankAccount;
}
