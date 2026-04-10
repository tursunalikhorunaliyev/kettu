package com.khorunaliyev.kettu.util.query;

import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.resources.Tag;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PlaceSpecification {
    public static Specification<Place> filterBy(
            PlaceStatus status, String name, Integer categoryId, List<Integer> tagIds, Integer regionId, Integer districtId
    ){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(status!=null){
                System.out.println("---------------------------------");
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if(name!=null && !name.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+ name.toLowerCase()+"%"));
            }
            if(categoryId != null){
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            if(tagIds!=null && !tagIds.isEmpty()){
                Fetch<Place, Tag> tagFetch = root.fetch("tags", JoinType.INNER);

                Join<Place, Tag> tagJoin = (Join<Place, Tag>) tagFetch; // fetch-ni join-ga cast qilamiz

                predicates.add(tagJoin.get("id").in(tagIds));
                query.distinct(true);
            }
            if(regionId!=null){
                predicates.add(criteriaBuilder.equal(root.get("region").get("id"), regionId));
            }

            if(districtId!=null){
                predicates.add(criteriaBuilder.equal(root.get("district").get("id"), districtId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
