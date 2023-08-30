package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addNewCategory(NewCategoryDto newUCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto patchCategory(Long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategory(Long catId);

    Category getCategoryById(Long catId);
}
