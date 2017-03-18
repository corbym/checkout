package com.corbym.checkout.domain;

public class DiscountRule {

    private static final long LOWEST_DISCOUNT_TRIGGER_AMOUNT = 0L;

    private final long discountTriggerAmount;
    private final long discountInPence;

    public DiscountRule(long discountTriggerAmount, long discountInPence) {
        this.discountTriggerAmount = discountTriggerAmount <= LOWEST_DISCOUNT_TRIGGER_AMOUNT ? 1 : discountTriggerAmount;
        this.discountInPence = discountInPence;
    }

    public long getTotalDiscount(Long numberOfItems) {
        return discountInPence * (numberOfItems / discountTriggerAmount);
    }
}
