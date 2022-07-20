package br.com.cursoudemy.productapi.modules.category.controller;

import br.com.cursoudemy.productapi.config.exception.SuccessResponse;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public CategoryResponse save(@RequestBody CategoryRequest request) {
        return this.categoryService.save(request);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return this.categoryService.findAll();
    }

    @GetMapping("{id}")
    public CategoryResponse findById(@PathVariable Integer id) {
        return this.categoryService.findByIdResponse(id);
    }

    @GetMapping("/description/{description}")
    public List<CategoryResponse> findByDescription(@PathVariable String description) {
        return this.categoryService.findByDescription(description);
    }

    @PutMapping("{categoryId}")
    public CategoryResponse update(@RequestBody CategoryRequest categoryRequest, @PathVariable Integer categoryId) {
        return this.categoryService.update(categoryRequest, categoryId);
    }

    @DeleteMapping("{categoryId}")
    public SuccessResponse delete(@PathVariable Integer categoryId) {
        return this.categoryService.delete(categoryId);
    }
}
