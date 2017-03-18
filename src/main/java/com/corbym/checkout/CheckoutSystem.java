package com.corbym.checkout;


import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockUnitPriceRule;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * This class is not thread safe. Create a new instance of this class
 * per transaction / thread.
 *
 */
public class CheckoutSystem {

    private final Map<String, StockUnitPriceRule> stockUnitPricingRules;
    private final Map<String, DiscountRule> discountRules;

    private final List<String> shoppingList = new ArrayList<>();

    public CheckoutSystem(List<StockUnitPriceRule> stockUnitPricingRules) {
        this(stockUnitPricingRules, emptyList());
    }

    public CheckoutSystem(List<StockUnitPriceRule> stockUnitPricingRules,
                          List<DiscountRule> discountRule) {
        this.stockUnitPricingRules = convertPricingRulesToMap(stockUnitPricingRules);
        this.discountRules = convertDiscountRulesToMap(discountRule);
    }

    /**
     * Scans one stockKeepingUnit, and adds it to a shopping list.
     *
     * @param stockKeepingUnit - the item's SKU identifier
     */
    public void scanItem(String stockKeepingUnit) {
        shoppingList.add(stockKeepingUnit);
    }

    /**
     * Calculates the total amount in the shopping list so far.
     *
     * @return the total amount in whole pence
     */
    public long calculateTotalCostInPence() {
        return calculateGrossAmountBeforeDiscount() - calculateTotalDiscount();
    }

    private long calculateGrossAmountBeforeDiscount() {
        return shoppingList.stream()
                .map(stockUnitPricingRules::get)
                .mapToLong(StockUnitPriceRule::unitPrice)
                .sum();
    }

    private long calculateTotalDiscount() {
        Map<String, Long> countOfUniqueSkus = shoppingList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return countOfUniqueSkus.keySet().stream()
                .map(discountRules::get)
                .filter(Objects::nonNull)
                .mapToLong((discountRule) -> {
                    String itemId = discountRule.getStockKeepingUnit();
                    return discountRule.getTotalDiscount(countOfUniqueSkus.get(itemId));
                }).sum();
    }

    private Map<String, StockUnitPriceRule> convertPricingRulesToMap(List<StockUnitPriceRule> stockUnitPricingRules) {
        Map<String, StockUnitPriceRule> stockUnitPriceRuleMap = new HashMap<>();
        for (StockUnitPriceRule stockUnitPricingRule : stockUnitPricingRules) {
            stockUnitPriceRuleMap.put(stockUnitPricingRule.getStockKeepingUnit(), stockUnitPricingRule);
        }
        return stockUnitPriceRuleMap;
    }

    private Map<String, DiscountRule> convertDiscountRulesToMap(List<DiscountRule> discountRule) {
        Map<String, DiscountRule> discountRuleMap = new HashMap<>();
        for (DiscountRule stockUnitPricingRule : discountRule) {
            discountRuleMap.put(stockUnitPricingRule.getStockKeepingUnit(), stockUnitPricingRule);
        }
        return discountRuleMap;
    }
}
