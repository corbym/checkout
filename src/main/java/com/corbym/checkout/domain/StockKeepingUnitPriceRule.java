package com.corbym.checkout.domain;

public class StockKeepingUnitPriceRule {

    private final String stockKeepingUnit;
    private final long unitPrice;
    private final DiscountRule discountRule;

    public StockKeepingUnitPriceRule(String stockKeepingUnit, long unitPriceInPence) {
       this(stockKeepingUnit, unitPriceInPence, null);
    }

    public StockKeepingUnitPriceRule(String stockKeepingUnit, long unitPriceInPence, DiscountRule discountRule) {
        this.stockKeepingUnit = stockKeepingUnit;
        this.unitPrice = unitPriceInPence;
        this.discountRule = discountRule;
    }

    public long unitPrice() {
        return unitPrice;
    }

    public String getStockKeepingUnit() {
        return stockKeepingUnit;
    }

    public DiscountRule getDiscountRule() {
        return discountRule;
    }
}
