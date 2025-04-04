package swp391.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import swp391.jobseeker.domain.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findById(long id);

    List<Skill> findByName(String name);

    @SuppressWarnings("null")
    Page<Skill> findAll(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    void deleteById(long id);

    @Query(value = """
                SELECT s.name, COUNT(js.skill_id) AS count
                FROM job_skill js
                JOIN skills s ON js.skill_id = s.id
                GROUP BY s.name
                ORDER BY COUNT(js.skill_id) DESC
                LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findTopSkills();

    Skill findFirstByNameContainingIgnoreCase(String name);

    @Query(value = """
                SELECT EXISTS (
                    SELECT 1 FROM company_skill WHERE skill_id = :skillId
                    UNION ALL
                    SELECT 1 FROM job_skill WHERE skill_id = :skillId
                )
            """, nativeQuery = true)
    boolean isSkillUsedByAnyCompanyOrJob(long skillId);
}
