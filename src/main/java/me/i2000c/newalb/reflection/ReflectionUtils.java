package me.i2000c.newalb.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;

class ReflectionUtils {
    private static final Map<Class, Class> PRIMITIVE_CLASS_MAP = new HashMap<>();    
    static {
        PRIMITIVE_CLASS_MAP.put(byte.class,    Byte.class);
        PRIMITIVE_CLASS_MAP.put(short.class,   Short.class);
        PRIMITIVE_CLASS_MAP.put(int.class,     Integer.class);
        PRIMITIVE_CLASS_MAP.put(long.class,    Long.class);
        PRIMITIVE_CLASS_MAP.put(float.class,   Float.class);
        PRIMITIVE_CLASS_MAP.put(double.class,  Double.class);
        PRIMITIVE_CLASS_MAP.put(char.class,    Character.class);
        PRIMITIVE_CLASS_MAP.put(boolean.class, Boolean.class);
    }
    
    static <T extends AccessibleObject> T[] getAllThingsOfClass(Class clazz, Class<T> genericTypeClass) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Object[] array1, array2;
        if(genericTypeClass.equals(Method.class)) {
            array1 = clazz.getMethods(); array2 = clazz.getDeclaredMethods();
        } else if(genericTypeClass.equals(Constructor.class)) {
            array1 = clazz.getConstructors(); array2 = clazz.getDeclaredConstructors();
        } else if(genericTypeClass.equals(Field.class)) {
            array1 = clazz.getFields(); array2 = clazz.getDeclaredFields();
        } else {
            throw new IllegalArgumentException(String.format("Invalid generic type: %s. Valid generic types are Method.class, Constructor.class and Field.class",
                                                genericTypeClass.toGenericString()));
        }
        
        Object[] concat = Stream.concat(Arrays.stream(array1), Arrays.stream(array2)).toArray();
        return Arrays.asList(concat).toArray((T[]) Array.newInstance(genericTypeClass, 0));
//</editor-fold>
    }
    
    static Field findField(Class clazz, String name) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Field[] fields = getAllThingsOfClass(clazz, Field.class);
        for(Field field : fields) {
            if(field.getName().equals(name)) {
                field.setAccessible(true);
                return field;
            }
        }
        
        return null;
//</editor-fold>
    }
    
    static <T extends Executable> T findExecutable(Class clazz, @Nullable String name, Object... params) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        T[] executables = (T[]) (name == null
                ? getAllThingsOfClass(clazz, Constructor.class)
                : getAllThingsOfClass(clazz, Method.class));
        
        for(T executable : executables) {
            if(name != null && !executable.getName().equals(name)) {
                continue;
            }
            
            if(executable.getParameterCount() != params.length) {
                continue;
            }
            
            boolean executableFound = true;
            Class[] classes = executable.getParameterTypes();
            for(int i=0; i<classes.length; i++) {
                Class executableParameterClass = classes[i];
                Class parameterClass = params[i].getClass();
                
                if(executableParameterClass.isPrimitive()) {
                    executableParameterClass = PRIMITIVE_CLASS_MAP.get(executableParameterClass);
                }
                if(parameterClass.isPrimitive()) {
                    parameterClass = PRIMITIVE_CLASS_MAP.get(parameterClass);
                }
                
                if(!executableParameterClass.isAssignableFrom(parameterClass)) {
                    executableFound = false;
                    break;
                }
            }
            
            if(!executableFound) {
                continue;
            }
            
            executable.setAccessible(true);
            return executable;
        }
        
        return null;
//</editor-fold>
    }
}
