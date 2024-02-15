package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageFileService {

  String uploadImage(MultipartFile file, String isbn) throws BadRequestDetailsException;

  String downloadImage(String coverUrl, String isbn);
}
