package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.InvoiceDto;
import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MappingService {

    private static ModelMapper modelMapper;

    @Autowired
    public MappingService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public static ProductDto convertToDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    public static InvoiceDto convertToDto(Invoice invoice) {
        return modelMapper.map(invoice, InvoiceDto.class);
    }

    public static Product convertToEntity(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }

    public static Invoice convertToEntity(InvoiceDto invoiceDto) {
        return modelMapper.map(invoiceDto, Invoice.class);
    }


}
