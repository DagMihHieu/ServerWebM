package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.CategoryDTO;
import com.lowquality.serverwebm.models.entity.Category;

import com.lowquality.serverwebm.repository.CategoriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    CategoriesRepository categoriesRepository;
    public CategoryService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }
    public void deleteCategory(Integer id) {
        categoriesRepository.deleteById(id);
    }
    public CategoryDTO addCategory(String name, String image) {
        Category category = new Category();
        category.setCategory_name(name);

        categoriesRepository.save(category);
        return convertToDTO(category);
    }
    public List<CategoryDTO> getAllCategories(){
        return categoriesRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public CategoryDTO saveCategory(Category categories){
        categoriesRepository.save(categories);
        return convertToDTO(categories);
    }
    public Optional<CategoryDTO> getCategoryById(Integer id) {
      return  categoriesRepository.findById(id).map(this::convertToDTO);
    }
    public CategoryDTO convertToDTO(Category categories){
        return CategoryDTO.builder()
                .id(categories.getId())
                .category_name(categories.getCategory_name()).build();
    }


    public Category findById(Integer categoryId) {
        Optional<Category> category = categoriesRepository.findById(categoryId);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw new IllegalArgumentException("Category not found: " + categoryId);
        }
    }
}
