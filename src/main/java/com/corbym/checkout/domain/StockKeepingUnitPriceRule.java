package com.corbym.checkout.domain;

public class StockKeepingUnitPriceRule {

    private final StockKeepingUnit stockKeepingUnit;
    private final DiscountRule discountRule;

    private final long unitPriceInPence;

    public StockKeepingUnitPriceRule(StockKeepingUnit keepingUnit, long unitPriceInPence) {
       this(keepingUnit, unitPriceInPence, null);
    }

    public StockKeepingUnitPriceRule(StockKeepingUnit stockKeepingUnit, long unitPriceInPence, DiscountRule discountRule) {
        this.stockKeepingUnit = stockKeepingUnit;
        this.unitPriceInPence = unitPriceInPence;
        this.discountRule = discountRule;
    }

    public long getUnitPriceInPence() {
        return unitPriceInPence;
    }

    public StockKeepingUnit getStockKeepingUnit() {
        return stockKeepingUnit;
    }

    public DiscountRule getDiscountRule() {
        return discountRule;
    }
}
