package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.Review;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.ReviewEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceReviewMapper {
    PersistenceReviewMapper INSTANCE = Mappers.getMapper(PersistenceReviewMapper.class);
    
    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "reviewerId", source = "reviewer.id")
    Review toDomain(ReviewEntity entity);
    
    @Mapping(target = "rental", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    ReviewEntity toEntity(Review domain);
    
    default ReviewEntity toEntity(Review domain, RentalEntity rental, UserEntity reviewer) {
        ReviewEntity entity = toEntity(domain);
        entity.setRental(rental);
        entity.setReviewer(reviewer);
        return entity;
    }
}
