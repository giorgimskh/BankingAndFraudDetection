# BankingAndFraudDetection

A rule-driven banking and fraud detection system, built in Java as an OOP design exercise.

## Overview

BankingAndFraudDetection is a console-based banking simulation. It models:

- Customers
- Bank accounts (Checking, Savings)
- Cards (Debit, Virtual)
- Transactions (Deposit, Withdrawal, Transfer, Card Payment)
- A ledger for financial consistency
- A rule-based fraud detection engine

The design emphasizes inheritance, polymorphism, encapsulation, composition, and separation of concerns, and is implemented with plain Java (no external frameworks or build tools) targeting Java 21+.

## Core Features

### Customer Management
- Create customers
- Each customer can own multiple accounts
- Each customer can hold multiple cards

### Account System
- Open Checking or Savings accounts
- Deposit and withdraw funds
- Transfer between accounts
- Account status handling (ACTIVE, FROZEN, CLOSED)

### Card Payments
- Issue debit or virtual cards linked to an account
- Daily spending limits, reset per calendar day
- Card authorization logic
- Merchant-based payments

## Transactions

Supported transaction types:

- Deposit
- Withdrawal
- Transfer
- CardPayment

Each transaction:

- Has a unique ID and a timestamp
- Has a status (CREATED, APPROVED, REVIEW, DECLINED, POSTED)
- Is recorded in a central Ledger
- Becomes immutable once posted

## Fraud Detection Engine

Risky transactions (withdrawal, transfer, card payment) are evaluated by the `FraudEngine` before posting. Each `FraudRule` implementation returns a decision:

- ALLOW - transaction is posted and balances update
- REVIEW - transaction is recorded but balances do not change
- BLOCK - transaction is declined and balances do not change

The engine runs all configured rules and applies the most severe decision. Current rule implementations:

- `LargeAmountRule` - flags transactions above a fixed threshold
- `DailySpendLimitRule` - flags spend past a card's or account's daily limit
- `VelocitySumRule` - flags total spend above a threshold within a time window
- `TooManyTransactionsRule` - flags too many transactions within a time window
- `NewMerchantRule` - flags payments to a merchant not seen before
- `RapidLocationChangeRule` - flags transactions in different countries too close together in time

## Architecture

Project structure:

```
src/
├── Main.java
├── domain/
│   ├── customer/     Customer
│   ├── account/      Account (abstract), CheckingAccount, SavingsAccount
│   ├── card/         Card (abstract), DebitCard, VirtualCard
│   ├── transaction/  Transaction (abstract), Deposit, Withdrawal, Transfer, CardPayment
│   ├── ledger/       Ledger
│   └── merchant/     Merchant
├── rules/
│   ├── FraudRule, FraudEngine, FraudContext, RuleResult, Decision
│   └── impl/         individual rule implementations
├── service/
│   └── BankService   application facade
├── exception/         domain-specific runtime exceptions
└── util/              Money, Currency
```

### Domain Layer
Business entities: `Customer`, the `Account` hierarchy, the `Card` hierarchy, the `Transaction` hierarchy, `Ledger`, and `Merchant`.

### Rules Layer
`FraudRule` interface, its implementations, and `FraudEngine`, which evaluates a transaction against all configured rules using the current `FraudContext`.

### Service Layer
`BankService` is the application facade. It holds in-memory state (customers, accounts, cards) and coordinates the domain model, the ledger, and the fraud engine for every operation.

### Util Layer
`Money` and `Currency` provide immutable, currency-safe arithmetic used throughout the domain layer.

There is no separate storage or persistence layer - `BankService` keeps all state in memory for the duration of the program.

## Architecture Diagram

See [docs/uml-diagram.md](docs/uml-diagram.md) for the UML class diagram covering the domain, rules, and service layers.

## Design Principles

- Encapsulation: account balances cannot be modified directly, only through `deposit`/`withdraw`.
- Inheritance: `Account`, `Card`, and `Transaction` hierarchies.
- Polymorphism: transaction posting (`apply()`) and fraud rule evaluation.
- Composition: `Customer` owns its `Account`s and `Card`s.
- Strategy pattern: `FraudRule` implementations plugged into `FraudEngine`.
- Clear separation between domain, rules, and service layers.

## System Invariants

- `Money` is immutable.
- Account balances change only through `Ledger` posting.
- Transactions are atomic - no partial transfers.
- A currency mismatch between operands raises `CurrencyMismatchException`.
- A fraud decision of REVIEW or BLOCK never alters balances.

## Example Scenario

The scripted demo in `Main.java` runs the following flow:

1. Create a customer
2. Open a checking account
3. Deposit funds
4. Issue a debit card with a daily limit
5. Card payment within the limit - ALLOW, balance decreases
6. Card payment exceeding the daily limit - BLOCK, balance unchanged
7. Card payment in a different country shortly after - flagged by the rapid location change rule
8. Withdraw funds
9. Open a savings account
10. Transfer funds between accounts

## Notes

- Pure JDK, no external dependencies or build tool (no Maven/Gradle).
- Targets Java 21+ and uses preview features: unnamed classes/instance main methods in `Main.java`, and `SequencedCollection` (`List.reversed()`, `getLast()`).
- No automated test suite - `Main.java` acts as a scripted smoke test covering the main flows.
