package com.corbym.checkout;

import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockKeepingUnitPriceRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class CheckoutSystemTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final CheckoutSystem underTest = new CheckoutSystem(
            asList(new StockKeepingUnitPriceRule("A", 50),
                    new StockKeepingUnitPriceRule("B", 30))
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
    public void checkoutSystemScansMoreThanOneItemOfWithDifferentTypesThenCalculatesTheCorrectTotalPrice() {

        underTest.scanItem("B");
        underTest.scanItem("A");
        underTest.scanItem("B");

        assertThat(underTest.calculateTotalCostInPence(), is(110L));
    }


    @Test
    public void stockKeepingUnitWithSpecialPriceTriggersADiscountCalculatingTheTotalPrice() {
        final CheckoutSystem underTest = new CheckoutSystem(
                asList(new StockKeepingUnitPriceRule("A", 50L,
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
    public void unknownStockKeepingUnitThrowsException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("Unknown SKU specified."));
        underTest.scanItem("unknownSku");
    }

}
