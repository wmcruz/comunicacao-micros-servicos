package br.com.cursoudemy.productapi.modules.supplier.service;

import br.com.cursoudemy.productapi.config.exception.SuccessResponse;
import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.modules.product.service.ProductService;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierRequest;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierResponse;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import br.com.cursoudemy.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductService productService;

    public Supplier findById(Integer id) {
        this.validateInformedId(id);

        return this.supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no supplier for the given ID."));
    }

    public SupplierResponse findByIdResponse(Integer id) {
        return SupplierResponse.of(this.findById(id));
    }

    public List<SupplierResponse> findByName(String name) {
        this.validateSupplierNameInformed(name);

        return this.supplierRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findAll() {
        return this.supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse save(SupplierRequest request) {
        this.validateSupplierNameInformed(request.getName());

        var supplier = this.supplierRepository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest supplierRequest, Integer supplierId) {
        this.validateSupplierNameInformed(supplierRequest.getName());
        this.validateInformedId(supplierId);

        var supplier = Supplier.of(supplierRequest);
        supplier.setId(supplierId);

        this.supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public SuccessResponse delete(Integer supplierId) {
        this.validateInformedId(supplierId);

        if (this.productService.existsBySupplierId(supplierId))
            throw new ValidationException("You cannot delete this supplier because it's already defined by product.");

        this.supplierRepository.deleteById(supplierId);
        return SuccessResponse.create("The supplier was deleted.");
    }

    private void validateSupplierNameInformed(String name) {
        if (ObjectUtils.isEmpty(name)) {
            throw new ValidationException("The supplier's name was not informed.");
        }
    }

    private void validateInformedId(Integer supplierId) {
        if (ObjectUtils.isEmpty(supplierId))
            throw new ValidationException("The supplier ID must be informed.");
    }
}
