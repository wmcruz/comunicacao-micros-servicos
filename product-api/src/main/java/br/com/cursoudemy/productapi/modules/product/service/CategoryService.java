package br.com.cursoudemy.productapi.modules.product.service;

import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.modules.product.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.product.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.product.model.Category;
import br.com.cursoudemy.productapi.modules.product.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryResponse save(CategoryRequest request) {
        this.validateCategoryNameInformed(request);

        var category = this.categoryRepository.save(Category.of(request));
        return CategoryResponse.of(category);
    }

    private void validateCategoryNameInformed(CategoryRequest request) {
        if (ObjectUtils.isEmpty(request.getDescription())) {
            throw new ValidationException("The category description was not informed.");
        }
    }
}
