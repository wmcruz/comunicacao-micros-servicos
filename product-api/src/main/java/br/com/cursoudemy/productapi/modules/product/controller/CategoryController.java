package br.com.cursoudemy.productapi.modules.product.controller;

import br.com.cursoudemy.productapi.modules.product.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.product.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public CategoryResponse save(@RequestBody CategoryRequest request) {
        return this.categoryService.save(request);
    }
}
