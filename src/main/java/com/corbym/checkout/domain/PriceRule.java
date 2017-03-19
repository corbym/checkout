package com.corbym.checkout.domain;

import java.util.Optional;

public class PriceRule {

    private final StockKeepingUnit stockKeepingUnit;
    private final DiscountRule discountRule;

    private final long unitPriceInPence;

    public PriceRule(StockKeepingUnit stockKeepingUnit, long unitPriceInPence) {
       this(stockKeepingUnit, unitPriceInPence, null);
    }

    public PriceRule(StockKeepingUnit stockKeepingUnit, long unitPriceInPence, DiscountRule discountRule) {
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

    public Optional<DiscountRule> getDiscountRule() {
        return Optional.ofNullable(discountRule);
    }
}
