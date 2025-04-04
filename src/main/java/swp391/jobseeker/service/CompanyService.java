package swp391.jobseeker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Company;
import swp391.jobseeker.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private static final int PAGE_SIZE_RECRUITER = 7;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public List<Company> findTopCompaniesHomepage() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<Company> page = companyRepository.findAll(pageable);
        return page.getContent();
    }

    public Company getCompanyById(long id) {
        Company Company = this.companyRepository.findById(id);
        return Company;
    }

    public Company getCompanyByName(String name) {
        Company Company = this.companyRepository.findByName(name);
        return Company;
    }

    public Page<Company> getAllCompanyPageable(Pageable pageable) {
        Page<Company> Companies = this.companyRepository.findAll(pageable);
        return Companies;
    }

    public void deleteById(Long id) {
        this.companyRepository.deleteById(id);
    }

    public List<Company> getCompaniesWithIdAndName() {
        List<Object[]> results = companyRepository.findAllCompanyIdsAndNames();

        // Convert the Object[] results to a List of Company objects
        return results.stream()
                .map(result -> {
                    Long id = (Long) result[0];
                    String name = (String) result[1];
                    Company company = new Company();
                    company.setId(id);
                    company.setName(name);
                    return company;
                })
                .collect(Collectors.toList());
    }

    public void handleAddFollowingCompany(long userId, long companyId) {
        this.companyRepository.addFollowingCompany(userId, companyId);
    }

    public void handleUnfollowingCompany(long userId, long companyId) {
        this.companyRepository.deleteFollowingCompany(userId, companyId);
    }

    public boolean isUserFollowingCompany(long userId, long companyId) {
        return this.companyRepository.isUserFollowingCompany(userId, companyId);
    }

    public long countByIsApproved(boolean isApproved) {
        return this.companyRepository.countByIsApproved(isApproved);
    }

    public long countCompanyLikes(long companyId) {
        return this.companyRepository.countCompanyLikes(companyId);
    }
}
