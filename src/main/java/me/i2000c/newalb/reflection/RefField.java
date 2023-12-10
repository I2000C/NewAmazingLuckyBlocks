package me.i2000c.newalb.reflection;

import java.lang.reflect.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

@AllArgsConstructor(staticName = "of")
@Getter
public class RefField {
    @NonNull private final Field actualField;
    
    @SneakyThrows    
    @SuppressWarnings("UseSpecificCatch")
    public void setValue(Object target, Object value) {
        actualField.set(target, value);
    }
    
    @SneakyThrows
    @SuppressWarnings("UseSpecificCatch")
    public <T> T getValue(Object target) {
        return (T) actualField.get(target);
    }
    
    public void setStaticValue(Object value) {
        setValue(null, value);
    }
    
    public <T> T getStaticValue() {
        return getValue(null);
    }
}
