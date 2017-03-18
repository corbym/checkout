package com.corbym.checkout.domain;

public class StockUnitPriceRule {

    private final String stockKeepingUnit;
    private final long unitPrice;

    public StockUnitPriceRule(String stockKeepingUnit, long unitPriceInPence) {
        this.stockKeepingUnit = stockKeepingUnit;
        this.unitPrice = unitPriceInPence;
    }

    public long unitPrice() {
        return unitPrice;
    }

    public String getStockKeepingUnit() {
        return stockKeepingUnit;
    }
}
