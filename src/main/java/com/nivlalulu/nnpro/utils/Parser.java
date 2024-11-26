package com.nivlalulu.nnpro.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nivlalulu.nnpro.dto.InvoiceDto;

public class Parser {

    public String parseDtoToJson(InvoiceDto invoiceDto) {

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(invoiceDto);
            return json;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


}
