package com.corbym.checkout;


import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockKeepingUnit;
import com.corbym.checkout.domain.StockKeepingUnitPriceRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.counting;

/**
 * This class is stateful, and maintains a list of shopping items between calls.
 * <p>
 * Create a new instance of this class per transaction.
 * <p>
 * This class is threadsafe between scanItem and calculateTotalCostInPence.
 */
public class CheckoutSystem {

    private final Map<StockKeepingUnit, StockKeepingUnitPriceRule> stockUnitPricingRules;

    private final List<StockKeepingUnit> shoppingList = synchronizedList(new ArrayList<>());

    public CheckoutSystem(List<StockKeepingUnitPriceRule> stockKeepingUnitPricingRules) {
        this.stockUnitPricingRules = convertPricingRulesToMap(stockKeepingUnitPricingRules);
    }

    /**
     * Scans one stockKeepingUnit, and adds it to a shopping list.
     * <p>
     * Calls to this are threadsafe between scanning and calculatingTotalCostInPence.
     *
     * @param stockKeepingUnit a stock keeping unit to scan
     */
    public void scanItem(StockKeepingUnit stockKeepingUnit) {
        if (!stockUnitPricingRules.containsKey(stockKeepingUnit)) {
            throw new IllegalArgumentException("Unknown SKU specified.");
        }
        shoppingList.add(stockKeepingUnit);
    }

    /**
     * Calculates the total amount in the shopping list so far.
     * <p>
     * Removes discounts from the total, as per the given pricing rules.
     * This class is stateful, and will keep the shopping list between calls.
     * <p>
     * Calls to this are threadsafe between scanning and calculatingTotalCostInPence.
     *
     * @return the total amount in whole pence
     */
    public long calculateTotalCostInPence() {
        final ArrayList<StockKeepingUnit> shoppingListSnapshot = new ArrayList<>(shoppingList);
        return calculateGrossAmountBeforeDiscount(shoppingListSnapshot) - calculateTotalDiscount(shoppingListSnapshot);
    }

    private long calculateGrossAmountBeforeDiscount(final ArrayList<StockKeepingUnit> copyOfShoppingList) {
        return copyOfShoppingList.stream()
                .map(stockUnitPricingRules::get)
                .mapToLong(StockKeepingUnitPriceRule::getUnitPriceInPence)
                .sum();
    }

    private long calculateTotalDiscount(ArrayList<StockKeepingUnit> copyOfShoppingList) {
        Map<StockKeepingUnit, Long> countOfShoppingListGroupedBySkus = copyOfShoppingList.stream()
                .collect(Collectors.groupingBy(Function.identity(), counting()));

        return countOfShoppingListGroupedBySkus.keySet().stream()
                .mapToLong(stockKeepingUnit -> {
                    StockKeepingUnitPriceRule stockKeepingUnitPriceRule = stockUnitPricingRules.get(stockKeepingUnit);
                    DiscountRule discountRule = stockKeepingUnitPriceRule.getDiscountRule();
                    return discountRule == null ? 0 : discountRule.getTotalDiscount(countOfShoppingListGroupedBySkus.get(stockKeepingUnit));
                }).sum();
    }

    private Map<StockKeepingUnit, StockKeepingUnitPriceRule> convertPricingRulesToMap(List<StockKeepingUnitPriceRule> stockUnitPricingRules) {
        Map<StockKeepingUnit, StockKeepingUnitPriceRule> stockUnitPriceRuleMap = new HashMap<>();
        for (StockKeepingUnitPriceRule stockUnitPricingRule : stockUnitPricingRules) {
            stockUnitPriceRuleMap.put(stockUnitPricingRule.getStockKeepingUnit(), stockUnitPricingRule);
        }
        return stockUnitPriceRuleMap;
    }

}
