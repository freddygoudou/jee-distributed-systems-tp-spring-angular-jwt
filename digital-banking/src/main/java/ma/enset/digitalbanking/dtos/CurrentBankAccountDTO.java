package ma.enset.digitalbanking.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrentBankAccountDTO extends BankAccountDTO {
    private double overDraft;

    @Override
    public String getType() {
        return "CurrentAccount";
    }
}
