package in.ibrahimabad.vanshawali.gallery.dto;

import jakarta.validation.constraints.NotBlank;

public record GalleryCommentSubmitRequest(@NotBlank String commenterName, @NotBlank String body) {
}
