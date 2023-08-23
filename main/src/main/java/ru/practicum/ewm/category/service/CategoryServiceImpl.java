package ru.practicum.ewm.category.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.ConflictException;
import ru.practicum.ewm.common.NotFoundException;
import ru.practicum.ewm.common.Verify;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    private static final Sort SORT_BY_ID = Sort.by(Sort.Direction.ASC, "id");

    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        if(categoryRepository.existsByName(category.getName())){
            throw new ConflictException("Название категории уже существует");
        }
        category = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        Category category = getCategoryById(catId);
        if(eventRepository.countAllByCategory(category)>0){
            throw new ConflictException("Существуют события, связанные с категорией");
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto patchCategory(Long catId, NewCategoryDto newCategoryDto) {
        Category category = getCategoryById(catId);
        if(categoryRepository.existsByNameAndIdNot(newCategoryDto.getName(),category.getId())){
            throw new ConflictException("Название категории уже существует");
        }
        category.setName(newCategoryDto.getName());
        category = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Verify.verifyFromAndSize(from,size);

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_ID);

        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = getCategoryById(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    public Category getCategoryById(Long catId){
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id %d не найдена", catId)));
    }

}
