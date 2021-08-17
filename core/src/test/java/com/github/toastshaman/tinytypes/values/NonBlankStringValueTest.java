package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;
import static com.github.toastshaman.tinytypes.validation.Validator.MaxLength;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.toastshaman.tinytypes.validation.ValidationException;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class NonBlankStringValueTest {

    @Test
    void creates_new_non_blank_value_type() {
        Supplier<MyNonBlankString> firstnameFn = () -> new MyNonBlankString("Robert", AlwaysValid());

        var first = firstnameFn.get();
        var second = firstnameFn.get();

        assertThat(first).isNotNull();
        assertThat(first.show()).isEqualTo("Robert");
        assertThat(first.toString()).isEqualTo("Robert");
        assertThat(first.unwrap()).isEqualTo("Robert");
        assertThat(first).isEqualTo(second);
        assertThat(first.compareTo(second)).isEqualTo(0);
    }

    @Test
    void throws_if_blank() {
        assertThatThrownBy(() -> new MyNonBlankString("   ", AlwaysValid()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("MyNonBlankString: [must not be blank]");
    }

    @Test
    void throws_if_null() {
        assertThatThrownBy(() -> new MyNonBlankString(null, AlwaysValid())).isInstanceOf(NullPointerException.class);
    }

    @Test
    void throws_if_validation_fails() {
        assertThatThrownBy(() -> new MyNonBlankString("abc", MaxLength(1)))
                .isInstanceOf(ValidationException.class)
                .hasMessage("MyNonBlankString: [must be less than or equal to 1]");
    }

    private static class MyNonBlankString extends NonBlankStringValue {
        public MyNonBlankString(String value, Validator<String> validator) {
            super(value, validator);
        }
    }
}
