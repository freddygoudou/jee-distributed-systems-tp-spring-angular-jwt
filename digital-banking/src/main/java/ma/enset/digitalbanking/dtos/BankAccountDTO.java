package ma.enset.digitalbanking.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import ma.enset.digitalbanking.enums.AccountStatus;

import java.util.Date;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CurrentBankAccountDTO.class, name = "CurrentAccount"),
        @JsonSubTypes.Type(value = SavingBankAccountDTO.class, name = "SavingAccount")
})
public abstract class BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private String currency;
    private String createdBy;
    private CustomerDTO customerDTO;

    public abstract String getType();
}
