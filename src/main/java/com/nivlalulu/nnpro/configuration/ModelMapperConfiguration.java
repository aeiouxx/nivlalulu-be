package com.nivlalulu.nnpro.configuration;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.v1.PartySnapshotDto;
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
        MapInvoiceItemToInvoiceItemDto(mapper);
        MapInvoiceToInvoiceDto(mapper);
        MapInvoiceDtoToInvoice(mapper);
        return mapper;
    }

    private static void MapInvoiceItemToInvoiceItemDto(ModelMapper mapper) {
        mapper.typeMap(InvoiceItem.class, InvoiceItemDto.class);
    }

    private static void MapInvoiceToInvoiceDto(ModelMapper mapper) {
        mapper.typeMap(Invoice.class, InvoiceDto.class).addMappings(m -> {
            m.map(Invoice::getItems, InvoiceDto::setItems);
            m.map(src -> new PartySnapshotDto(
                    src.getSupplierName(),
                    src.getSupplierAddress(),
                    src.getSupplierCountry(),
                    src.getSupplierIcTax(),
                    src.getSupplierDicTax(),
                    src.getSupplierTelephone(),
                    src.getSupplierEmail()
            ), InvoiceDto::setSupplier);
            m.map(src -> new PartySnapshotDto(
                    src.getCustomerName(),
                    src.getCustomerAddress(),
                    src.getCustomerCountry(),
                    src.getCustomerIcTax(),
                    src.getCustomerDicTax(),
                    null,
                    null
            ), InvoiceDto::setCustomer);
        });
    }

    private static void MapInvoiceDtoToInvoice(ModelMapper mapper) {
        mapper.typeMap(InvoiceDto.class, Invoice.class).addMappings(m -> {
            m.map(InvoiceDto::getSupplier, (dest, value) -> {
                if (value instanceof PartySnapshotDto snapshot) {
                    dest.setSupplierName(snapshot.name());
                    dest.setSupplierAddress(snapshot.address());
                    dest.setSupplierCountry(snapshot.country());
                    dest.setSupplierIcTax(snapshot.icTax());
                    dest.setSupplierDicTax(snapshot.dicTax());
                    dest.setSupplierTelephone(snapshot.telephone());
                    dest.setSupplierEmail(snapshot.email());
                }
            });
            m.map(InvoiceDto::getCustomer, (dest, value) -> {
                if (value instanceof PartySnapshotDto snapshot) {
                    dest.setCustomerName(snapshot.name());
                    dest.setCustomerAddress(snapshot.address());
                    dest.setCustomerCountry(snapshot.country());
                    dest.setCustomerIcTax(snapshot.icTax());
                    dest.setCustomerDicTax(snapshot.dicTax());
                }
            });
        });
    }

}
