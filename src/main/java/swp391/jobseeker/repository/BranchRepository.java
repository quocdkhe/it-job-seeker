package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import swp391.jobseeker.domain.Branch;
import swp391.jobseeker.domain.Company;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByCompany(Company company);

    Branch findById(long id);

    long countByCompany(Company company);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM job_branch WHERE branch_id = :branchId)", nativeQuery = true)
    boolean existsByBranchIdInJobBranch(@Param("branchId") long branchId);
}
