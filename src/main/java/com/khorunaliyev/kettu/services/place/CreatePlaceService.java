package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.component.UserContext;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.geo.GeoDataFromPoint;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlaceLocation;
import com.khorunaliyev.kettu.entity.place.UserActiveUploads;
import com.khorunaliyev.kettu.entity.resources.*;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.place.UserActiveUploadsRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import com.khorunaliyev.kettu.services.geo.GeoService;
import com.khorunaliyev.kettu.services.storage.LocalStorageService;
import com.khorunaliyev.kettu.services.storage.R2Service;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatePlaceService {

    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;
    private final EntityManager entityManager;
    private final GeoService geoService;
    private final UserContext userContext;
    private final UserActiveUploadsRepository userActiveUploadsRepository;
    private final LocalStorageService localStorageService;
    private final R2Service r2Service;
    private final PlacePhotoService placePhotoService;

    @Transactional
    @CacheEvict(value = "places", allEntries = true)
    public ResponseEntity<Response> createPlace(PlaceRequest request, MultipartFile mainPhoto, List<MultipartFile> additionalPhotos) throws IOException {

        if (!categoryRepository.existsById(request.getCategory_id())) {
            throw new ResourceNotFoundException("Category not found");
        }

        Set<Integer> tagsSet = new HashSet<>(request.getTags());

        if (tagsSet.size() != categoryRepository.countByCategoryAndTags(request.getCategory_id(), tagsSet)) {
            throw new ResourceNotFoundException("Some tags not found");
        }

        Place place = new Place();
        place.setName(request.getName());
        place.setDescription(request.getDescription().trim());
        place.setStatus(PlaceStatus.UPLOADING);
        place.setCategory(entityManager.getReference(Category.class, request.getCategory_id()));

        GeoDataFromPoint geoDataFromPoint = geoService.geoData(request.getPlace_location().getLat_(), request.getPlace_location().getLong_());

        Country country = entityManager.getReference(Country.class, 1);
        Region region = entityManager.getReference(Region.class, geoDataFromPoint.region().id());
        District district = entityManager.getReference(District.class, geoDataFromPoint.district().id());

        PlaceLocation placeLocation = new PlaceLocation();
        placeLocation.setCountry(country);
        placeLocation.setRegion(region);
        placeLocation.setDistrict(district);

        placeLocation.setPlace(place);
        place.setLocation(placeLocation);


        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(request.getPlace_location().getLong_(), request.getPlace_location().getLat_()));

        placeLocation.setPoint(point);

        place.setLocation(placeLocation);
        place.setPhotoCount(1 + (additionalPhotos != null ? additionalPhotos.size() : 0));
        place.setTags(request.getTags().stream().map(tagId -> entityManager.getReference(Tag.class, tagId)).collect(Collectors.toSet()));

        Place createdPlace = placeRepository.save(place);

        UserActiveUploads activeUploads = new UserActiveUploads();
        activeUploads.setPlace(createdPlace);
        activeUploads.setUser(userContext.getUser());
        userActiveUploadsRepository.save(activeUploads);

        Map<String, List<String>> localFilePaths = localStorageService.saveToTempDirectory(createdPlace.getId().toString(), mainPhoto, additionalPhotos);

        List<String> localMainPhotoPath = localFilePaths.get("main");
        List<String> localAdditionalPhotoPaths = localFilePaths.get("additional");

        placePhotoService.processAndUpload(createdPlace.getId(), localMainPhotoPath.get(0), localAdditionalPhotoPaths, userContext.getUserId());

        return new ResponseEntity<>(new Response("Success", "Place created successfully"), HttpStatus.CREATED);
    }
}
