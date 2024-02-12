package inst.iop.LibraryManager.library.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageFileService {

  String uploadImage(MultipartFile file, String isbn);

  void downloadImage(String imageUrl, String isbn);
}
