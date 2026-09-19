package ma.enset.digitalbanking.services;

import ma.enset.digitalbanking.dtos.*;
import ma.enset.digitalbanking.dtos.requests.*;
import ma.enset.digitalbanking.exceptions.BalanceNotSufficientException;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    // ===== Customers =====
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    List<CustomerDTO> listCustomers();
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
    CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) throws CustomerNotFoundException;
    void deleteCustomer(Long customerId);
    List<CustomerDTO> searchCustomers(String keyword);

    // ===== Accounts =====
    CurrentBankAccountDTO saveCurrentBankAccount(CurrentAccountRequestDTO request) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(SavingAccountRequestDTO request) throws CustomerNotFoundException;
    List<BankAccountDTO> bankAccountList();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    List<BankAccountDTO> getCustomerAccounts(Long customerId) throws CustomerNotFoundException;

    // ===== Operations =====
    void debit(DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(CreditDTO creditDTO) throws BankAccountNotFoundException;
    void transfer(TransferDTO transferDTO) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<AccountOperationDTO> accountHistory(String accountId);
    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
