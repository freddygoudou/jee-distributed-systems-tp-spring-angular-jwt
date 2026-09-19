package ma.enset.digitalbanking.services;

import lombok.AllArgsConstructor;
import ma.enset.digitalbanking.dtos.DashboardStatsDTO;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.entities.CurrentAccount;
import ma.enset.digitalbanking.entities.SavingAccount;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.repositories.AccountOperationRepository;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    public DashboardStatsDTO getStats() {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        DashboardStatsDTO dto = new DashboardStatsDTO();

        dto.setTotalCustomers(customerRepository.count());
        dto.setTotalAccounts(accounts.size());
        dto.setTotalOperations(accountOperationRepository.count());
        dto.setTotalBalance(accounts.stream().mapToDouble(BankAccount::getBalance).sum());

        Map<String, Long> accountsByType = new LinkedHashMap<>();
        Map<String, Double> balanceByType = new LinkedHashMap<>();
        accountsByType.put("CurrentAccount", accounts.stream().filter(a -> a instanceof CurrentAccount).count());
        accountsByType.put("SavingAccount", accounts.stream().filter(a -> a instanceof SavingAccount).count());
        balanceByType.put("CurrentAccount", accounts.stream().filter(a -> a instanceof CurrentAccount)
                .mapToDouble(BankAccount::getBalance).sum());
        balanceByType.put("SavingAccount", accounts.stream().filter(a -> a instanceof SavingAccount)
                .mapToDouble(BankAccount::getBalance).sum());
        dto.setAccountsByType(accountsByType);
        dto.setBalanceByType(balanceByType);

        var operations = accountOperationRepository.findAll();
        Map<String, Long> opsByType = new LinkedHashMap<>();
        Map<String, Double> amountByType = new LinkedHashMap<>();
        for (OperationType t : OperationType.values()) {
            opsByType.put(t.name(), operations.stream().filter(o -> o.getType() == t).count());
            amountByType.put(t.name(), operations.stream().filter(o -> o.getType() == t)
                    .mapToDouble(o -> o.getAmount()).sum());
        }
        dto.setOperationsByType(opsByType);
        dto.setAmountByOperationType(amountByType);

        dto.setTopAccounts(accounts.stream()
                .sorted(Comparator.comparingDouble(BankAccount::getBalance).reversed())
                .limit(5)
                .map(a -> {
                    DashboardStatsDTO.TopAccount ta = new DashboardStatsDTO.TopAccount();
                    ta.setAccountId(a.getId());
                    ta.setCustomerName(a.getCustomer() != null ? a.getCustomer().getName() : "-");
                    ta.setType(a instanceof SavingAccount ? "SavingAccount" : "CurrentAccount");
                    ta.setBalance(a.getBalance());
                    ta.setCurrency(a.getCurrency());
                    return ta;
                })
                .collect(Collectors.toList()));

        return dto;
    }
}
