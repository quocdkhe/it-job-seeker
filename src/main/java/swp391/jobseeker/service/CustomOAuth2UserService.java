package swp391.jobseeker.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.Role;
import swp391.jobseeker.domain.User;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        // get provider
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // call api
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String username = (String) attributes.get("email");
        String fullName = (String) attributes.get("name");
        String avatar = null;
        // Process avatar file for each provider
        switch (registrationId) {
            case "facebook" -> {

                Map<String, Object> picture = (LinkedHashMap) attributes.get("picture");
                Map<String, Object> data = (LinkedHashMap) picture.get("data");
                avatar = (String) data.get("url");
            }
            case "google" -> {
                avatar = (String) attributes.get("picture");
            }
            case "github" -> {
                username = (String) attributes.get("login");
                if (fullName == null) {
                    fullName = username;
                }
                avatar = (String) attributes.get("avatar_url");
            }
            default -> {
                if (fullName == null) {
                    fullName = username;
                }
            }
        }

        // Set role admin for some specific account
        Role userRole = null;
        if (username.equals("doanquoc281004@gmail.com")
                || username.equals("lenam7546@gmail.com")
                || username.equals("duyhvhe180050@fpt.edu.vn")
                || username.equals("trandinhdung6acxh@gmail.com")
                || username.equals("linhvodanh2004@gmail.com")) {
            userRole = this.userService.getRoleByName("Admin");
        } else {
            userRole = this.userService.getRoleByName("Seeker");
        }

        User user = this.userService.getUserByUsername(username);
        // If user is null, add this oauth user to DB
        if (user == null) {
            User newUser = new User();
            newUser.setActivated(true);
            if (!registrationId.equals("GITHUB")) {
                newUser.setEmail(username);
            }
            newUser.setFullName(fullName);
            newUser.setRole(userRole);
            newUser.setGender(true);
            newUser.setProvider(registrationId.toUpperCase());
            newUser.setUsername(username);
            newUser.setAvatar(avatar);
            this.userService.handleSaveUser(newUser);
        } else {
            // if user is not null, check it's enabled or not
            if (!user.isActivated()) {
                throw new OAuth2AuthenticationException("Account is locked");
            }
            userRole = user.getRole();
            user.setFullName(fullName);
            user.setAvatar(avatar);
            this.userService.handleSaveUser(user);
        }

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.getName())),
                oAuth2User.getAttributes(),
                registrationId.equals("github") ? "login" : "email");
    }
}
