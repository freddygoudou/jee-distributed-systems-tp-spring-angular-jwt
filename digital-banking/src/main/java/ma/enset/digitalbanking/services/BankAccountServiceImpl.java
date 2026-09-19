package ma.enset.digitalbanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.*;
import ma.enset.digitalbanking.dtos.requests.*;
import ma.enset.digitalbanking.entities.*;
import ma.enset.digitalbanking.enums.AccountStatus;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceNotSufficientException;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.mappers.BankAccountMapper;
import ma.enset.digitalbanking.repositories.AccountOperationRepository;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapper mapper;

    /** Nom de l'utilisateur authentifié courant, ou "system" si contexte non authentifié (seed). */
    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "system";
        }
        return auth.getName();
    }

    // ===================== Customers =====================

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = mapper.fromCustomerDTO(customerDTO);
        customer.setCreatedBy(currentUsername());
        Customer saved = customerRepository.save(customer);
        return mapper.fromCustomer(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        return mapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setCreatedBy(currentUsername());
        return mapper.fromCustomer(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.findByNameContainsIgnoreCase(keyword).stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    // ===================== Accounts =====================

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(CurrentAccountRequestDTO request) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + request.getCustomerId()));
        CurrentAccount account = new CurrentAccount();
        account.setId(UUID.randomUUID().toString());
        account.setCreatedAt(new Date());
        account.setBalance(request.getInitialBalance());
        account.setOverDraft(request.getOverDraft());
        account.setCurrency(request.getCurrency());
        account.setStatus(AccountStatus.CREATED);
        account.setCustomer(customer);
        account.setCreatedBy(currentUsername());
        return mapper.fromCurrentAccount(bankAccountRepository.save(account));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(SavingAccountRequestDTO request) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + request.getCustomerId()));
        SavingAccount account = new SavingAccount();
        account.setId(UUID.randomUUID().toString());
        account.setCreatedAt(new Date());
        account.setBalance(request.getInitialBalance());
        account.setInterestRate(request.getInterestRate());
        account.setCurrency(request.getCurrency());
        account.setStatus(AccountStatus.CREATED);
        account.setCustomer(customer);
        account.setCreatedBy(currentUsername());
        return mapper.fromSavingAccount(bankAccountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll().stream()
                .map(mapper::fromBankAccount)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found: " + accountId));
        return mapper.fromBankAccount(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) throws CustomerNotFoundException {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found: " + customerId);
        }
        return bankAccountRepository.findByCustomerId(customerId).stream()
                .map(mapper::fromBankAccount)
                .collect(Collectors.toList());
    }

    // ===================== Operations =====================

    private BankAccount loadAccount(String accountId) throws BankAccountNotFoundException {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found: " + accountId));
    }

    @Override
    public void debit(DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount account = loadAccount(debitDTO.getAccountId());
        double allowed = account.getBalance();
        if (account instanceof CurrentAccount ca) {
            allowed += ca.getOverDraft();
        }
        if (allowed < debitDTO.getAmount()) {
            throw new BalanceNotSufficientException("Balance not sufficient");
        }
        AccountOperation operation = AccountOperation.builder()
                .type(OperationType.DEBIT)
                .amount(debitDTO.getAmount())
                .description(debitDTO.getDescription())
                .operationDate(new Date())
                .createdBy(currentUsername())
                .bankAccount(account)
                .build();
        accountOperationRepository.save(operation);
        account.setBalance(account.getBalance() - debitDTO.getAmount());
        bankAccountRepository.save(account);
    }

    @Override
    public void credit(CreditDTO creditDTO) throws BankAccountNotFoundException {
        BankAccount account = loadAccount(creditDTO.getAccountId());
        AccountOperation operation = AccountOperation.builder()
                .type(OperationType.CREDIT)
                .amount(creditDTO.getAmount())
                .description(creditDTO.getDescription())
                .operationDate(new Date())
                .createdBy(currentUsername())
                .bankAccount(account)
                .build();
        accountOperationRepository.save(operation);
        account.setBalance(account.getBalance() + creditDTO.getAmount());
        bankAccountRepository.save(account);
    }

    @Override
    public void transfer(TransferDTO transferDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        if (transferDTO.getAccountSource().equals(transferDTO.getAccountDestination())) {
            throw new BankAccountNotFoundException("Source and destination accounts must be different");
        }
        String desc = transferDTO.getDescription() == null ? "" : transferDTO.getDescription();
        DebitDTO debitDTO = new DebitDTO();
        debitDTO.setAccountId(transferDTO.getAccountSource());
        debitDTO.setAmount(transferDTO.getAmount());
        debitDTO.setDescription("Transfer to " + transferDTO.getAccountDestination() + " " + desc);
        debit(debitDTO);

        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setAccountId(transferDTO.getAccountDestination());
        creditDTO.setAmount(transferDTO.getAmount());
        creditDTO.setDescription("Transfer from " + transferDTO.getAccountSource() + " " + desc);
        credit(creditDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountOperationDTO> accountHistory(String accountId) {
        return accountOperationRepository.findByBankAccountId(accountId).stream()
                .map(mapper::fromAccountOperation)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount account = loadAccount(accountId);
        Page<AccountOperation> operations = accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO dto = new AccountHistoryDTO();
        dto.setAccountId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setCurrentPage(page);
        dto.setPageSize(size);
        dto.setTotalPages(operations.getTotalPages());
        dto.setAccountOperationDTOS(
                operations.getContent().stream()
                        .map(mapper::fromAccountOperation)
                        .collect(Collectors.toList()));
        return dto;
    }
}
