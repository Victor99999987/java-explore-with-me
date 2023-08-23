package ru.practicum.ewm.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@Validated
@RestController
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto addNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен запрос на эндпоинт {}", "POST /admin/categories");
        return categoryService.addNewCategory(newCategoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable Long catId) {
        log.info("Получен запрос на эндпоинт {}", "DELETE /admin/categories/" + catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    CategoryDto pathCategory(@PathVariable Long catId,
                      @Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен запрос на эндпоинт {}", "PATCH /admin/categories/" + catId);
        return categoryService.patchCategory(catId, newCategoryDto);
    }

}
