package me.i2000c.newalb.utils.reflection;

import java.lang.reflect.Constructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

@AllArgsConstructor(staticName = "of")
@Getter
public class RefConstructor {
    @NonNull private final Constructor actualConstructor;
    
    @SneakyThrows
    @SuppressWarnings("UseSpecificCatch")
    public <T> T call(Object... params) {
        return (T) actualConstructor.newInstance(params);
    }
}
