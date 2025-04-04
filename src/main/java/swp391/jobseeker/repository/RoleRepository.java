package swp391.jobseeker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import swp391.jobseeker.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

}
