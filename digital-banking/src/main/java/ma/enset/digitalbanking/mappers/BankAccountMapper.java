package ma.enset.digitalbanking.mappers;

import ma.enset.digitalbanking.dtos.*;
import ma.enset.digitalbanking.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }

    public Customer fromCustomerDTO(CustomerDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        return customer;
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation operation) {
        AccountOperationDTO dto = new AccountOperationDTO();
        BeanUtils.copyProperties(operation, dto);
        return dto;
    }

    public SavingBankAccountDTO fromSavingAccount(SavingAccount account) {
        SavingBankAccountDTO dto = new SavingBankAccountDTO();
        BeanUtils.copyProperties(account, dto);
        if (account.getCustomer() != null) {
            dto.setCustomerDTO(fromCustomer(account.getCustomer()));
        }
        return dto;
    }

    public CurrentBankAccountDTO fromCurrentAccount(CurrentAccount account) {
        CurrentBankAccountDTO dto = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(account, dto);
        if (account.getCustomer() != null) {
            dto.setCustomerDTO(fromCustomer(account.getCustomer()));
        }
        return dto;
    }

    public BankAccountDTO fromBankAccount(BankAccount account) {
        if (account instanceof SavingAccount savingAccount) {
            return fromSavingAccount(savingAccount);
        } else if (account instanceof CurrentAccount currentAccount) {
            return fromCurrentAccount(currentAccount);
        }
        return null;
    }
}
