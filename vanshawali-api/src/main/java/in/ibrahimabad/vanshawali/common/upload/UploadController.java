package in.ibrahimabad.vanshawali.common.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Single shared upload endpoint reused by person photos, gallery uploads, and fund receipts.
 * The "folder" param namespaces files by feature (e.g. "persons", "gallery", "receipts").
 */
@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final FileStorageService storageService;

    public UploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "folder", defaultValue = "misc") String folder) {
        String url = storageService.store(file, folder);
        return ResponseEntity.ok(new UploadResponse(url));
    }
}
