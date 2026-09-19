package ma.enset.digitalbanking.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import ma.enset.digitalbanking.dtos.DashboardStatsDTO;
import ma.enset.digitalbanking.services.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
@Tag(name = "Dashboard", description = "Statistiques et indicateurs")
public class DashboardRestController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales pour le tableau de bord")
    public DashboardStatsDTO stats() {
        return dashboardService.getStats();
    }
}
