package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findAll();

}
