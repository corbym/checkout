package com.corbym.checkout;

import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockUnitPriceRule;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CheckoutSystemTest {

    private final CheckoutSystem underTest = new CheckoutSystem(
            asList(new StockUnitPriceRule("A", 50),
                    new StockUnitPriceRule("B", 30))
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

        assertThat(underTest.calculateTotalCostInPence(), is(80L));
    }


    @Test
    public void stockKeepingUnitWithSpecialPriceTriggersADiscountCalculatingTheTotalPrice(){
        final CheckoutSystem underTest = new CheckoutSystem(
                asList(new StockUnitPriceRule("A", 50)),
                asList(new DiscountRule("A", 3, 20))
        );
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");
        underTest.scanItem("A");

        assertThat(underTest.calculateTotalCostInPence(), is(260L));

    }

}
