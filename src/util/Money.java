package util;

import exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money  implements Comparable<Money> {
    private static final int SCALE = 2;

    private BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO)<=0) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        if(this.isNegative())

        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    private void requireSameCurrency(Money other) throws CurrencyMismatchException {
        if (other == null) throw new IllegalArgumentException("money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException("Currency mismatch");
        }
    }

    public void add(Money other) throws CurrencyMismatchException {
        requireSameCurrency(other);
        this.amount=this.amount.add(other.getAmount());
    }

    public void subtract(Money other) throws CurrencyMismatchException {
        requireSameCurrency(other);
        this.amount=this.amount.subtract(other.getAmount());
    }

    public void multiply(BigDecimal factor){
        if(factor==null) throw new IllegalArgumentException("Factor cannot be null");
        this.amount=this.amount.multiply(factor);
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public int compareTo(Money other) {
        try {
            requireSameCurrency(other);
        } catch (CurrencyMismatchException e) {
            throw new RuntimeException(e);
        }
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
