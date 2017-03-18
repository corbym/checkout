package com.corbym.checkout.domain;

public class StockUnitPriceRule {

    private final String itemId;
    private final long unitPrice;

    public StockUnitPriceRule(String itemId, long unitPriceInPence) {
        this.itemId = itemId;
        this.unitPrice = unitPriceInPence;
    }

    public long unitPrice() {
        return unitPrice;
    }

    public String getItemId() {
        return itemId;
    }
}
