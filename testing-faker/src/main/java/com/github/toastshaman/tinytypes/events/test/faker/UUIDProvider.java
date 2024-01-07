package com.github.toastshaman.tinytypes.events.test.faker;

import java.nio.ByteBuffer;
import java.util.UUID;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public final class UUIDProvider extends AbstractProvider<BaseProviders> {

    public UUIDProvider(BaseProviders faker) {
        super(faker);
    }

    public UUID random() {
        var bytes = ByteBuffer.wrap(faker.random().nextRandomBytes(16));
        return new UUID(bytes.getLong(), bytes.getLong());
    }
}
