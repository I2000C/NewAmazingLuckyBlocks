package me.i2000c.newalb.utils.reflection;

import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

@AllArgsConstructor(staticName = "of")
@Getter
public class RefMethod {
    @NonNull private final Method actualMethod;
    
    @SneakyThrows
    @SuppressWarnings("UseSpecificCatch")
    public <T> T call(Object target, Object... params) {
        return (T) actualMethod.invoke(target, params);
    }
    
    public <T> T callStatic(Object... params) {
        return call(null, params);
    }
}
