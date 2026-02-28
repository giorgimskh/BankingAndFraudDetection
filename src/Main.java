import domain.account.CheckingAccount;
import domain.account.SavingsAccount;
import domain.card.DebitCard;
import domain.customer.Customer;
import domain.ledger.Ledger;
import domain.merchant.Merchant;
import domain.transaction.Transaction;
import rules.Decision;
import rules.FraudEngine;
import rules.FraudRule;
import rules.impl.*;
import service.BankService;
import util.Currency;
import util.Money;

void main() {

    Ledger ledger = new Ledger();

    List<FraudRule> rules = List.of(
            new DailySpendLimitRule(Decision.BLOCK),
            new RapidLocationChangeRule(Duration.ofMinutes(2), Decision.REVIEW)
    );

    FraudEngine fraudEngine = new FraudEngine(rules);
    BankService bank = new BankService(ledger, fraudEngine);

    // Create customer
    Customer c = bank.createCustomer("John Doe");

    // Open account
    CheckingAccount acc = bank.openChecking(
            c.getId(),
            Currency.USD,
            new Money(BigDecimal.valueOf(500), Currency.USD)
    );

    // Deposit
    bank.deposit(acc.getId(),
            new Money(BigDecimal.valueOf(1000), Currency.USD),
            "Initial deposit");

    // Issue card
    DebitCard card = bank.issueDebitCard(
            c.getId(),
            acc.getId(),
            new Money(BigDecimal.valueOf(300), Currency.USD)
    );

    // Merchant US
    Merchant amazon = new Merchant("Amazon", "US");

    // Normal payment (ALLOW)
    Transaction t1 = bank.payByCard(
            card.getId(),
            amazon,
            new Money(BigDecimal.valueOf(100), Currency.USD),
            "Shopping"
    );

    System.out.println("T1 decision: " + t1.getStatus());

    // Exceed daily limit (BLOCK)
    Transaction t2 = bank.payByCard(
            card.getId(),
            amazon,
            new Money(BigDecimal.valueOf(250), Currency.USD),
            "Big purchase"
    );

    System.out.println("T2 decision: " + t2.getStatus());

    // Rapid location change test
    Merchant tokyoShop = new Merchant("TokyoShop", "JP");

    Transaction t3 = bank.payByCard(
            card.getId(),
            tokyoShop,
            new Money(BigDecimal.valueOf(50), Currency.USD),
            "Japan purchase"
    );

    System.out.println("T3 decision: " + t3.getStatus());

    // Withdraw test
    Transaction w1 = bank.withdraw(
            acc.getId(),
            new Money(BigDecimal.valueOf(50), Currency.USD),
            "ATM"
    );

    System.out.println("Withdraw decision: " + w1.getStatus());

    // Transfer test (create second account)
    SavingsAccount acc2 = bank.openSavings(
            c.getId(),
            Currency.USD,
            new Money(BigDecimal.valueOf(100), Currency.USD),
            5
    );

    Transaction tr = bank.transfer(
            acc.getId(),
            acc2.getId(),
            new Money(BigDecimal.valueOf(100), Currency.USD),
            "Move money"
    );

    System.out.println("Transfer decision: " + tr.getStatus());

    // Print ledger size
    System.out.println("Ledger transactions: " + bank.getLedger().size());

    // Print attempts
    System.out.println("Attempted transactions: " + bank.listAttempts().size());

}
