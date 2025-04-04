package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.ReviewCreateDto;
import com.automovil_unite.backend.application.dto.ReviewDto;
import com.automovil_unite.backend.domain.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);
    
    ReviewDto toDto(Review review);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviewerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewCreateDto dto);
}
