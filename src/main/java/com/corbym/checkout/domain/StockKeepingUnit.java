package com.corbym.checkout.domain;

import java.util.Objects;

public class StockKeepingUnit {

    private String identifier;

    private StockKeepingUnit(String identifier) {
        this.identifier = identifier;
    }

    public static StockKeepingUnit of(String identifier) {
        return new StockKeepingUnit(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockKeepingUnit that = (StockKeepingUnit) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
