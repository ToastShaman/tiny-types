package com.github.toastshaman.tinytypes.events.test.faker;

import com.github.f4b6a3.ulid.Ulid;
import java.nio.ByteBuffer;
import java.util.UUID;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public final class UlidProvider extends AbstractProvider<BaseProviders> {

    public UlidProvider(BaseProviders faker) {
        super(faker);
    }

    public Ulid random() {
        var bytes = ByteBuffer.wrap(faker.random().nextRandomBytes(16));
        var uuid = new UUID(bytes.getLong(), bytes.getLong());
        return Ulid.from(uuid);
    }
}
