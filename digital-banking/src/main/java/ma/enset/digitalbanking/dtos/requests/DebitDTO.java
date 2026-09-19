package ma.enset.digitalbanking.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DebitDTO {
    @NotBlank(message = "L'identifiant du compte est obligatoire")
    private String accountId;

    @Positive(message = "Le montant doit être strictement positif")
    private double amount;

    private String description;
}
