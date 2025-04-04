package swp391.jobseeker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findAll();

    Optional<District> findByCode(Long code);

    List<District> findByProvinceCode(Long provinceCode);
}
