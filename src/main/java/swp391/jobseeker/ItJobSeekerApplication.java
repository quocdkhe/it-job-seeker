package swp391.jobseeker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItJobSeekerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItJobSeekerApplication.class, args);
	}

	/*
	 * @Bean
	 * CommandLineRunner run(LocationService locationService) {
	 * return args -> {
	 * System.out.
	 * println("🚀 Fetching and saving provinces, districts, and wards...");
	 * locationService.fetchAndSaveProvinces();
	 * System.out.println("✅ Data fetching completed!");
	 * };
	 * }
	 */
}
