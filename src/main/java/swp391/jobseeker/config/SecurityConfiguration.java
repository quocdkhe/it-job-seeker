package swp391.jobseeker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import jakarta.servlet.DispatcherType;
import swp391.jobseeker.service.CustomOAuth2UserService;
import swp391.jobseeker.service.CustomUserDetailsService;
import swp391.jobseeker.service.UserService;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

	private static final String LOGIN_REDIRECT = "/login";

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(UserService userService) {
		return new CustomUserDetailsService(userService);
	}

	@Bean
	public DaoAuthenticationProvider authProvider(
			PasswordEncoder passwordEncoder,
			UserDetailsService userDetailsService) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		authProvider.setHideUserNotFoundExceptions(false);
		return authProvider;
	}

	@Bean
	public SpringSessionRememberMeServices rememberMeServices() {
		SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
		// optionally customize
		rememberMeServices.setAlwaysRemember(false);
		return rememberMeServices;
	}

	@Bean
	public AuthenticationSuccessHandler customSuccessAuthHandler(UserService userService) {
		return new CustomSuccessHandler(userService);
	}

	public AuthenticationFailureHandler customFailureAuthHandler() {
		return new CustomFailureHandler();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, UserService userService) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE)
						.permitAll()
						.requestMatchers(HttpMethod.GET, LOGIN_REDIRECT).permitAll()
						.requestMatchers(HttpMethod.POST, "/register").permitAll()
						.requestMatchers(HttpMethod.POST, "/company-registration").permitAll()
						.requestMatchers(HttpMethod.POST, "/job-search").permitAll()
						.requestMatchers("/", "/reset-password/**", "/forget-pass/**",
								"/register/**", "/user/**", "/company-logo/**", "/company-images/**", "/jobs/**",
								"/manager/**", "/companies/**", "/feedback/**", "/feedback-comment/**", "/api/**",
								"/company-registration", "/resume-template", "/about-us", "/contact", "/privacy-policy",
								"/terms-of-service", "/feedback-support/**","/all-companies")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("Admin")
						.requestMatchers("/seeker/**").hasRole("Seeker")
						.requestMatchers("/recruiter/**").hasRole("Recruiter")
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2.loginPage(LOGIN_REDIRECT)
						.defaultSuccessUrl("/", true)
						.failureUrl("/login?error")
						.userInfoEndpoint(user -> user
								.userService(new CustomOAuth2UserService(userService)))
						.successHandler(customSuccessAuthHandler(userService))
						.failureHandler(customFailureAuthHandler()))
				.sessionManagement((sessionManagement) -> sessionManagement
						.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
						.invalidSessionUrl("/logout?expired")
						.maximumSessions(1)
						.maxSessionsPreventsLogin(false))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.deleteCookies("JSESSIONID")
						.invalidateHttpSession(true))
				.rememberMe((rememberMe) -> rememberMe
						.rememberMeServices(rememberMeServices()))
				.formLogin(formLogin -> formLogin
						.loginPage(LOGIN_REDIRECT)
						.failureUrl("/login?error")
						.successHandler(customSuccessAuthHandler(userService))
						.failureHandler(customFailureAuthHandler())
						.permitAll())
				.exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"));
		return http.build();
	}

}
