package com.nivlalulu.nnpro.utils;

import java.math.BigDecimal;

public class CalculationModule {

    private final BigDecimal TAX_RATE = new BigDecimal(1.21);

    public BigDecimal calculateTaxPriceProduct(BigDecimal rawPrice) {
        return rawPrice.multiply(TAX_RATE);
    }

    public BigDecimal totalPriceProduct(BigDecimal raw, BigDecimal tax) {
        return raw.add(tax);
    }

}
