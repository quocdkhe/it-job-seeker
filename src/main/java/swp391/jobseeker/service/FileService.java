package swp391.jobseeker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import swp391.jobseeker.domain.S3Properties;

import java.io.IOException;

@Service
public class FileService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public FileService(S3Client s3Client, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    /**
     * Upload a file to S3-compatible storage.
     *
     * @param file         the uploaded file from input
     * @param targetFolder subfolder name (e.g. username, image type)
     * @param type         "image" or "cv"
     * @return full public URL of the uploaded file, or "" / null on failure
     */
    public String handleSaveUploadFile(MultipartFile file, String targetFolder, String type) {
        if (file.isEmpty()) {
            return "";
        }

        String folder;
        if (type.equals("image")) {
            folder = "images/" + targetFolder;
        } else if (type.equals("cv")) {
            folder = "cv/" + targetFolder;
        } else {
            return null;
        }

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        String s3Key = folder + "/" + fileName; // e.g. images/avatar/1234567890-photo.jpg

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

            // Return the public URL
            return s3Properties.getPublicUrl() + "/" + s3Properties.getBucketName() + "/" + s3Key;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Delete an image from S3.
     *
     * @param fileName     file name stored in DB
     * @param targetFolder subfolder (e.g. image type)
     * @return true if deleted successfully
     */
    public boolean handleDeleteImage(String fileName, String targetFolder) {
        String s3Key = "images/" + targetFolder + "/" + fileName;
        return deleteFromS3(s3Key);
    }

    /**
     * Delete a CV from S3.
     *
     * @param fileName     file name stored in DB
     * @param targetFolder subfolder (username)
     * @return true if deleted successfully
     */
    public boolean handleDeleteCV(String fileName, String targetFolder) {
        String s3Key = "cv/" + targetFolder + "/" + fileName;
        return deleteFromS3(s3Key);
    }

    private boolean deleteFromS3(String s3Key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}