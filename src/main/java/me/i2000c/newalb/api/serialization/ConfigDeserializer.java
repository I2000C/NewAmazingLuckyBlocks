package me.i2000c.newalb.api.serialization;

import me.i2000c.newalb.config.Config;

@FunctionalInterface
public interface ConfigDeserializer<T> {    
    public T deserialize(Config config, String path);
}
