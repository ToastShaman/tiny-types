package com.github.toastshaman.tinytypes.events.test.faker;

import java.util.Locale;
import java.util.Random;
import net.datafaker.providers.base.BaseFaker;

public final class Faker extends BaseFaker {

    public Faker() {
        this(647383L);
    }

    public Faker(long seed) {
        super(Locale.UK, new Random(seed));
    }

    public UUIDProvider uuid() {
        return getProvider(UUIDProvider.class, UUIDProvider::new, this);
    }

    public UlidProvider ulid() {
        return getProvider(UlidProvider.class, UlidProvider::new, this);
    }
}
