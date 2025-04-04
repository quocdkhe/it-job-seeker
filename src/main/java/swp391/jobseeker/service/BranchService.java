package swp391.jobseeker.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Branch;
import swp391.jobseeker.domain.Company;
import swp391.jobseeker.repository.BranchRepository;
import swp391.jobseeker.repository.CompanyRepository;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;

    public BranchService(BranchRepository branchRepository, CompanyRepository companyRepository) {
        this.branchRepository = branchRepository;
        this.companyRepository = companyRepository;
    }

    public List<Branch> getAllBranchesByCompanyId(long id) {
        Company company = new Company();
        company.setId(id);
        return branchRepository.findByCompany(company);
    }

    public Branch getBranchById(long id) {
        return branchRepository.findById(id);
    }

    public String convertBranchesSetToCity(Set<Branch> branches) {
        Set<String> cities = branches.stream()
                .map(branch -> {
                    String provinceName = branch.getProvince().getName();
                    long provinceCode = branch.getProvince().getCode();
                    if (provinceCode == 1 || provinceCode == 32 || provinceCode == 50) {
                        return provinceName.replace("Thành phố ", "");
                    } else {
                        return "Khác";
                    }
                })
                .collect(Collectors.toSet());
        if (cities.contains("Khác")) {
            cities.remove("Khác");
            cities.add("Khác");
        }
        return String.join(" - ", cities);
    }

    public void handleSaveBranch(Branch branch) {
        branchRepository.save(branch);
    }

    public void handleDeleteBranch(long branchId) {
        branchRepository.deleteById(branchId);
    }

    public long countBranchesByCompanyId(long companyId) {
        return branchRepository.countByCompany(
                companyRepository.findById(companyId));
    }

    public boolean isBranchCanBeDeleted(long branchId) {
        return branchRepository.existsByBranchIdInJobBranch(branchId);
    }
}
