package com.corbym.checkout.domain;

public class DiscountRule {

    private final long discountTriggerAmount;
    private final long discountInPence;

    public DiscountRule(long discountTriggerAmount, long discountInPence) {
        this.discountTriggerAmount = discountTriggerAmount;
        this.discountInPence = discountInPence;
    }

    public long getTotalDiscount(Long numberOfItems) {
        return discountInPence * (numberOfItems / discountTriggerAmount);
    }
}
