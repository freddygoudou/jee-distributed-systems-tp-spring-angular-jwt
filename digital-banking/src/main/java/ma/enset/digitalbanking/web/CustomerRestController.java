package ma.enset.digitalbanking.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
@Tag(name = "Customers", description = "Gestion des clients de la banque")
public class CustomerRestController {

    private final BankAccountService bankAccountService;

    @GetMapping
    @Operation(summary = "Lister tous les clients")
    public List<CustomerDTO> customers() {
        return bankAccountService.listCustomers();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des clients par nom")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword) {
        return bankAccountService.searchCustomers(keyword);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par son id")
    public CustomerDTO getCustomer(@PathVariable Long id) {
        return bankAccountService.getCustomer(id);
    }

    @GetMapping("/{id}/accounts")
    @Operation(summary = "Lister les comptes d'un client")
    public List<BankAccountDTO> customerAccounts(@PathVariable Long id) {
        return bankAccountService.getCustomerAccounts(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un nouveau client")
    public CustomerDTO saveCustomer(@RequestBody @Valid CustomerDTO customerDTO) {
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un client existant")
    public CustomerDTO updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerDTO customerDTO) {
        return bankAccountService.updateCustomer(id, customerDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client")
    public void deleteCustomer(@PathVariable Long id) {
        bankAccountService.deleteCustomer(id);
    }
}
