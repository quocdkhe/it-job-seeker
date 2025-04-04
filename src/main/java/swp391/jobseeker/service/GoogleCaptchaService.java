package swp391.jobseeker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GoogleCaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verify(String recaptchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", recaptchaSecret);
        body.add("response", recaptchaResponse);
        // Send POST request
        Map<String, Object> response = restTemplate.postForObject(RECAPTCHA_VERIFY_URL, body, Map.class);

        if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
            return false;
        }

        return true;
    }
}
