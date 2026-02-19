package util;

import exception.CurrencyMismatchException;

import java.math.BigDecimal;

public class Money  implements  Comparable<Money>{
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if(amount==null)
            throw new IllegalArgumentException("Amount cannot be null");
        if(currency==null)
            throw new IllegalArgumentException("Currency cannot be null");

        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) throws CurrencyMismatchException {
        if(!this.currency.toString().equals(other.currency.toString())) {
            throw new CurrencyMismatchException("Currency mismatched");
        }

        return new Money(this.amount.add(other.amount),this.currency);
    }




    @Override
    public int compareTo(Money o) {
        return 0;
    }
}
