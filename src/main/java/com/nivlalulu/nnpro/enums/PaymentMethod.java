package com.nivlalulu.nnpro.enums;

public enum PaymentMethod {
    P("Převodem na účet"),
    D("Hotově při převzetí");

    private String text;

    PaymentMethod(String text) {
        this.text = text;
    }
}
