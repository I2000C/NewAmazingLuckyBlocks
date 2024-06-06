package me.i2000c.newalb.config.serializators;

import me.i2000c.newalb.config.Config;

@FunctionalInterface
public interface ConfigSerializer<T> {
    public void serialize(Config config, String path, T value);
}
