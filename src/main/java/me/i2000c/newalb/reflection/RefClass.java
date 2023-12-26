package me.i2000c.newalb.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("UseSpecificCatch")
@RequiredArgsConstructor(staticName = "of")
public class RefClass {
    private RefConstructor cachedConstructor;
    private final Map<String, RefMethod> METHOD_MAP = new HashMap<>();
    private final Map<String, RefField> FIELD_MAP = new HashMap<>();
    
    @Getter
    @NonNull private final Class actualClass;
    
    public RefField getField(String name) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Field field = ReflectionUtils.findField(actualClass, name);
        return field != null ? RefField.of(field) : null;
//</editor-fold>
    }    
    public RefMethod getMethod(String name, Object... params) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Method method = ReflectionUtils.findExecutable(actualClass, name, params);
        return method != null ? RefMethod.of(method) : null;
//</editor-fold>
    }
    public RefConstructor getConstructor(Object... params) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Constructor constructor = ReflectionUtils.findExecutable(actualClass, null, params);
        return constructor != null ? RefConstructor.of(constructor) : null;
//</editor-fold>
    }
    
    public List<RefMethod> getMethods() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return Arrays.stream(ReflectionUtils.getAllThingsOfClass(actualClass, Method.class))
                .distinct()
                .map(method -> {
                    method.setAccessible(true);
                    return RefMethod.of(method);
                })
                .collect(Collectors.toList());
//</editor-fold>
    }
    public List<RefConstructor> getConstructors() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return Arrays.stream(ReflectionUtils.getAllThingsOfClass(actualClass, Constructor.class))
                .distinct()
                .map(constructor -> {
                    constructor.setAccessible(true);
                    return RefConstructor.of(constructor);
                })
                .collect(Collectors.toList());
//</editor-fold>
    }
    public List<RefField> getFields() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return Arrays.stream(ReflectionUtils.getAllThingsOfClass(actualClass, Field.class))
                .distinct()
                .map(field -> {
                    field.setAccessible(true);
                    return RefField.of(field);
                })
                .collect(Collectors.toList());
//</editor-fold>
    }
    
    public <T> T callMethod(String name, Object target, Object... params) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        RefMethod refMethod = METHOD_MAP.get(name);
        if(refMethod == null) {
            refMethod = getMethod(name, params);
            if(refMethod == null) {
                String paramsToString = Arrays.stream(params)
                        .map(param -> param != null ? param.getClass().toGenericString() : "null")
                        .collect(Collectors.joining(", ", "[", "]"));
                
                String message = String.format("Could not find method with name %s in class %s with parameters %s",
                        name,
                        actualClass.toGenericString(),
                        paramsToString);
                throw new NoSuchMethodError(message);
            }
            
            METHOD_MAP.put(name, refMethod);
        }
        return refMethod.call(target, params);
//</editor-fold>
    }
    public <T> T callStaticMethod(String name, Object... params) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return callMethod(name, null, params);
//</editor-fold>
    }
    
    public <T> T callConstructor(Object... params) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        RefConstructor refConstructor = cachedConstructor;
        if(refConstructor == null) {
            refConstructor = getConstructor(params);
            if(refConstructor == null) {
                String paramsToString = Arrays.stream(params)
                        .map(param -> param != null ? param.getClass().toGenericString() : "null")
                        .collect(Collectors.joining(", ", "[", "]"));
                
                String message = String.format("Could not find constructor in class %s with parameters %s",
                        actualClass.toGenericString(),
                        paramsToString);
                throw new NoSuchMethodError(message);
            }
            
            cachedConstructor = refConstructor;
        }
        return refConstructor.call(params);
//</editor-fold>
    }
    
    public <T> T getFieldValue(String name, Object target) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        RefField refField = FIELD_MAP.get(name);
        if(refField == null) {
            refField = getField(name);
            if(refField == null) {
                String message = String.format("Could not find field with name %s in class %s",
                        name,
                        actualClass.toGenericString());
                throw new NoSuchFieldError(message);
            }
            
            FIELD_MAP.put(name, refField);
        }
        return refField.getValue(target);
//</editor-fold>
    }
    public <T> T getStaticFieldValue(String name) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getFieldValue(name, null);
//</editor-fold>
    }
    public void setFieldValue(String name, Object target, Object value) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        RefField refField = FIELD_MAP.get(name);
        if(refField == null) {
            refField = getField(name);
            if(refField == null) {
                String message = String.format("Could not find field with name %s in class %s",
                        name,
                        actualClass.toGenericString());
                throw new NoSuchFieldError(message);
            }
            
            FIELD_MAP.put(name, refField);
        }
        refField.setValue(target, value);
//</editor-fold>
    }
    public void setStaticFieldValue(String name, Object value) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        setFieldValue(name, null, value);
//</editor-fold>
    }
}
