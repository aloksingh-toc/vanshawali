package in.ibrahimabad.vanshawali.common.upload;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LocalFileStorageService implements FileStorageService {

    // All current callers (person photos, gallery uploads, fund receipts) only ever send images
    // from <input accept="image/*">; reject anything else server-side too.
    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private final Path root;

    public LocalFileStorageService(@Value("${app.uploads.dir}") String uploadsDir) {
        this.root = Path.of(uploadsDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file");
        }
        String safeFolder = folder.replaceAll("[^a-zA-Z0-9_-]", "");
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = (original.contains(".") ? original.substring(original.lastIndexOf('.')) : "")
                .toLowerCase(Locale.ROOT);
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(ext) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files (jpg, png, webp, gif) are allowed");
        }
        String filename = UUID.randomUUID() + ext;

        try {
            Path folderPath = root.resolve(safeFolder).normalize();
            if (!folderPath.startsWith(root)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid folder");
            }
            Files.createDirectories(folderPath);
            Path target = folderPath.resolve(filename);
            file.transferTo(target);
            return "/uploads/" + safeFolder + "/" + filename;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file", e);
        }
    }
}
