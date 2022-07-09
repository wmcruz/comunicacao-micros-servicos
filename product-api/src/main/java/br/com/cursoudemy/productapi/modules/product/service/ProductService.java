package br.com.cursoudemy.productapi.modules.product.service;

import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.product.dto.ProductRequest;
import br.com.cursoudemy.productapi.modules.product.dto.ProductResponse;
import br.com.cursoudemy.productapi.modules.product.model.Product;
import br.com.cursoudemy.productapi.modules.product.repository.ProductRepository;
import br.com.cursoudemy.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;

    public ProductResponse save(ProductRequest request) {
        this.validateProductDataInformed(request);
        this.validateCategoryAndSupplierIdInformed(request);

        var category = this.categoryService.findById(request.getCategoryId());
        var supplier = this.supplierService.findById(request.getSupplierId());

        var product = this.productRepository.save(Product.of(request, supplier, category));
        return ProductResponse.of(product);
    }

    private void validateProductDataInformed(ProductRequest request) {
        if (ObjectUtils.isEmpty(request.getName())) {
            throw new ValidationException("The product's name was not informed.");
        }
        if (ObjectUtils.isEmpty(request.getQuantityAvailable())) {
            throw new ValidationException("The product's quantity was not informed.");
        }
        if (request.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The quantity should not be less or equal to zero.");
        }
    }

    private void validateCategoryAndSupplierIdInformed(ProductRequest request) {
        if (ObjectUtils.isEmpty(request.getCategoryId())) {
            throw new ValidationException("The category ID was not informed.");
        }
        if (ObjectUtils.isEmpty(request.getSupplierId())) {
            throw new ValidationException("The supplier ID was not informed.");
        }
    }
}
