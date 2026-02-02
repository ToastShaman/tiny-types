package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.HexFormat;
import java.util.Objects;
import java.util.Random;

public interface IdGenerator {

    String generateSpanId();

    String generateTraceId();

    static IdGenerator random() {
        return new SimpleIdGenerator(new Random());
    }

    static IdGenerator random(Random random) {
        return new SimpleIdGenerator(random);
    }

    final class SimpleIdGenerator implements IdGenerator {
        private final Random random;

        SimpleIdGenerator(Random random) {
            this.random = Objects.requireNonNull(random);
        }

        @Override
        public String generateSpanId() {
            byte[] bytes = new byte[8];
            random.nextBytes(bytes);
            return HexFormat.of().withLowerCase().formatHex(bytes);
        }

        @Override
        public String generateTraceId() {
            byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            return HexFormat.of().withLowerCase().formatHex(bytes);
        }
    }
}
