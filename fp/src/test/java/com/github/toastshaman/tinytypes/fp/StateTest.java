package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.Tuple;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class StateTest {

    @Test
    void example() {
        var bankAccount = new BankAccount(100);

        var computation = BankOperations.deposit(10)
                .flatMap(account -> BankOperations.deposit(11))
                .flatMap(account -> BankOperations.withdraw(5));

        var finalBalance = computation.execState(bankAccount).balance();

        assertThat(finalBalance).isEqualTo(116.0);
    }

    @Test
    void modify_state_and_run() {
        State<Integer, String> init = State.of(s -> Tuple.of(s + 1, "A"));
        State<Integer, String> addOne = State.of(s -> Tuple.of(s + 1, "B"));
        State<Integer, String> addTwo = State.of(s -> Tuple.of(s + 2, "C"));

        var result = init.andThen(addOne).andThen(addTwo).map(String::toLowerCase);

        assertThat(result.run(1)).satisfies(it -> {
            assertThat(it._1).isEqualTo(5);
            assertThat(it._2).isEqualTo("c");
        });
        assertThat(result.execState(1)).isEqualTo(5);
        assertThat(result.evalState(1)).isEqualTo("c");
    }

    record BankAccount(double balance) {
        BankAccount deposit(double amount) {
            return new BankAccount(balance + amount);
        }

        BankAccount withdraw(double amount) {
            if (amount <= balance) {
                return new BankAccount(balance - amount);
            }
            throw new IllegalStateException("insufficient funds");
        }
    }

    static class BankOperations {
        static State<BankAccount, Double> deposit(double amount) {
            return State.modify(account -> account.deposit(amount));
        }

        static State<BankAccount, Double> withdraw(double amount) {
            return State.modify(account -> account.withdraw(amount));
        }
    }
}
