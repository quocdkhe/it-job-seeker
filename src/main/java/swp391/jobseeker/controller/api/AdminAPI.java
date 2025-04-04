package swp391.jobseeker.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import swp391.jobseeker.domain.Company;
import swp391.jobseeker.service.CompanyService;

@RestController
public class AdminAPI {

    private final CompanyService companyService;

    public AdminAPI(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/admin/api/companies")
    // Trả về dữ liệu dạng JSON
    public List<Company> getAllCompany() {
        return companyService.getCompaniesWithIdAndName();
    }
}
