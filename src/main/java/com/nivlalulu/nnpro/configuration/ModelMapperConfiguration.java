package com.nivlalulu.nnpro.configuration;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.PartySnapshotDto;
import com.nivlalulu.nnpro.model.Invoice;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper(){
        var mapper = new ModelMapper();
        MapInvoiceToInvoiceDto(mapper);
        MapInvoiceDtoToInvoice(mapper);
        return mapper;
    }

    private static void MapInvoiceToInvoiceDto(ModelMapper mapper) {
        TypeMap<Invoice, InvoiceDto> typeMap = mapper.emptyTypeMap(Invoice.class, InvoiceDto.class);
        typeMap.addMappings(m -> {
            m.map(Invoice::getItems, InvoiceDto::setItems);
            m.skip(InvoiceDto::setSupplier);
            m.skip(InvoiceDto::setCustomer);
        });
        typeMap.implicitMappings();
        typeMap.setPostConverter(ctx -> {
            Invoice source = ctx.getSource();
            InvoiceDto destination = ctx.getDestination();

            PartySnapshotDto supplier = new PartySnapshotDto(
                    source.getSupplierName(),
                    source.getSupplierAddress(),
                    source.getSupplierCountry(),
                    source.getSupplierIcTax(),
                    source.getSupplierDicTax(),
                    source.getSupplierTelephone(),
                    source.getSupplierEmail()
            );
            destination.setSupplier(supplier);

            PartySnapshotDto customer = new PartySnapshotDto(
                    source.getCustomerName(),
                    source.getCustomerAddress(),
                    source.getCustomerCountry(),
                    source.getCustomerIcTax(),
                    source.getCustomerDicTax(),
                    null,
                    null
            );
            destination.setCustomer(customer);

            return destination;
        });
    }

    private static void MapInvoiceDtoToInvoice(ModelMapper mapper) {
       TypeMap<InvoiceDto, Invoice> typeMap = mapper.emptyTypeMap(InvoiceDto.class, Invoice.class);
       typeMap.addMappings(m -> {
           m.skip(Invoice::setSupplier);
           m.skip(Invoice::setCustomer);
       });
       typeMap.implicitMappings();
       typeMap.setPostConverter(ctx -> {
           InvoiceDto source = ctx.getSource();
           Invoice destination = ctx.getDestination();

           PartySnapshotDto supplier = source.getSupplier();
           if (supplier != null) {
               destination.setSupplierName(supplier.getName());
               destination.setSupplierAddress(supplier.getAddress());
               destination.setSupplierCountry(supplier.getCountry());
               destination.setSupplierIcTax(supplier.getIcTax());
               destination.setSupplierDicTax(supplier.getDicTax());
               destination.setSupplierTelephone(supplier.getTelephone());
               destination.setSupplierEmail(supplier.getEmail());
           }

           PartySnapshotDto customer = source.getCustomer();
           if (customer != null) {
               destination.setCustomerName(customer.getName());
               destination.setCustomerAddress(customer.getAddress());
               destination.setCustomerCountry(customer.getCountry());
               destination.setCustomerIcTax(customer.getIcTax());
               destination.setCustomerDicTax(customer.getDicTax());
           }

           return destination;
       });
    }

}
