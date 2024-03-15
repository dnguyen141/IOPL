package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ImageFileServiceImpl implements ImageFileService {

  private final RestTemplate restTemplate;

  private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;

  @Value("${library.coversPath}")
  private String UPLOAD_COVERS_PATH;

  @Override
  public String uploadImage(MultipartFile file, Long bookId) throws BadRequestDetailsException {
    if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
      Map<String, String> violations = new HashMap<>();
      violations.put("file", "Upload file must be a not-empty image that is smaller than 10Mb");
      throw new BadRequestDetailsException("Unable to upload image", violations);
    }

    String contentType = file.getContentType();
    if (contentType != null && contentType.startsWith("image/")) {
      try {
        Path path = getDestinationPath(bookId);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return path.toString();
      } catch (IOException e) {
        Map<String, String> violations = new HashMap<>();
        violations.put("file", "Unable to save image file");
        throw new BadRequestDetailsException("Unable to upload image", violations);
      }
    }

    Map<String, String> violations = new HashMap<>();
    violations.put("file", "File must be an image");
    throw new BadRequestDetailsException("Unable to upload image", violations);
  }

  @Override
  public String downloadImage(String coverUrl, Long bookId) throws BadRequestDetailsException {
    HttpHeaders headers;
    try {
       headers = restTemplate.headForHeaders(coverUrl);
    } catch (RestClientException e) {
      Map<String, String> violations = new HashMap<>();
      violations.put("coverUrl", "Can't download image due to server error");
      throw new BadRequestDetailsException("Unable to download image from URL", violations);
    }
    MediaType contentType = headers.getContentType();
    byte[] imageFileContent = restTemplate.getForObject(coverUrl, byte[].class);

    if (contentType == null || imageFileContent == null || !contentType.isCompatibleWith(MediaType.IMAGE_JPEG) &&
        !contentType.isCompatibleWith(MediaType.IMAGE_PNG) || imageFileContent.length > MAX_FILE_SIZE) {
      Map<String, String> violations = new HashMap<>();
      violations.put("file", "URL must be from a not-empty image that is smaller than 10Mb");
      throw new BadRequestDetailsException("Unable to download image from URL", violations);
    }

    try {
      Path path = getDestinationPath(bookId);
      Files.write(path, imageFileContent);
      return path.toString();
    } catch (IOException e) {
      Map<String, String> violations = new HashMap<>();
      violations.put("file", "Unable to write to file");
      throw new BadRequestDetailsException("Unable to download image from URL", violations);
    }
  }

  private Path getDestinationPath(Long bookId) throws IOException {
    Path coversPath = Paths.get(UPLOAD_COVERS_PATH);
    Files.createDirectories(coversPath);
    return coversPath.resolve(bookId + ".jpg");
  }
}
