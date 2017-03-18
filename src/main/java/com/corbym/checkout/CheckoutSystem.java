package com.corbym.checkout;


import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockKeepingUnitPriceRule;

import java.util.*;

import static java.util.Collections.synchronizedList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * This class is stateful, and maintains a list of shopping items between calls.
 *
 * Create a new instance of this class per transaction.
 *
 * This class is threadsafe between scanItem and calculateTotalCostInPence.
 *
 */
public class CheckoutSystem {

    private final Map<String, StockKeepingUnitPriceRule> stockUnitPricingRules;

    private final List<String> shoppingList = synchronizedList(new ArrayList<>());

    public CheckoutSystem(List<StockKeepingUnitPriceRule> stockKeepingUnitPricingRules) {
        this.stockUnitPricingRules = convertPricingRulesToMap(stockKeepingUnitPricingRules);
    }

    /**
     * Scans one stockKeepingUnit, and adds it to a shopping list.
     *
     * Calls to this are threadsafe between scanning and calculatingTotalCostInPence.
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
     * Removes discounts from the total, as per the given pricing rules.
     * This class is stateful, and will keep the shopping list between calls.
     *
     * Calls to this are threadsafe between scanning and calculatingTotalCostInPence.
     * @return the total amount in whole pence
     */
    public long calculateTotalCostInPence() {
        final ArrayList<String> shoppingListSnapshot = new ArrayList<>(shoppingList);
        return calculateGrossAmountBeforeDiscount(shoppingListSnapshot) - calculateTotalDiscount(shoppingListSnapshot);
    }

    private long calculateGrossAmountBeforeDiscount(final ArrayList<String> copyOfShoppingList) {
        return copyOfShoppingList.stream()
                .map(stockUnitPricingRules::get)
                .mapToLong(StockKeepingUnitPriceRule::getUnitPriceInPence)
                .sum();
    }

    private long calculateTotalDiscount(ArrayList<String> copyOfShoppingList) {
        Map<String, Long> countOfShoppingListGroupedBySkus = copyOfShoppingList.stream()
                .collect(groupingBy(identity(), counting()));

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
