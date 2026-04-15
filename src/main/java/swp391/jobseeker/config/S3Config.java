package swp391.jobseeker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import swp391.jobseeker.domain.S3Properties;

import java.net.URI;

@Configuration
public class S3Config {

    private final S3Properties s3Properties;

    public S3Config(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                s3Properties.getAccessKey(),
                s3Properties.getSecretKey());

        return S3Client.builder()
                .endpointOverride(URI.create(s3Properties.getEndpoint())) // custom S3-compatible endpoint
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1) // required by SDK, but ignored by most S3-compatible providers
                .forcePathStyle(true) // required for non-AWS S3 (MinIO, Cloudflare R2, etc.)
                .build();
    }
}