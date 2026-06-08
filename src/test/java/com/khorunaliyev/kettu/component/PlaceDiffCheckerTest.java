package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.dto.request.place.PlacePhotoRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateRequest;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlaceDiffCheckerTest {

    private final PlaceDiffChecker checker = new PlaceDiffChecker();

    private Place place(String name, String description) {
        Place place = new Place();
        place.setName(name);
        place.setDescription(description);
        return place;
    }

    private PlacePhotoRequest photoReq(String name, boolean main) {
        PlacePhotoRequest req = new PlacePhotoRequest();
        req.setImageName(name);
        req.setIsMain(main);
        return req;
    }

    @Test
    void detectsNameChange() {
        PlaceUpdateRequest request = new PlaceUpdateRequest();
        request.setName("New Name");

        assertThat(checker.isPlaceDifferent(place("Old Name", "desc"), request)).isTrue();
    }

    @Test
    void ignoresCaseAndSurroundingWhitespaceInName() {
        PlaceUpdateRequest request = new PlaceUpdateRequest();
        request.setName("  cafe  ");

        assertThat(checker.isPlaceDifferent(place("Cafe", "desc"), request)).isFalse();
    }

    @Test
    void detectsDescriptionChange() {
        PlaceUpdateRequest request = new PlaceUpdateRequest();
        request.setDescription("Updated description");

        assertThat(checker.isPlaceDifferent(place("Cafe", "Old"), request)).isTrue();
    }

    @Test
    void nullFieldsMeanNoChange() {
        PlaceUpdateRequest request = new PlaceUpdateRequest();

        assertThat(checker.isPlaceDifferent(place("Cafe", "desc"), request)).isFalse();
    }

    @Test
    void detectsPhotoListChange() {
        Place existing = place("Cafe", "desc");
        PlacePhoto photo = new PlacePhoto();
        photo.setImage("a.jpg");
        photo.setMain(true);
        existing.setPhotos(List.of(photo));

        PlaceUpdateRequest request = new PlaceUpdateRequest();
        request.setPlacePhotos(List.of(photoReq("b.jpg", false)));

        assertThat(checker.isPlaceDifferent(existing, request)).isTrue();
    }

    @Test
    void identicalPhotoListMeansNoChange() {
        Place existing = place("Cafe", "desc");
        PlacePhoto photo = new PlacePhoto();
        photo.setImage("a.jpg");
        photo.setMain(true);
        existing.setPhotos(List.of(photo));

        PlaceUpdateRequest request = new PlaceUpdateRequest();
        request.setPlacePhotos(List.of(photoReq("a.jpg", true)));

        assertThat(checker.isPlaceDifferent(existing, request)).isFalse();
    }

    @Test
    void emptyRequestedPhotoListDoesNotThrow() {
        Place existing = place("Cafe", "desc"); // no existing photos

        PlaceUpdateRequest request = new PlaceUpdateRequest();
        request.setPlacePhotos(List.of());

        // Regression: previously System.out.println(getPlacePhotos().get(0)) threw an
        // IndexOutOfBoundsException on an empty list. It must now compute cleanly.
        // Both sides are empty, so there is no difference.
        assertThat(checker.isPlaceDifferent(existing, request)).isFalse();
    }
}
