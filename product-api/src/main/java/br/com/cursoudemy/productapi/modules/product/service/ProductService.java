package br.com.cursoudemy.productapi.modules.product.service;

import br.com.cursoudemy.productapi.config.exception.SuccessResponse;
import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.product.dto.ProductCheckStockRequest;
import br.com.cursoudemy.productapi.modules.product.dto.ProductQuantityDTO;
import br.com.cursoudemy.productapi.modules.product.dto.ProductRequest;
import br.com.cursoudemy.productapi.modules.product.dto.ProductResponse;
import br.com.cursoudemy.productapi.modules.product.dto.ProductSalesResponse;
import br.com.cursoudemy.productapi.modules.product.dto.ProductStockDTO;
import br.com.cursoudemy.productapi.modules.product.model.Product;
import br.com.cursoudemy.productapi.modules.product.repository.ProductRepository;
import br.com.cursoudemy.productapi.modules.sales.client.SalesClient;
import br.com.cursoudemy.productapi.modules.sales.dto.SalesConfirmationDTO;
import br.com.cursoudemy.productapi.modules.sales.enums.SalesStatus;
import br.com.cursoudemy.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.cursoudemy.productapi.modules.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SalesConfirmationSender salesConfirmationSender;

    @Autowired
    private SalesClient salesClient;

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

    public void updateProductStock(ProductStockDTO product) {
        try {
            this.validateStockUpdateData(product);
            this.updateStock(product);
        } catch (Exception exception) {
            log.error("Error while trying to update stock for message with error: {}", exception.getMessage(), exception);

            var rejectedMessaege = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.REJECTED);
            this.salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessaege);
        }
    }
    @Transactional
    private void validateStockUpdateData(ProductStockDTO product) {
        if (ObjectUtils.isEmpty(product) || ObjectUtils.isEmpty(product.getSalesId()))
            throw new ValidationException("The Product data and the sales ID must be informed.");

        if (ObjectUtils.isEmpty(product.getProducts()))
            throw new ValidationException("The sales' products must be informed.");

        product
            .getProducts()
            .forEach(salesProduct -> {
                if (ObjectUtils.isEmpty(salesProduct.getQuantity()) || ObjectUtils.isEmpty(salesProduct.getProductId()))
                    throw new ValidationException("The productID and the quantity must be informed.");
            });
    }

    private void updateStock(ProductStockDTO product) {
        var productsForUpdate = new ArrayList<Product>();
        product
            .getProducts()
            .forEach(salesProduct -> {
                var existingProduct = this.findById(salesProduct.getProductId());
                this.validateQuantityInStock(salesProduct, existingProduct);

                existingProduct.updateStock(salesProduct.getQuantity());
                productsForUpdate.add(existingProduct);
            });

        if (!ObjectUtils.isEmpty(productsForUpdate)) {
            this.productRepository.saveAll(productsForUpdate);

            var approvedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.APROVED);
            this.salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }
    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct, Product existingProduct) {
        if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable())
            throw new ValidationException(String.format("The product %s is out of stock.", existingProduct.getId()));
    }

    public ProductSalesResponse findProductSales(Integer productId) {
        var product = this.findById(productId);

        try {
            var sales = this.salesClient
                    .findSalesByProductId(product.getId())
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product."));

            return ProductSalesResponse.of(product, sales.getSalesId());
        } catch (Exception exception) {
            throw new ValidationException("There was an error trying to get the product's sales.");
        }
    }

    public SuccessResponse checkProductStock(ProductCheckStockRequest request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getProducts()))
            throw new ValidationException("The request data and products must be informed.");

        request
            .getProducts()
            .forEach(this::validateStock);

        return SuccessResponse.create("The stock is ok!");
    }

    private void validateStock(ProductQuantityDTO productQuantity) {
        if (ObjectUtils.isEmpty(productQuantity.getProductId()) || ObjectUtils.isEmpty(productQuantity.getQuantity()))
            throw new ValidationException("Product ID and quantity must be informed.");

        var product = findById(productQuantity.getProductId());
        if (productQuantity.getQuantity() > product.getQuantityAvailable())
            throw new ValidationException(String.format("The product %s is out of stock.", product.getId()));
    }
}
