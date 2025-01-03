package com.nivlalulu.nnpro.configuration;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper(){
        var mapper = new ModelMapper();
        mapper.typeMap(InvoiceItem.class, InvoiceItemDto.class);
        mapper.typeMap(Invoice.class, InvoiceDto.class)
                .addMappings(m -> m.map(Invoice::getItems, InvoiceDto::setItems));
        return mapper;
    }
}
