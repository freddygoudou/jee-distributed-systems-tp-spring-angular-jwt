package ma.enset.digitalbanking.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CurrentAccountRequestDTO {
    @NotNull(message = "Le solde initial est obligatoire")
    @PositiveOrZero(message = "Le solde initial doit être positif")
    private Double initialBalance;

    @PositiveOrZero(message = "Le découvert doit être positif")
    private double overDraft;

    private String currency = "MAD";

    @NotNull(message = "Le client est obligatoire")
    private Long customerId;
}
