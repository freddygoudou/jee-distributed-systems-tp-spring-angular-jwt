package ma.enset.digitalbanking.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SavingAccountRequestDTO {
    @NotNull(message = "Le solde initial est obligatoire")
    @PositiveOrZero(message = "Le solde initial doit être positif")
    private Double initialBalance;

    @PositiveOrZero(message = "Le taux d'intérêt doit être positif")
    private double interestRate;

    private String currency = "MAD";

    @NotNull(message = "Le client est obligatoire")
    private Long customerId;
}
