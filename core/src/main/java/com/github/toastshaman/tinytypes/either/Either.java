/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2022 Vavr, https://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.toastshaman.tinytypes.either;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Either represents a value of two possible types. An Either is either a {@link Left} or a
 * {@link Right}.
 * <p>
 * If the given Either is a Right and projected to a Left, the Left operations have no effect on the Right value.<br>
 * If the given Either is a Left and projected to a Right, the Right operations have no effect on the Left value.<br>
 * If a Left is projected to a Left or a Right is projected to a Right, the operations have an effect.
 * <p>
 * <strong>Example:</strong> A compute() function, which results either in an Integer value (in the case of success) or
 * in an error message of type String (in the case of failure). By convention the success case is Right and the failure
 * is Left.
 *
 * <pre>
 * <code>
 * Either&lt;String,Integer&gt; value = compute().right().map(i -&gt; i * 2).toEither();
 * </code>
 * </pre>
 * <p>
 * If the result of compute() is Right(1), the value is Right(2).<br>
 * If the result of compute() is Left("error"), the value is Left("error").
 *
 * @param <L> The type of the Left value of an Either.
 * @param <R> The type of the Right value of an Either.
 */
public abstract class Either<L, R> implements Iterable<R> {

    // sealed
    private Either() {
    }

    /**
     * Constructs a {@link Right}
     *
     * <pre>{@code
     * // Creates Either instance initiated with right value 1
     * Either<?, Integer> either = Either.right(1);
     * }</pre>
     *
     * @param right The value.
     * @param <L>   Type of left value.
     * @param <R>   Type of right value.
     * @return A new {@code Right} instance.
     */
    public static <L, R> Either<L, R> right(R right) {
        return new Right<>(right);
    }

    /**
     * Constructs a {@link Left}
     *
     * <pre>{@code
     * // Creates Either instance initiated with left value "error message"
     * Either<String, ?> either = Either.left("error message");
     * }</pre>
     *
     * @param left The value.
     * @param <L>  Type of left value.
     * @param <R>  Type of right value.
     * @return A new {@code Left} instance.
     */
    public static <L, R> Either<L, R> left(L left) {
        return new Left<>(left);
    }

    /**
     * Narrows a widened {@code Either<? extends L, ? extends R>} to {@code Either<L, R>}
     * by performing a type-safe cast. This is eligible because immutable/read-only
     * collections are covariant.
     *
     * <pre>{@code
     * // It's ok, Integer inherits from Number
     * Either<?, Number> answer = Either.right(42);
     *
     * // RuntimeException is an Exception
     * Either<Exception, ?> failed = Either.left(new RuntimeException("Vogon poetry recital"));
     * }</pre>
     *
     * @param either A {@code Either}.
     * @param <L>    Type of left value.
     * @param <R>    Type of right value.
     * @return the given {@code either} instance as narrowed type {@code Either<L, R>}.
     */
    @SuppressWarnings("unchecked")
    public static <L, R> Either<L, R> narrow(Either<? extends L, ? extends R> either) {
        return (Either<L, R>) either;
    }

    /**
     * Returns the left value.
     *
     * <pre>{@code
     * // prints "error"
     * System.out.println(Either.left("error").getLeft());
     *
     * // throws NoSuchElementException
     * System.out.println(Either.right(42).getLeft());
     * }</pre>
     *
     * @return The left value.
     * @throws NoSuchElementException if this is a {@code Right}.
     */
    public abstract L getLeft();

    /**
     * Returns whether this Either is a Left.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Either.left("error").isLeft());
     *
     * // prints "false"
     * System.out.println(Either.right(42).isLeft());
     * }</pre>
     *
     * @return true, if this is a Left, false otherwise
     */
    public abstract boolean isLeft();

    /**
     * Returns whether this Either is a Right.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Either.right(42).isRight());
     *
     * // prints "false"
     * System.out.println(Either.left("error").isRight());
     * }</pre>
     *
     * @return true, if this is a Right, false otherwise
     */
    public abstract boolean isRight();

    /**
     * Maps either the left or the right side of this disjunction.
     *
     * <pre>{@code
     * Either<?, AtomicInteger> success = Either.right(new AtomicInteger(42));
     *
     * // prints "Right(42)"
     * System.out.println(success.bimap(Function1.identity(), AtomicInteger::get));
     *
     * Either<Exception, ?> failure = Either.left(new Exception("error"));
     *
     * // prints "Left(error)"
     * System.out.println(failure.bimap(Exception::getMessage, Function1.identity()));
     * }</pre>
     *
     * @param leftMapper  maps the left value if this is a Left
     * @param rightMapper maps the right value if this is a Right
     * @param <X>         The new left type of the resulting Either
     * @param <Y>         The new right type of the resulting Either
     * @return A new Either instance
     */
    public final <X, Y> Either<X, Y> bimap(Function<? super L, ? extends X> leftMapper, Function<? super R, ? extends Y> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");
        if (isRight()) {
            return new Right<>(rightMapper.apply(get()));
        } else {
            return new Left<>(leftMapper.apply(getLeft()));
        }
    }

    /**
     * Folds either the left or the right side of this disjunction.
     *
     * <pre>{@code
     * Either<Exception, Integer> success = Either.right(3);
     *
     * // prints "Users updated: 3"
     * System.out.println(success.fold(Exception::getMessage, count -> "Users updated: " + count));
     *
     * Either<Exception, Integer> failure = Either.left(new Exception("Failed to update users"));
     *
     * // prints "Failed to update users"
     * System.out.println(failure.fold(Exception::getMessage, count -> "Users updated: " + count));
     * }</pre>
     *
     * @param leftMapper  maps the left value if this is a Left
     * @param rightMapper maps the right value if this is a Right
     * @param <U>         type of the folded value
     * @return A value of type U
     */
    public final <U> U fold(Function<? super L, ? extends U> leftMapper, Function<? super R, ? extends U> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");
        if (isRight()) {
            return rightMapper.apply(get());
        } else {
            return leftMapper.apply(getLeft());
        }
    }

    /**
     * Transforms this {@code Either}.
     *
     * <pre>{@code
     * // prints "Anwser is 42"
     * System.out.println(Either.right(42).<String> transform(e -> "Anwser is " + e.get()));
     * }</pre>
     *
     * @param f   A transformation
     * @param <U> Type of transformation result
     * @return An instance of type {@code U}
     * @throws NullPointerException if {@code f} is null
     */
    public final <U> U transform(Function<? super Either<L, R>, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        return f.apply(this);
    }

    /**
     * Gets the Right value or an alternate value, if the projected Either is a Left.
     *
     * <pre>{@code
     * // prints "42"
     * System.out.println(Either.right(42).getOrElseGet(l -> -1));
     *
     * // prints "13"
     * System.out.println(Either.left("error message").getOrElseGet(String::length));
     * }</pre>
     *
     * @param other a function which converts a Left value to an alternative Right value
     * @return the right value, if the underlying Either is a Right or else the alternative Right value provided by
     * {@code other} by applying the Left value.
     */
    public final R getOrElseGet(Function<? super L, ? extends R> other) {
        Objects.requireNonNull(other, "other is null");
        if (isRight()) {
            return get();
        } else {
            return other.apply(getLeft());
        }
    }

    /**
     * Runs an action in the case this is a projection on a Left value.
     *
     * <pre>{@code
     * // prints "no value found"
     * Either.left("no value found").orElseRun(System.out::println);
     * }</pre>
     *
     * @param action an action which consumes a Left value
     */
    public final void orElseRun(Consumer<? super L> action) {
        Objects.requireNonNull(action, "action is null");
        if (isLeft()) {
            action.accept(getLeft());
        }
    }

    /**
     * Gets the Right value or throws, if the projected Either is a Left.
     *
     * <pre>{@code
     * Function<String, RuntimeException> exceptionFunction = RuntimeException::new;
     * // prints "42"
     * System.out.println(Either.<String, Integer>right(42).getOrElseThrow(exceptionFunction));
     *
     * // throws RuntimeException("no value found")
     * Either.left("no value found").getOrElseThrow(exceptionFunction);
     * }</pre>
     *
     * @param <X>               a throwable type
     * @param exceptionFunction a function which creates an exception based on a Left value
     * @return the right value, if the underlying Either is a Right or else throws the exception provided by
     * {@code exceptionFunction} by applying the Left value.
     * @throws X if the projected Either is a Left
     */
    public final <X extends Throwable> R getOrElseThrow(Function<? super L, X> exceptionFunction) throws X {
        Objects.requireNonNull(exceptionFunction, "exceptionFunction is null");
        if (isRight()) {
            return get();
        } else {
            throw exceptionFunction.apply(getLeft());
        }
    }

    /**
     * Converts a {@code Left} to a {@code Right} vice versa by wrapping the value in a new type.
     *
     * <pre>{@code
     * // prints "Right(42)"
     * System.out.println(Either.left(42).swap());
     *
     * // prints "Left(message)"
     * System.out.println(Either.right("message").swap());
     * }</pre>
     *
     * @return a new {@code Either}
     */
    public final Either<R, L> swap() {
        if (isRight()) {
            return new Left<>(get());
        } else {
            return new Right<>(getLeft());
        }
    }

    /**
     * Calls recoveryFunction if the projected Either is a Left, performs no operation if this is a Right. This is
     * similar to {@code getOrElseGet}, but where the fallback method also returns an Either.
     *
     * <pre>{@code
     * Either<Integer, String> tryGetString() { return Either.left(1); }
     *
     * Either<Integer, String> tryGetStringAnotherWay(Integer lvalue) { return Either.right("yo " + lvalue); }
     *
     * = Right("yo 1")
     * tryGetString().recover(this::tryGetStringAnotherWay);
     * }</pre>
     *
     * @param recoveryFunction a function which accepts a Left value and returns an Either
     * @return an {@code Either<L, R>} instance
     * @throws NullPointerException if the given {@code recoveryFunction} is null
     */
    @SuppressWarnings("unchecked")
    public final Either<L, R> recoverWith(Function<? super L, ? extends Either<? extends L, ? extends R>> recoveryFunction) {
        Objects.requireNonNull(recoveryFunction, "recoveryFunction is null");
        if (isLeft()) {
            return (Either<L, R>) recoveryFunction.apply(getLeft());
        } else {
            return this;
        }
    }

    /**
     * Calls {@code recoveryFunction} if the projected Either is a Left, or returns {@code this} if Right. The result
     * of {@code recoveryFunction} will be projected as a Right.
     *
     * <pre>{@code
     * Either<Integer, String> tryGetString() { return Either.left(1); }
     *
     * String getStringAnotherWay() { return "yo"; }
     *
     * = Right("yo")
     * tryGetString().recover(this::getStringAnotherWay);
     * }</pre>
     *
     * @param recoveryFunction a function which accepts a Left value and returns a Right value
     * @return an {@code Either<L, R>} instance
     * @throws NullPointerException if the given {@code recoveryFunction} is null
     */
    public final Either<L, R> recover(Function<? super L, ? extends R> recoveryFunction) {
        Objects.requireNonNull(recoveryFunction, "recoveryFunction is null");
        if (isLeft()) {
            return Either.right(recoveryFunction.apply(getLeft()));
        } else {
            return this;
        }
    }

    // -- Adjusted return types of Monad methods

    /**
     * FlatMaps this right-biased Either.
     *
     * <pre>{@code
     * // prints "Right(42)"
     * System.out.println(Either.right(21).flatMap(v -> Either.right(v * 2)));
     *
     * // prints "Left(error message)"
     * System.out.println(Either.left("error message").flatMap(Either::right));
     * }</pre>
     *
     * @param mapper A mapper
     * @param <U>    Component type of the mapped right value
     * @return this as {@code Either<L, U>} if this is a Left, otherwise the right mapping result
     * @throws NullPointerException if {@code mapper} is null
     */
    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> flatMap(Function<? super R, ? extends Either<L, ? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (isRight()) {
            return (Either<L, U>) mapper.apply(get());
        } else {
            return (Either<L, U>) this;
        }
    }

    /**
     * Maps the value of this Either if it is a Right, performs no operation if this is a Left.
     *
     * <pre>{@code
     * // = Right("A")
     * Either.right("a").map(String::toUpperCase);
     *
     * // = Left(1)
     * Either.left(1).map(String::toUpperCase);
     * }</pre>
     *
     * @param mapper A mapper
     * @param <U>    Component type of the mapped right value
     * @return a mapped {@code Monad}
     * @throws NullPointerException if {@code mapper} is null
     */
    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> map(Function<? super R, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (isRight()) {
            return Either.right(mapper.apply(get()));
        } else {
            return (Either<L, U>) this;
        }
    }

    /**
     * Maps the value of this Either if it is a Left, performs no operation if this is a Right.
     *
     * <pre>{@code
     * // = Left(2)
     * Either.left(1).mapLeft(i -> i + 1);
     *
     * // = Right("a")
     * Either.right("a").mapLeft(i -> i + 1);
     * }</pre>
     *
     * @param leftMapper A mapper
     * @param <U>        Component type of the mapped right value
     * @return a mapped {@code Monad}
     * @throws NullPointerException if {@code mapper} is null
     */
    @SuppressWarnings("unchecked")
    public final <U> Either<U, R> mapLeft(Function<? super L, ? extends U> leftMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        if (isLeft()) {
            return Either.left(leftMapper.apply(getLeft()));
        } else {
            return (Either<U, R>) this;
        }
    }

    // -- Adjusted return types of Value methods

    /**
     * Filters this right-biased {@code Either} by testing a predicate.
     * <p>
     *
     * @param predicate A predicate
     * @return a new {@code Option} instance
     * @throws NullPointerException if {@code predicate} is null
     */
    public final Optional<Either<L, R>> filter(Predicate<? super R> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return isLeft() || predicate.test(get()) ? Optional.of(this) : Optional.empty();
    }

    /**
     * Filters this right-biased {@code Either} by testing a predicate.
     *
     * @param predicate A predicate
     * @return a new {@code Either}
     * @throws NullPointerException if {@code predicate} is null
     */
    public final Optional<Either<L, R>> filterNot(Predicate<? super R> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return filter(predicate.negate());
    }

    /**
     * Filters this right-biased {@code Either} by testing a predicate.
     * If the {@code Either} is a {@code Right} and the predicate doesn't match, the
     * {@code Either} will be turned into a {@code Left} with contents computed by applying
     * the zero function to the {@code Either} value.
     *
     * <pre>{@code
     * // = Left("bad: a")
     * Either.right("a").filterOrElse(i -> false, val -> "bad: " + val);
     *
     * // = Right("a")
     * Either.right("a").filterOrElse(i -> true, val -> "bad: " + val);
     * }</pre>
     *
     * @param predicate A predicate
     * @param zero      A function that turns a right value into a left value if the right value does not make it through the filter.
     * @return an {@code Either} instance
     * @throws NullPointerException if {@code predicate} is null
     */
    public final Either<L, R> filterOrElse(Predicate<? super R> predicate, Function<? super R, ? extends L> zero) {
        Objects.requireNonNull(predicate, "predicate is null");
        Objects.requireNonNull(zero, "zero is null");
        if (isLeft() || predicate.test(get())) {
            return this;
        } else {
            return Either.left(zero.apply(get()));
        }
    }

    /**
     * Gets the right value if this is a {@code Right} or throws if this is a {@code Left}.
     *
     * @return the right value
     * @throws NoSuchElementException if this is a {@code Left}.
     */
    public abstract R get();

    public final boolean isEmpty() {
        return isLeft();
    }

    @SuppressWarnings("unchecked")
    public final Either<L, R> orElse(Either<? extends L, ? extends R> other) {
        Objects.requireNonNull(other, "other is null");
        return isRight() ? this : (Either<L, R>) other;
    }

    @SuppressWarnings("unchecked")
    public final Either<L, R> orElse(Supplier<? extends Either<? extends L, ? extends R>> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return isRight() ? this : (Either<L, R>) supplier.get();
    }

    @Override
    public final Iterator<R> iterator() {
        if (isRight()) {
            return List.of(get()).iterator();
        } else {
            return Collections.emptyIterator();
        }
    }

    /**
     * Performs the given {@code leftAction} on the left element if this is Left.
     * Performs the given {@code rightAction} on the right element if this is Right.
     *
     * @param leftAction  The action that will be performed on the left element
     * @param rightAction The action that will be performed on the right element
     * @return this instance
     */
    public final Either<L, R> peek(Consumer<? super L> leftAction, Consumer<? super R> rightAction) {
        Objects.requireNonNull(leftAction, "leftAction is null");
        Objects.requireNonNull(rightAction, "rightAction is null");

        if (isLeft()) {
            leftAction.accept(getLeft());
        } else { // this isRight() by definition
            rightAction.accept(get());
        }

        return this;
    }

    public final Either<L, R> peek(Consumer<? super R> action) {
        Objects.requireNonNull(action, "action is null");
        if (isRight()) {
            action.accept(get());
        }
        return this;
    }

    public final Either<L, R> peekLeft(Consumer<? super L> action) {
        Objects.requireNonNull(action, "action is null");
        if (isLeft()) {
            action.accept(getLeft());
        }
        return this;
    }

    /**
     * The {@code Left} version of an {@code Either}.
     *
     * @param <L> left component type
     * @param <R> right component type
     */
    private static final class Left<L, R> extends Either<L, R> {
        private final L value;

        /**
         * Constructs a {@code Left}.
         *
         * @param value a left value
         */
        private Left(L value) {
            this.value = value;
        }

        @Override
        public R get() {
            throw new NoSuchElementException("get() on Left");
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Left && Objects.equals(value, ((Left<?, ?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        public String stringPrefix() {
            return "Left";
        }

        @Override
        public String toString() {
            return stringPrefix() + "(" + value + ")";
        }
    }

    /**
     * The {@code Right} version of an {@code Either}.
     *
     * @param <L> left component type
     * @param <R> right component type
     */
    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        /**
         * Constructs a {@code Right}.
         *
         * @param value a right value
         */
        private Right(R value) {
            this.value = value;
        }

        @Override
        public R get() {
            return value;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("getLeft() on Right");
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Right && Objects.equals(value, ((Right<?, ?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        public String stringPrefix() {
            return "Right";
        }

        @Override
        public String toString() {
            return stringPrefix() + "(" + value + ")";
        }
    }
}

