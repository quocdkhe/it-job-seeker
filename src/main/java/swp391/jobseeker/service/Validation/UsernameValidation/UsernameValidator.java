package swp391.jobseeker.service.Validation.UsernameValidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import swp391.jobseeker.service.UserService;

@Service
public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (userService != null) {
            if (username != null && username.length() < 6) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Tên đăng nhập phải có ít nhất 6 ký tự")
                        .addConstraintViolation();
                return false;
            }
            if (username == null) {
                return true;
            }
            if (username != null && userService.checkUserExistByUsername(username)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Tên đăng nhập đã tồn tại").addConstraintViolation();
                return false;
            }
        }
        return true;
    }

}
