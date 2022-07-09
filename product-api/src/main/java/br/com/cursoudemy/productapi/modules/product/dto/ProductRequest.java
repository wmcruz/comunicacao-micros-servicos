package br.com.cursoudemy.productapi.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductRequest {

    private String name;
    @JsonProperty("quantity_available")
    private Integer quantityAvailable;
    @JsonProperty("category_id")
    private Integer categoryId;
    @JsonProperty("supplier_id")
    private Integer supplierId;
}
