package com.corbym.checkout.domain;

public class DiscountRule {
    private final String stockKeepingUnit;

    private final long discountTriggerAmount;
    private final long discountInPence;

    public DiscountRule(String stockKeepingUnit, long discountTriggerAmount, long discountInPence) {
        this.stockKeepingUnit = stockKeepingUnit;
        this.discountTriggerAmount = discountTriggerAmount;
        this.discountInPence = discountInPence;
    }

    public long getDiscountInPence() {
        return discountInPence;
    }

    public String getStockKeepingUnit() {
        return stockKeepingUnit;
    }

    public long getTotalDiscount(Long numberOfItems) {
        return discountInPence * (numberOfItems / discountTriggerAmount);
    }
}
