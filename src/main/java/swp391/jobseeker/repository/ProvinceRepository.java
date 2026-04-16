package swp391.jobseeker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.Province;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> findAll();

    Optional<Province> findByCode(Long code);

    @Query("SELECT p FROM Province p WHERE p.name ILIKE %:name%")
    Province findByNameContainingIgnoreCase(@Param("name") String name);

    @Query(value = "SELECT DISTINCT p.* FROM provinces p " +
            "INNER JOIN branches b ON p.code = b.province_code " +
            "INNER JOIN job_branch jb ON jb.branch_id = b.id " +
            "INNER JOIN jobs j ON j.id = jb.job_id " +
            "WHERE j.id = :jobId", nativeQuery = true)
    List<Province> findProvinceByJobId(@Param("jobId") Long jobId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM provinces)", nativeQuery = true)
    boolean existsAny();

}
