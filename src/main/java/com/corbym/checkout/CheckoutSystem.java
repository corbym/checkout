package com.corbym.checkout;


import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockKeepingUnitPriceRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is not thread safe, the shopping list is stateful.
 * Create a new instance of this class per transaction / thread.
 */
public class CheckoutSystem {

    private final Map<String, StockKeepingUnitPriceRule> stockUnitPricingRules;

    private final List<String> shoppingList = new ArrayList<>();

    public CheckoutSystem(List<StockKeepingUnitPriceRule> stockKeepingUnitPricingRules) {
        this.stockUnitPricingRules = convertPricingRulesToMap(stockKeepingUnitPricingRules);
    }

    /**
     * Scans one stockKeepingUnit, and adds it to a shopping list.
     *
     * @param stockKeepingUnit - the item's SKU identifier
     */
    public void scanItem(String stockKeepingUnit) {
        if (!stockUnitPricingRules.containsKey(stockKeepingUnit)) {
            throw new IllegalArgumentException("Unknown SKU specified.");
        }
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
                .mapToLong(StockKeepingUnitPriceRule::unitPrice)
                .sum();
    }

    private long calculateTotalDiscount() {
        Map<String, Long> countOfShoppingListGroupedBySkus = shoppingList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return countOfShoppingListGroupedBySkus.keySet().stream()
                .mapToLong(sku -> {
                    StockKeepingUnitPriceRule stockKeepingUnitPriceRule = stockUnitPricingRules.get(sku);
                    DiscountRule discountRule = stockKeepingUnitPriceRule.getDiscountRule();
                    return discountRule == null ? 0 : discountRule.getTotalDiscount(countOfShoppingListGroupedBySkus.get(sku));
                }).sum();
    }

    private Map<String, StockKeepingUnitPriceRule> convertPricingRulesToMap(List<StockKeepingUnitPriceRule> stockUnitPricingRules) {
        Map<String, StockKeepingUnitPriceRule> stockUnitPriceRuleMap = new HashMap<>();
        for (StockKeepingUnitPriceRule stockUnitPricingRule : stockUnitPricingRules) {
            stockUnitPriceRuleMap.put(stockUnitPricingRule.getStockKeepingUnit(), stockUnitPricingRule);
        }
        return stockUnitPriceRuleMap;
    }
}
