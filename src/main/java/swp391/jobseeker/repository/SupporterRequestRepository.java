package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import swp391.jobseeker.domain.SupportRequest;

public interface SupporterRequestRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findAllByUserId(long userId);

    Page<SupportRequest> findAll(Pageable pageable);


    List<SupportRequest> findAll();

    SupportRequest findById(long id);

    void deleteById(long id);

    long countByIsCheckedFalse();
}
