package com.nivlalulu.nnpro.common.mapping.impl;

import com.nivlalulu.nnpro.common.mapping.IGenericMapper;
import com.nivlalulu.nnpro.dto.UserDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.ProductDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import com.nivlalulu.nnpro.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenericModelMapper implements IGenericMapper {
    private final ModelMapper modelMapper;
    // Sadly, because we don't have reified generics in java we can't do cool stuff
    // like this: collection.stream().map(mapper::map<DtoType, EntityType>)
    // instead we have to use: collection.stream().map(item -> mapper.map(item, EntityType.class))
    // because the infered type of the lambda is Object and not the actual type of the item Q_Q
    // and obviously polluting the interface with a bunch of methods like mapProductToDto, mapUserToDto, etc
    // is garbage
    /**
     * Maps a source object to a destination object
     * @param from
     * @param toClass
     * @return TTo
     */
    @Override
    public <TFrom, TTo> TTo map(TFrom from, Class<TTo> toClass) {
        return modelMapper.map(from, toClass);
    }

    public ProductDto convertToDto(Product product) {
        return map(product, ProductDto.class);
    }

    public UserDto convertToDto(User user) {
        return map(user, UserDto.class);
    }

    public InvoiceDto convertToDto(Invoice invoice) {
        return map(invoice, InvoiceDto.class);
    }

    public Product convertToEntity(ProductDto productDto) {
        return map(productDto, Product.class);
    }

    public Invoice convertToEntity(InvoiceDto invoiceDto) {
        return map(invoiceDto, Invoice.class);
    }

    public User convertToEntity(UserDto userDto) {
        return map(userDto, User.class);
    }
}
