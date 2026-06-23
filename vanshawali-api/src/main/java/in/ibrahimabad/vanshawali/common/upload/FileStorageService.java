package in.ibrahimabad.vanshawali.common.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * Accepts a file, stores it, returns a URL. Local disk in development;
 * swap for an S3/Cloudinary implementation in production without touching callers.
 */
public interface FileStorageService {

    String store(MultipartFile file, String folder);
}
