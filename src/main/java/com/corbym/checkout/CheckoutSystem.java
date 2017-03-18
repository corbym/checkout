package com.corbym.checkout;


import com.corbym.checkout.domain.StockUnitPriceRule;

import java.util.*;

public class CheckoutSystem {

    private Map<String, StockUnitPriceRule> stockUnitPricingRules;

    private List<String> shoppingList = new ArrayList<>();

    public CheckoutSystem(StockUnitPriceRule... stockUnitPricingRules) {
        this.stockUnitPricingRules = convertPricingRulesToMap(stockUnitPricingRules);
    }

    /**
     *
     * Scans one itemId, and adds it to a shopping list.
     *
     * @param itemId - the item's identifier
     */
    public void scanItem(String itemId) {
        shoppingList.add(itemId);
    }

    /**
     *
     * Calculates the total amount in the shopping list so far.
     *
     * @return the total amount in whole pence
     */
    public long calculateTotalCostInPence() {
        return shoppingList.stream()
                .map(stockUnitPricingRules::get)
                .mapToLong(StockUnitPriceRule::unitPrice)
                .sum();
    }

    private Map<String, StockUnitPriceRule> convertPricingRulesToMap(StockUnitPriceRule... stockUnitPricingRules) {
        Map<String, StockUnitPriceRule> stockUnitPriceRuleMap = new HashMap<>();
        for (StockUnitPriceRule stockUnitPricingRule : stockUnitPricingRules) {
            stockUnitPriceRuleMap.put(stockUnitPricingRule.getItemId(), stockUnitPricingRule);
        }
        return stockUnitPriceRuleMap;
    }

}
