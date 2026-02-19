package util;

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

    @Override
    public int compareTo(Money o) {
        return 0;
    }
}
