package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.ProductDto;
import com.nivlalulu.nnpro.dto.UserDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.User;
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

    public static ProductDto convertToDto(InvoiceItem invoiceItem) {
        return modelMapper.map(invoiceItem, ProductDto.class);
    }

    public static UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public static InvoiceDto convertToDto(Invoice invoice) {
        return modelMapper.map(invoice, InvoiceDto.class);
    }

    public static InvoiceItem convertToEntity(ProductDto productDto) {
        return modelMapper.map(productDto, InvoiceItem.class);
    }

    public static Invoice convertToEntity(InvoiceDto invoiceDto) {
        return modelMapper.map(invoiceDto, Invoice.class);
    }

    public static User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }


}
