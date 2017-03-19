package com.corbym.checkout.integration;


import com.corbym.checkout.CheckoutSystem;
import com.corbym.checkout.domain.DiscountRule;
import com.corbym.checkout.domain.StockKeepingUnit;
import com.corbym.checkout.domain.StockKeepingUnitPriceRule;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CheckoutIntegrationTest {

    private static final int TOTAL_THREADS = 100;

    @Test(timeout = 15000)
    public void scanningItemsAndCalculatingTotalCostIsThreadsafe() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_THREADS);
        CheckoutSystem checkoutSystem = new CheckoutSystem(singletonList(new StockKeepingUnitPriceRule(StockKeepingUnit.of("A"), 10L, new DiscountRule(0L, 0))));
        for (int t = 0; t <= TOTAL_THREADS; t++) {
            executorService.submit(() -> {
                for (int i = 0; i < 10; i++) {
                    checkoutSystem.scanItem(StockKeepingUnit.of("A"));
                    checkoutSystem.scanItem(StockKeepingUnit.of("A"));
                    checkoutSystem.scanItem(StockKeepingUnit.of("A"));
                    checkoutSystem.scanItem(StockKeepingUnit.of("A"));
                    checkoutSystem.scanItem(StockKeepingUnit.of("A"));
                    checkoutSystem.calculateTotalCostInPence();
                    sleep();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        long totalCost = checkoutSystem.calculateTotalCostInPence();
        assertTrue(String.format("should be correct price %d, was %d", 50500L, totalCost), totalCost == 50500L);

    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            fail(String.format("Thread interrupted, test failed: %s", e.getMessage()));
        }
    }
}
