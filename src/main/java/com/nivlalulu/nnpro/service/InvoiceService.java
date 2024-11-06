package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dao.InvoiceRepository;
import com.nivlalulu.nnpro.dto.InvoiceDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import com.nivlalulu.nnpro.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingService mappingService;


    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        List<Product> productList = invoiceDto.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toList());
        Optional<User> user = userService.findById(invoiceDto.getId());
        Invoice invoice = new Invoice(invoiceDto.getCompanyName(), invoiceDto.getCompanyId(), invoiceDto.getTaxId(),
                invoiceDto.getCreated(), invoiceDto.getExpiration(), productList, user.orElse(null));
        return mappingService.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated) {
        List<Product> productList = invoiceUpdated.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toList());

        Invoice invoice = invoiceRepository.findById(invoiceUpdated.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Invoice with id %s doesn't exist, can't be updated",
                        invoiceUpdated.getId())));

        invoice.setCompanyId(invoiceUpdated.getCompanyId());
        invoice.setProductList(productList);
        invoice.setTaxId(invoiceUpdated.getTaxId());
        invoice.setCompanyName(invoiceUpdated.getCompanyName());
        invoice.setExpiration(invoiceUpdated.getExpiration());

        return mappingService.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto deleteInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Invoice with id %s doesn't exist", id)));

        invoiceRepository.delete(invoice);
        return mappingService.convertToDto(invoice);
    }

    public InvoiceDto findInvoivceDtoById(UUID id) {
        return mappingService.convertToDto(invoiceRepository.findById(id).orElse(null));
    }

    protected Optional<Invoice> findInvoiceById(UUID id) {
        return invoiceRepository.findById(id);
    }

    public List<InvoiceDto> findAllByCompanyId(String companyId) {
        return invoiceRepository.findAllByCompanyId(companyId).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceDto> findAllByCompanyName(String companyName) {
        return invoiceRepository.findAllByCompanyName(companyName).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceDto> findAllByTaxId(String taxId) {
        return invoiceRepository.findAllByTaxId(taxId).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceDto> findAllUserInvoices(UUID userId) {
        return invoiceRepository.findAllByUser(userId).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceDto> findAllInvoices() {
        return invoiceRepository.findAll().stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<Invoice> findAllContainsProduct(Product product){
        return invoiceRepository.findAllByProductListContains(product);
    }

}
