package com.github.toastshaman.tinytypes.events.test.faker;

import java.util.Locale;
import java.util.Random;

public final class Faker extends net.datafaker.Faker {

    public Faker() {
        this(647383L);
    }

    public Faker(long seed) {
        super(Locale.UK, new Random(seed));
    }

    public UUIDProvider uuid() {
        return getProvider(UUIDProvider.class, UUIDProvider::new);
    }

    public UlidProvider ulid() {
        return getProvider(UlidProvider.class, UlidProvider::new);
    }
}
