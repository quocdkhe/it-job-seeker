package swp391.jobseeker.service;

import jakarta.servlet.ServletContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileService {

    private final ServletContext servletContext;

    public FileService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Hàm này sử dụng để lưu file upload lên server
     * 
     * @param file         file upload
     * @param targetFolder thư mục lưu file
     * @return tên file đã lưu (để lưu vào trong database)
     */

    /**
     * Hàm này sử dụng để lưu file upload lên server
     * 
     * @param file         tên file upload, truyền từ thẻ input
     * @param targetFolder đối với ảnh thì ghi loại ảnh, đối với cv, lấy username
     *                     người đó
     * @param type         file type: image để lưu vào thư mục images, cv để lưu vào
     *                     thư mục cv
     * @return true or false
     */
    public String handleSaveUploadFile(MultipartFile file, String targetFolder, String type) {
        if (file.isEmpty()) {
            return "";
        }
        // Get absolute path
        String rootPath = "";
        if (type.equals("image")) {
            rootPath = this.servletContext.getRealPath("/resources/images");
        } else if (type.equals("cv")) {
            rootPath = this.servletContext.getRealPath("/resources/cv");
        } else {
            return null;
        }
        String finalName = "";
        try {
            byte[] bytes = file.getBytes();
            File dir = new File(rootPath + File.separator + targetFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Create the file on server
            finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            File serverFile = new File(dir.getAbsolutePath() + File.separator + finalName);
            try (BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(serverFile))) {
                stream.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalName;
    }

    public boolean handleDeleteImage(String fileName, String targetFolder) {
        String rootPath = this.servletContext.getRealPath("/resources/images");
        File dir = new File(rootPath + File.separator + targetFolder + File.separator + fileName);
        return dir.delete();
    }

    /**
     * Tương tự với hàm này, targetFolder truyền vào username của người đó
     * 
     * @param fileName
     * @param targetFolder
     * @return
     */
    public boolean handleDeleteCV(String fileName, String targetFolder) {
        String rootPath = this.servletContext.getRealPath("/resources/cv");
        File dir = new File(rootPath + File.separator + targetFolder + File.separator + fileName);
        return dir.delete();
    }

}
