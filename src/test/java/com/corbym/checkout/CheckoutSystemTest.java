package com.corbym.checkout;

import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockKeepingUnit;
import com.corbym.checkout.domain.PriceRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class CheckoutSystemTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final CheckoutSystem underTest = new CheckoutSystem(
            asList(new PriceRule(StockKeepingUnit.of("A"), 50L),
                    new PriceRule(StockKeepingUnit.of("B"), 30L),
                    new PriceRule(StockKeepingUnit.of("C"), 20L)
            )
    );

    @Test
    public void checkoutSystemScansASingleItemThenCalculatesTheCorrectTotalPrice() {
        underTest.scanItem(StockKeepingUnit.of("A"));

        assertThat(underTest.calculateTotalCostInPence(), is(50L));
    }

    @Test
    public void checkoutSystemScansMoreThanOneItemOfTheSameTypeThenCalculatesTheCorrectTotalPrice() {
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));

        assertThat(underTest.calculateTotalCostInPence(), is(100L));
    }

    @Test
    public void checkoutSystemScansMoreThanOneItemWithDifferentUnitsThenCalculatesTheCorrectTotalPrice() {
        underTest.scanItem(StockKeepingUnit.of("B"));
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("B"));

        assertThat(underTest.calculateTotalCostInPence(), is(110L));
    }


    @Test
    public void stockKeepingUnitWithSpecialPriceTriggersADiscountCalculatingTheTotalPrice() {
        final CheckoutSystem underTest = new CheckoutSystem(
                singletonList(new PriceRule(StockKeepingUnit.of("A"), 50L,
                        new DiscountRule(5L, 20L)))
        );
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));

        assertThat(underTest.calculateTotalCostInPence(), is(230L));

    }

    @Test
    public void discountWithZeroPenceDiscountsNothingFromExpectedPrice() {
        final CheckoutSystem underTest = new CheckoutSystem(
                singletonList(new PriceRule(StockKeepingUnit.of("A"), 50L,
                        new DiscountRule(1L, 0L)))
        );
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));

        assertThat(underTest.calculateTotalCostInPence(), is(150L));

    }

    @Test
    public void discountTriggerAtOrBelowLowestBoundIsAppliedToEveryStockKeepingUnit() {
        final CheckoutSystem underTest = new CheckoutSystem(
                asList(
                        new PriceRule(StockKeepingUnit.of("A"), 50L, new DiscountRule(0, 10L)),
                        new PriceRule(StockKeepingUnit.of("B"), 30L, new DiscountRule(-1, 10L)))
        );
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("B"));

        assertThat(underTest.calculateTotalCostInPence(), is(60L));

    }

    @Test
    public void negativeDiscountAmountsCalculateExpectedPriceRise() {
        final CheckoutSystem underTest = new CheckoutSystem(
                singletonList(new PriceRule(StockKeepingUnit.of("A"), 50L,
                        new DiscountRule(2L, -20L)))
        );
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));

        assertThat(underTest.calculateTotalCostInPence(), is(120L));
    }

    @Test
    public void subsequentCallsToScanItemAfterCalculatingTotalCostContinueToCalculateTheExpectedPrice() {
        underTest.scanItem(StockKeepingUnit.of("B"));
        underTest.scanItem(StockKeepingUnit.of("A"));

        underTest.calculateTotalCostInPence();

        underTest.scanItem(StockKeepingUnit.of("C"));

        assertThat(underTest.calculateTotalCostInPence(), is(100L));
    }

    @Test
    public void duplicatePriceRulesIgnoresEarlierSpecifiedRulesAvailableToCalculateExpectedPrice(){
        final CheckoutSystem underTest = new CheckoutSystem(
                Arrays.asList(
                        earlierPriceRule(),
                new PriceRule(StockKeepingUnit.of("A"), 20L,
                                new DiscountRule(3L, 20L)))
        );
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));
        underTest.scanItem(StockKeepingUnit.of("A"));
        assertThat(underTest.calculateTotalCostInPence(), is(40L));
    }

    @Test
    public void emptyShoppingListCalculatesExpectedPrice() {
        assertThat(underTest.calculateTotalCostInPence(), is(0L));
    }

    @Test
    public void unknownStockKeepingUnitThrowsException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("Unknown SKU specified."));
        underTest.scanItem(StockKeepingUnit.of("unknownSku"));
    }

    @Test
    public void invalidPricingRulesThrowsIllegalArgumentException(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("List of PriceRule objects cannot be null."));

        new CheckoutSystem(null);
    }

    private PriceRule earlierPriceRule() {
        return new PriceRule(StockKeepingUnit.of("A"), 50L,
                new DiscountRule(2L, 10L));
    }
}
