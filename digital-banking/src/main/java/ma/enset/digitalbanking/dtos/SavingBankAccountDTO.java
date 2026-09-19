package ma.enset.digitalbanking.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SavingBankAccountDTO extends BankAccountDTO {
    private double interestRate;

    @Override
    public String getType() {
        return "SavingAccount";
    }
}
