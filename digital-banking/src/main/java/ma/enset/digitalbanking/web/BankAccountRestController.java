package ma.enset.digitalbanking.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.digitalbanking.dtos.*;
import ma.enset.digitalbanking.dtos.requests.*;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Tag(name = "Bank Accounts", description = "Gestion des comptes bancaires et opérations")
public class BankAccountRestController {

    private final BankAccountService bankAccountService;

    @GetMapping
    @Operation(summary = "Lister tous les comptes")
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un compte par son id")
    public BankAccountDTO getBankAccount(@PathVariable String id) {
        return bankAccountService.getBankAccount(id);
    }

    @GetMapping("/{id}/operations")
    @Operation(summary = "Historique complet (non paginé) d'un compte")
    public List<AccountOperationDTO> getHistory(@PathVariable String id) {
        return bankAccountService.accountHistory(id);
    }

    @GetMapping("/{id}/pageOperations")
    @Operation(summary = "Historique paginé d'un compte")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        return bankAccountService.getAccountHistory(id, page, size);
    }

    @PostMapping("/current")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un compte courant")
    public CurrentBankAccountDTO saveCurrentAccount(@RequestBody @Valid CurrentAccountRequestDTO request) {
        return bankAccountService.saveCurrentBankAccount(request);
    }

    @PostMapping("/saving")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un compte épargne")
    public SavingBankAccountDTO saveSavingAccount(@RequestBody @Valid SavingAccountRequestDTO request) {
        return bankAccountService.saveSavingBankAccount(request);
    }

    @PostMapping("/debit")
    @Operation(summary = "Débiter un compte")
    public DebitDTO debit(@RequestBody @Valid DebitDTO debitDTO) {
        bankAccountService.debit(debitDTO);
        return debitDTO;
    }

    @PostMapping("/credit")
    @Operation(summary = "Créditer un compte")
    public CreditDTO credit(@RequestBody @Valid CreditDTO creditDTO) {
        bankAccountService.credit(creditDTO);
        return creditDTO;
    }

    @PostMapping("/transfer")
    @Operation(summary = "Effectuer un virement entre deux comptes")
    public TransferDTO transfer(@RequestBody @Valid TransferDTO transferDTO) {
        bankAccountService.transfer(transferDTO);
        return transferDTO;
    }
}
