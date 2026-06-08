package com.khorunaliyev.kettu.services.geo;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.geo.GeoDataFromPoint;
import com.khorunaliyev.kettu.repository.resource.DistrictRepository;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoServiceTest {

    @Mock
    private DistrictRepository districtRepository;

    @InjectMocks
    private GeoService geoService;

    @Test
    void throwsResourceNotFoundWhenNoDistrictMatchesPoint() {
        when(districtRepository.findDistrictByGeoData(41.0, 69.0)).thenReturn(null);

        assertThatThrownBy(() -> geoService.geoData(41.0, 69.0))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("District not found");
    }

    @Test
    void mapsTupleToGeoDataResponse() {
        Tuple tuple = org.mockito.Mockito.mock(Tuple.class);
        when(tuple.get("region_id", Integer.class)).thenReturn(1);
        when(tuple.get("region_name", String.class)).thenReturn("Tashkent");
        when(tuple.get("district_id", Integer.class)).thenReturn(7);
        when(tuple.get("district_name", String.class)).thenReturn("Chilonzor");
        when(districtRepository.findDistrictByGeoData(41.31, 69.24)).thenReturn(tuple);

        GeoDataFromPoint result = geoService.geoData(41.31, 69.24);

        assertThat(result.region().id()).isEqualTo(1);
        assertThat(result.region().name()).isEqualTo("Tashkent");
        assertThat(result.district().id()).isEqualTo(7);
        assertThat(result.district().name()).isEqualTo("Chilonzor");
    }
}
