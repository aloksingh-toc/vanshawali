package in.ibrahimabad.vanshawali.gallery.dto;

import jakarta.validation.constraints.NotBlank;

public record GalleryPostSubmitRequest(
        @NotBlank String photoUrl, String caption, @NotBlank String uploaderName, String uploaderContact) {
}
