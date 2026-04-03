package com.khorunaliyev.kettu.dto.reponse.place;

public record UploadingPlaceData(Long place_id, String name, String description, Integer photo_count, Long uploaded_count, PhotoData main_photo) {
}
