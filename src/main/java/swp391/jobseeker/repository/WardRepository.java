package swp391.jobseeker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.Ward;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    List<Ward> findAll();

    Optional<Ward> findByCode(Long code);

    List<Ward> findByDistrict_Code(Long districtCode);
}
