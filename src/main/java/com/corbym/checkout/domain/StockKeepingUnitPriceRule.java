package com.corbym.checkout.domain;

public class StockKeepingUnitPriceRule {

    private final String stockKeepingUnit;
    private final DiscountRule discountRule;

    private final long unitPriceInPence;

    public StockKeepingUnitPriceRule(String stockKeepingUnit, long unitPriceInPence) {
       this(stockKeepingUnit, unitPriceInPence, null);
    }

    public StockKeepingUnitPriceRule(String stockKeepingUnit, long unitPriceInPence, DiscountRule discountRule) {
        this.stockKeepingUnit = stockKeepingUnit;
        this.unitPriceInPence = unitPriceInPence;
        this.discountRule = discountRule;
    }

    public long getUnitPriceInPence() {
        return unitPriceInPence;
    }

    public String getStockKeepingUnit() {
        return stockKeepingUnit;
    }

    public DiscountRule getDiscountRule() {
        return discountRule;
    }
}
