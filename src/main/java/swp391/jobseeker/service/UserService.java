package swp391.jobseeker.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Company;
import swp391.jobseeker.domain.Role;
import swp391.jobseeker.domain.User;
import swp391.jobseeker.repository.RoleRepository;
import swp391.jobseeker.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleSaveUser(User user) {
        this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        return this.userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id);
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public Page<User> getAllUsersPageable(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public boolean checkUserExistByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public void deleteById(Long userId) {
        this.userRepository.deleteById(userId);
    }

    public Company getCompanyByUser(User user) {
        if (user == null || user.getWorkingCompany() == null) {
            return null;
        }
        return user.getWorkingCompany();
    }

    public boolean checkUserExistByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean checkUserExistByPhone(String phone) {
        return this.userRepository.existsByPhone(phone);
    }

    public Page<User> getAllFollowerPageable(long company_id, Pageable pageable) {
        Page<User> Followers = this.userRepository.findFollowersByCompanyId(company_id, pageable);
        return Followers;
    }

    public List<User> getAllFollower(long company_id) {
        List<User> Followers = this.userRepository.getFollowersByCompanyId(company_id);
        return Followers;
    }

    public long getNumberOfActiveUser() {
        return this.userRepository.countByIsActivatedTrue();
    }
}
