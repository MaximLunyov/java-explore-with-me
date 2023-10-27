package ru.practicum.ewm.category.dto;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(NewCategoryDto newCategoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDto(List<Category> categories);

}
