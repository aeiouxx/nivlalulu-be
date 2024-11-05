package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dao.InvoiceRepository;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;


    public Invoice createInvoice(Invoice invoiceDto) {
        Invoice invoice = new Invoice(invoiceDto.getCompanyName(), invoiceDto.getCompanyId(), invoiceDto.getTaxId(),
                invoiceDto.getCreated(), invoiceDto.getExpiration(), invoiceDto.getProductList(), invoiceDto.getUser());
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Invoice invoiceUpdated) {
        Invoice invoice = invoiceRepository.findById(invoiceUpdated.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Invoice with id %s doesn't exist, can't be updated",
                        invoiceUpdated.getId())));

        invoice.setCompanyId(invoiceUpdated.getCompanyId());
        invoice.setProductList(invoiceUpdated.getProductList());
        invoice.setTaxId(invoiceUpdated.getTaxId());
        invoice.setCompanyName(invoiceUpdated.getCompanyName());
        invoice.setExpiration(invoiceUpdated.getExpiration());

        return invoiceRepository.save(invoiceUpdated);
    }

    public Invoice deleteInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Invoice with id %s doesn't exist", id)));

        invoiceRepository.delete(invoice);
        return invoice;
    }

    public List<Invoice> findAllByCompanyId(String companyId) {
        return invoiceRepository.findAllByCompanyId(companyId);
    }

    public List<Invoice> findAllByCompanyName(String companyName) {
        return invoiceRepository.findAllByCompanyName(companyName);
    }

    public List<Invoice> findAllByTaxId(String taxId) {
        return invoiceRepository.findAllByTaxId(taxId);
    }

    public List<Invoice> findAllUserInvoices(UUID userId) {
        return invoiceRepository.findAllByUser(userId);
    }

    public List<Invoice> findAllInvoices() {
        return invoiceRepository.findAll();
    }

}
