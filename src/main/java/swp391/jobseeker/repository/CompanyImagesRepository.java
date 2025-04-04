package swp391.jobseeker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import swp391.jobseeker.domain.CompanyImages;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyImagesRepository extends JpaRepository<CompanyImages, Long> {
    Page<CompanyImages> findByCompanyId(Long companyId, Pageable pageable);

    @SuppressWarnings("null")
    Page<CompanyImages> findAll(Pageable pageable);
}
