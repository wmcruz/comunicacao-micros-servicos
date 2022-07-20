package br.com.cursoudemy.productapi.modules.category.service;

import br.com.cursoudemy.productapi.config.exception.SuccessResponse;
import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.category.repository.CategoryRepository;
import br.com.cursoudemy.productapi.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductService productService;

    public CategoryResponse findByIdResponse(Integer id) {
        return CategoryResponse.of(this.findById(id));
    }

    public Category findById(Integer id) {
        if (ObjectUtils.isEmpty(id))
            throw new ValidationException("The Category ID not informed.");

        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new ValidationException("There's no category for the given ID."));
    }

    public List<CategoryResponse> findAll() {
        return this.categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> findByDescription(String description) {
        if (ObjectUtils.isEmpty(description))
            throw new ValidationException("The category description must be informed.");

        return this.categoryRepository.findByDescriptionIgnoreCaseContaining(description)
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public CategoryResponse save(CategoryRequest request) {
        this.validateCategoryNameInformed(request);

        var category = this.categoryRepository.save(Category.of(request));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest categoryRequest, Integer categoryId) {
        this.validateCategoryNameInformed(categoryRequest);
        this.validateInformedId(categoryId);

        var category = Category.of(categoryRequest);
        category.setId(categoryId);

        this.categoryRepository.save(category);
        return CategoryResponse.of(category);
    }

    public SuccessResponse delete(Integer categoryId) {
        this.validateInformedId(categoryId);

        if (this.productService.existsByCategoryId(categoryId))
            throw new ValidationException("You cannot delete this category because it's already defined by product.");

        this.categoryRepository.deleteById(categoryId);
        return SuccessResponse.create("The category was deleted.");
    }

    private void validateInformedId(Integer categoryId) {
        if (ObjectUtils.isEmpty(categoryId))
            throw new ValidationException("The category ID must be informed.");
    }

    private void validateCategoryNameInformed(CategoryRequest categoryRequest) {
        if (ObjectUtils.isEmpty(categoryRequest.getDescription()))
            throw new ValidationException("The category description was not informed.");
    }
}
