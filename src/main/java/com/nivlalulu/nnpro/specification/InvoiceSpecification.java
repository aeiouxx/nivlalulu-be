package com.nivlalulu.nnpro.specification;

import com.nivlalulu.nnpro.enums.PaymentMethod;
import com.nivlalulu.nnpro.model.Invoice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class InvoiceSpecification {

    public static Specification<Invoice> hasId(UUID id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<Invoice> hasVariableSymbol(String variableSymbol) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("variableSymbol"), variableSymbol);
    }

    public static Specification<Invoice> hasSupplierNameLike(String supplierName) {
        return (r, q, c) -> {
            return c.like(c.lower(r.get("supplierName")), "%" + supplierName.toLowerCase() + "%");
        };
    }

    public static Specification<Invoice> hasCustomerNameLike(String customerName) {
        return (r, q, c) -> {
            return c.like(c.lower(r.get("customerName")), "%" + customerName.toLowerCase() + "%");
        };
    }

    public static Specification<Invoice> hasPaymentMethod(PaymentMethod paymentMethod) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paymentMethod"), paymentMethod);
    }

    /**
     * Filter invoices by creation date, if the start date is null, it will be ignored.
     * If the end date is null, it will be ignored.
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Specification<Invoice> createdBetween(Instant startDate, Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            } else {
                return null;
            }
        };
    }

    /**
     * Filter invoices by expirtaion date, if the start date is null, it will be ignored.
     * If the end date is null, it will be ignored.
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Specification<Invoice> expiresBetween(Instant startDate, Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("expiresAt"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("expiresAt"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("expiresAt"), endDate);
            } else {
                return null;
            }
        };
    }

    public static Specification<Invoice> hasContactLike(String contact) {
        return (r, q, c) -> {
            return c.like(c.lower(r.get("contact")), "%" + contact.toLowerCase() + "%");
        };
    }

    public static Specification<Invoice> anyItemNamesLike(List<String> itemNames) {
        return (root, query, criteriaBuilder) -> {
            if (itemNames == null || itemNames.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filter applied
            }
            query.distinct(true);

            var itemJoin = root.join("items");

            List<String> lowerCaseItemNames = itemNames.stream()
                    .map(String::toLowerCase)
                    .toList();

            return criteriaBuilder.or(
                    lowerCaseItemNames.stream()
                            .map(itemName -> criteriaBuilder.like(
                                    criteriaBuilder.lower(itemJoin.get("name")), "%" + itemName + "%"))
                            .toArray(Predicate[]::new)
            );
        };
    }
}
