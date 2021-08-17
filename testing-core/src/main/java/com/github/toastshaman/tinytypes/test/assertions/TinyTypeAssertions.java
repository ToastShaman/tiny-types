package com.github.toastshaman.tinytypes.test.assertions;

import com.github.toastshaman.tinytypes.AbstractValueType;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.ObjectAssert;

public final class TinyTypeAssertions<T extends Comparable<? super T>>
        extends AbstractAssert<TinyTypeAssertions<T>, AbstractValueType<T>> {

    private TinyTypeAssertions(AbstractValueType<T> actual) {
        super(actual, TinyTypeAssertions.class);
    }

    public static <T extends Comparable<? super T>> TinyTypeAssertions<T> assertThatTinyType(
            AbstractValueType<T> actual) {
        return new TinyTypeAssertions<>(actual);
    }

    public TinyTypeAssertions<T> hasSameValueAs(AbstractValueType<?> expected) {
        objects.assertEqual(info, actual.unwrap(), expected.unwrap());
        return this;
    }

    public TinyTypeAssertions<T> hasSameValueAs(T value) {
        objects.assertEqual(info, actual.unwrap(), value);
        return this;
    }

    public ObjectAssert<T> unwrapped() {
        return AssertionsForClassTypes.assertThat(actual.unwrap());
    }
}
