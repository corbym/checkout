package com.corbym.checkout;

import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockKeepingUnitPriceRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class CheckoutSystemTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final CheckoutSystem underTest = new CheckoutSystem(
            asList(new StockKeepingUnitPriceRule("A", 50L),
                    new StockKeepingUnitPriceRule("B", 30L),
                    new StockKeepingUnitPriceRule("C", 20L)
            )
    );

    @Test
    public void checkoutSystemScansASingleItemThenCalculatesTheCorrectTotalPrice() {
        underTest.scanItem("A");

        assertThat(underTest.calculateTotalCostInPence(), is(50L));
    }

    @Test
    public void checkoutSystemScansMoreThanOneItemOfTheSameTypeThenCalculatesTheCorrectTotalPrice() {
        underTest.scanItem("A");
        underTest.scanItem("A");

        assertThat(underTest.calculateTotalCostInPence(), is(100L));
    }

    @Test
    public void checkoutSystemScansMoreThanOneItemWithDifferentUnitsThenCalculatesTheCorrectTotalPrice() {
        underTest.scanItem("B");
        underTest.scanItem("A");
        underTest.scanItem("B");

        assertThat(underTest.calculateTotalCostInPence(), is(110L));
    }


    @Test
    public void stockKeepingUnitWithSpecialPriceTriggersADiscountCalculatingTheTotalPrice() {
        final CheckoutSystem underTest = new CheckoutSystem(
                singletonList(new StockKeepingUnitPriceRule("A", 50L,
                        new DiscountRule(5L, 20L)))
        );
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");

        assertThat(underTest.calculateTotalCostInPence(), is(230L));

    }

    @Test
    public void discountWithZeroPenceDiscountsNothingFromExpectedPrice() {
        final CheckoutSystem underTest = new CheckoutSystem(
                singletonList(new StockKeepingUnitPriceRule("A", 50L,
                        new DiscountRule(5L, 0L)))
        );
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");

        assertThat(underTest.calculateTotalCostInPence(), is(150L));

    }

    @Test
    public void discountTriggerAtOrBelowLowestBoundIsAppliedToEveryStockKeepingUnit() {
        final CheckoutSystem underTest = new CheckoutSystem(
                asList(
                        new StockKeepingUnitPriceRule("A", 50L, new DiscountRule(0, 10L)),
                        new StockKeepingUnitPriceRule("B", 30L, new DiscountRule(-1, 10L)))
        );
        underTest.scanItem("A");
        underTest.scanItem("B");

        assertThat(underTest.calculateTotalCostInPence(), is(60L));

    }

    @Test
    public void negativeDiscountAmountsCalculateExpectedPriceRise(){
        final CheckoutSystem underTest = new CheckoutSystem(
                singletonList(new StockKeepingUnitPriceRule("A", 50L,
                        new DiscountRule(2L, -20L)))
        );
        underTest.scanItem("A");
        underTest.scanItem("A");

        assertThat(underTest.calculateTotalCostInPence(), is(120L));
    }

    @Test
    public void subsequentCallsToScanItemAfterCalculatingTotalCostContinueToCalculateTheExpectedPrice() {
        underTest.scanItem("B");
        underTest.scanItem("A");

        underTest.calculateTotalCostInPence();

        underTest.scanItem("C");

        assertThat(underTest.calculateTotalCostInPence(), is(100L));
    }

    @Test
    public void emptyShoppingListCalculatesExpectedPrice() {
        assertThat(underTest.calculateTotalCostInPence(), is(0L));
    }

    @Test
    public void unknownStockKeepingUnitThrowsException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("Unknown SKU specified."));
        underTest.scanItem("unknownSku");
    }

}
