package br.com.cursoudemy.productapi.modules.product.service;

import br.com.cursoudemy.productapi.config.exception.SuccessResponse;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;

    public Product findById(Integer id) {
        if (ObjectUtils.isEmpty(id))
            throw new ValidationException("The product ID must be informed.");

        return this.productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no product for the given ID."));
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(this.findById(id));
    }

    public List<ProductResponse> findByName(String name) {
        if (ObjectUtils.isEmpty(name))
            throw new ValidationException("The product name must be informed.");

        return this.productRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        if (ObjectUtils.isEmpty(supplierId))
            throw new ValidationException("The product's supplier ID must be informed.");

        return this.productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        if (ObjectUtils.isEmpty(categoryId))
            throw new ValidationException("The product's category ID must be informed.");

        return this.productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findAll() {
        return this.productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public Boolean existsByCategoryId(Integer categoryId) {
        return this.productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return this.productRepository.existsBySupplierId(supplierId);
    }

    public ProductResponse save(ProductRequest request) {
        this.validateProductDataInformed(request);
        this.validateCategoryAndSupplierIdInformed(request);

        var category = this.categoryService.findById(request.getCategoryId());
        var supplier = this.supplierService.findById(request.getSupplierId());

        var product = this.productRepository.save(Product.of(request, supplier, category));
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest productRequest, Integer productId) {
        this.validateProductDataInformed(productRequest);
        this.validateCategoryAndSupplierIdInformed(productRequest);
        this.validateInformedId(productId);

        var category = this.categoryService.findById(productRequest.getCategoryId());
        var supplier = this.supplierService.findById(productRequest.getSupplierId());

        var product = Product.of(productRequest, supplier, category);
        product.setId(productId);

        this.productRepository.save(product);
        return ProductResponse.of(product);
    }

    private void validateProductDataInformed(ProductRequest productRequest) {
        if (ObjectUtils.isEmpty(productRequest.getName()))
            throw new ValidationException("The product's name was not informed.");

        if (ObjectUtils.isEmpty(productRequest.getQuantityAvailable()))
            throw new ValidationException("The product's quantity was not informed.");

        if (productRequest.getQuantityAvailable() <= ZERO)
            throw new ValidationException("The quantity should not be less or equal to zero.");
    }

    private void validateCategoryAndSupplierIdInformed(ProductRequest productRequest) {
        if (ObjectUtils.isEmpty(productRequest.getCategoryId()))
            throw new ValidationException("The category ID was not informed.");

        if (ObjectUtils.isEmpty(productRequest.getSupplierId()))
            throw new ValidationException("The supplier ID was not informed.");
    }

    public SuccessResponse delete(Integer productId) {
        this.validateInformedId(productId);

        this.productRepository.deleteById(productId);
        return SuccessResponse.create("The product was deleted.");
    }

    private void validateInformedId(Integer productId) {
        if (ObjectUtils.isEmpty(productId))
            throw new ValidationException("The product ID must be informed.");
    }
}
