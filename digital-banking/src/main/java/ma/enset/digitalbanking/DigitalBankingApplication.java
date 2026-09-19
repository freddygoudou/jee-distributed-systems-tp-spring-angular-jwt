package ma.enset.digitalbanking;

import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.requests.CreditDTO;
import ma.enset.digitalbanking.dtos.requests.CurrentAccountRequestDTO;
import ma.enset.digitalbanking.dtos.requests.DebitDTO;
import ma.enset.digitalbanking.dtos.requests.SavingAccountRequestDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import ma.enset.digitalbanking.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }

    @Bean
    CommandLineRunner seedUsers(UserService userService) {
        return args -> {
            if (!userService.listUsers().isEmpty()) {
                return;
            }
            userService.saveNewRole("USER");
            userService.saveNewRole("ADMIN");

            userService.saveNewUser("admin", "admin", "admin@bank.com");
            userService.addRoleToUser("admin", "ADMIN");
            userService.addRoleToUser("admin", "USER");

            userService.saveNewUser("user", "user", "user@bank.com");
            userService.addRoleToUser("user", "USER");

            System.out.println(">>> Users seeded: admin/admin (ADMIN,USER), user/user (USER)");
        };
    }

    @Bean
    CommandLineRunner seedData(BankAccountService bankAccountService) {
        return args -> {
            if (!bankAccountService.listCustomers().isEmpty()) {
                return; // seed only once
            }
            List.of("Hassan", "Imane", "Mohamed").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name.toLowerCase() + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });

            bankAccountService.listCustomers().forEach(customer -> {
                // Compte courant
                CurrentAccountRequestDTO current = new CurrentAccountRequestDTO();
                current.setInitialBalance(Math.random() * 9000 + 1000);
                current.setOverDraft(1000);
                current.setCurrency("MAD");
                current.setCustomerId(customer.getId());
                var currentAccount = bankAccountService.saveCurrentBankAccount(current);

                // Compte épargne
                SavingAccountRequestDTO saving = new SavingAccountRequestDTO();
                saving.setInitialBalance(Math.random() * 9000 + 1000);
                saving.setInterestRate(5.5);
                saving.setCurrency("MAD");
                saving.setCustomerId(customer.getId());
                var savingAccount = bankAccountService.saveSavingBankAccount(saving);

                // Quelques opérations
                for (int i = 0; i < 5; i++) {
                    CreditDTO credit = new CreditDTO();
                    credit.setAccountId(currentAccount.getId());
                    credit.setAmount(Math.random() * 1000);
                    credit.setDescription("Versement");
                    bankAccountService.credit(credit);

                    DebitDTO debit = new DebitDTO();
                    debit.setAccountId(savingAccount.getId());
                    debit.setAmount(Math.random() * 100);
                    debit.setDescription("Retrait");
                    bankAccountService.debit(debit);
                }
            });
            System.out.println(">>> Seed data inserted.");
        };
    }
}
