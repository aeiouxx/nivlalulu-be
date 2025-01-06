package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivlalulu.nnpro.enums.PaymentMethod;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.specification.InvoiceSpecification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.BindParam;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Data
@Schema(
        description = "Search criteria for invoices, used to filter invoices, performs LIKE matching on string fields")
public class InvoiceSearchDto {

    public Specification<Invoice> toSpecification(User user) {
        Specification<Invoice> specification = Specification.where(null);
        if (id != null) {
            specification = specification.and(InvoiceSpecification.hasId(id));
        }
        if (variableSymbol != null) {
            specification = specification.and(InvoiceSpecification.hasVariableSymbol(variableSymbol));
        }
        if (supplierName != null) {
            specification = specification.and(InvoiceSpecification.hasSupplierNameLike(supplierName));
        }
        if (customerName != null) {
            specification = specification.and(InvoiceSpecification.hasCustomerNameLike(customerName));
        }
        if (paymentMethod != null) {
            specification = specification.and(InvoiceSpecification.hasPaymentMethod(paymentMethod));
        }
        if (createdAtStart != null || createdAtEnd != null) {
            specification = specification.and(InvoiceSpecification.createdBetween(createdAtStart, createdAtEnd));
        }
        if (expiresAtStart != null || expiresAtEnd != null) {
            specification = specification.and(InvoiceSpecification.expiresBetween(expiresAtStart, expiresAtEnd));
        }
        if (contact != null) {
            specification = specification.and(InvoiceSpecification.hasContactLike(contact));
        }

        if (itemNames != null && !itemNames.isEmpty()) {
            specification = specification.and(InvoiceSpecification.anyItemNamesLike(itemNames));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"), user));
        return specification;
    }

    @Schema(
            description = "Unique identifier of the invoice",
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(
            description = "Variable symbol",
            example = "123456")
    @BindParam("variable_symbol")
    private String variableSymbol;

    @Schema(
            description = "Search for invoices with this supplier name",
            example = "John Doe")
    @BindParam("supplier_name")
    private String supplierName;

    @Schema(
            description = "Search for invoices with this customer name",
            example = "Customer One")
    @BindParam("customer_name")
    private String customerName;

    @BindParam("payment_method")
    @Schema(description = "Payment method", example = "P")
    private PaymentMethod paymentMethod;

    @Schema(
            description = "Search for invoices created after this date (inclusive), " +
                    "if null and `created_at_end` is specified," +
                    "will match all invoices created before the end date (inclusive)",
            example = "2021-01-01T00:00:00Z")
    @BindParam("created_at_start")
    private Instant createdAtStart;

    @Schema(
            description = "Search for invoices created before this date (inclusive), " +
                    "if null and `created_at_start` is specified," +
                    "will match all invoices created after the start date (inclusive)",
            example = "2021-01-01T00:00:00Z")
    @BindParam("created_at_end")
    private Instant createdAtEnd;


    @Schema(
            description = "Search for invoices expiring after this date (inclusive), " +
                    "if null and `expires_at_end` is specified," +
                    "will match all invoices expiring before the end date (inclusive)",
            example = "2021-01-01T00:00:00Z")
    @BindParam("expires_at_start")
    private Instant expiresAtStart;

    @Schema(
            description = "Search for invoices expiring after this date (inclusive), " +
                    "if null and `expires_at_start` is specified," +
                    "will match all invoices expiring after the start date (inclusive)",
            example = "2021-01-01T00:00:00Z")
    @BindParam("expires_at_end")
    private Instant expiresAtEnd;

    @Schema(
            description = "Search for invoice whose contact information contains this string",
            example = "John Doe")
    private String contact;

    @Schema(
            description = "Search for invoices with items containing any of these names, meaning a single match suffices",
            example = "Product One")
    @BindParam("item_names")
    private List<String> itemNames;
}
