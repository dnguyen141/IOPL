package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.HashMap;

@Service
public class ImageFileServiceImpl implements ImageFileService {

  @Value("${library.coversDirectory}")
  private final String UPLOAD_FOLDER = "/covers";

  @Override
  public String uploadImage(MultipartFile file, String isbn) throws BadRequestDetailsException {
    if (file.isEmpty()) {
      return null;
    }

    String contentType = file.getContentType();
    if (contentType != null && !contentType.startsWith("image/")) {
      try {
        Path path = Path.of(UPLOAD_FOLDER, isbn);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return path.toString();
      } catch (IOException e) {
        Map<String, Object> violations = new HashMap<>();
        violations.put("file", "Unable to save image file");
        throw new BadRequestDetailsException(violations);
      }
    }

    Map<String, Object> violations = new HashMap<>();
    violations.put("file", "File must be an image");
    throw new BadRequestDetailsException(violations);
  }

  @Override
  public void downloadImage(String imageUrl, String isbn) {

  }
}
