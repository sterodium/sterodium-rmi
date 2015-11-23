package io.sterodium.rmi.protocol.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.sterodium.rmi.protocol.MethodInvocationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static io.sterodium.rmi.protocol.json.PrimitiveTypes.isCharacter;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class MethodInvoker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvoker.class);

    private static final Map<Class<?>, Class<?>> IMPLEMENTATIONS = Maps.newHashMap();

    static {
        IMPLEMENTATIONS.put(CharSequence.class, String.class);
    }

    private static final Gson GSON = new Gson();

    private ObjectLocator objectLocator;

    public MethodInvoker(ObjectLocator objectLocator) {
        this.objectLocator = objectLocator;
    }

    public InvocationResult invoke(Object target, MethodInvocationDto invocation) {
        String methodName = invocation.getMethod();
        Class<?>[] parameterTypes = toClasses(invocation.getArgumentClasses());
        Object[] arguments = toObjects(invocation.getArguments(), parameterTypes);
        Method method = getMethod(target, methodName, parameterTypes);

        Object result = invoke(target, method, arguments);
        Class<?> returnType = method.getReturnType();
        return new InvocationResult(result, returnType);
    }

    protected Class<?>[] toClasses(String[] argumentClassesNames) {
        Class<?>[] argumentClasses = new Class[argumentClassesNames.length];
        for (int i = 0; i < argumentClasses.length; i++) {
            try {
                argumentClasses[i] = ClassUtils.forName(argumentClassesNames[i]);
            } catch (ClassNotFoundException e) {
                throw new MethodParameterException(i + 1, argumentClassesNames[i]);
            }
        }
        return argumentClasses;
    }

    protected Object[] toObjects(String[] argumentStrings, Class<?>[] parameterTypes) {
        Object[] arguments = new Object[argumentStrings.length];
        for (int i = 0; i < arguments.length; i++) {
            try {
                arguments[i] = toObject(argumentStrings[i], parameterTypes[i]);
            } catch (Exception e) {
                throw new MethodParameterException(i + 1, parameterTypes[i], argumentStrings[i]);
            }
        }
        return arguments;
    }

    private Object toObject(String value, Class<?> targetClass) {
        if ("null".equals(value)) {
            return null;
        } else if (String.class.equals(targetClass)) {
            return value;
        } else if (boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            return Boolean.valueOf(value);
        } else if (byte.class.equals(targetClass) || Byte.class.equals(targetClass)) {
            return Byte.valueOf(value);
        } else if (isCharacter(targetClass)) {
            return GSON.fromJson(value, Character.class);
        } else if (short.class.equals(targetClass) || Short.class.equals(targetClass)) {
            return Short.valueOf(value);
        } else if (int.class.equals(targetClass) || Integer.class.equals(targetClass)) {
            return Integer.valueOf(value);
        } else if (long.class.equals(targetClass) || Long.class.equals(targetClass)) {
            return Long.valueOf(value);
        } else if (float.class.equals(targetClass) || Float.class.equals(targetClass)) {
            return Float.valueOf(value);
        } else if (double.class.equals(targetClass) || Double.class.equals(targetClass)) {
            return Double.valueOf(value);
        } else if (Class.class.equals(targetClass)) {
            try {
                return Class.forName(value);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (targetClass.isArray()) {
            Class<?> componentType = targetClass.getComponentType();
            if (componentType.isInterface()) {
                // searching for proper implementation
                componentType = IMPLEMENTATIONS.get(componentType);
            }
            try {
                Class<?> arrayType = Class.forName("[L" + componentType.getName() + ";");
                return GSON.fromJson(value, arrayType);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(String.format("Could not find class for array of '%s'", componentType.getName()));
            }
        } else {
            Object object = objectLocator.get(value);
            if (object != null) {
                return object;
            }
        }
        throw new RuntimeException(String.format("Class %s is not supported as method parameter", targetClass));
    }

    protected Method getMethod(Object target, String methodName, Class<?>[] parameterTypes) {
        Class<?> targetClass = target.getClass().equals(Class.class) ? (Class<?>) target : target.getClass();
        try {
            return targetClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // trying to find declared method in class and all its superclasses (so we have more specific message)
            Class<?> classToScan = targetClass;
            while (classToScan != null) {
                try {
                    classToScan.getDeclaredMethod(methodName, parameterTypes);
                    throw new MethodNotVisibleException(classToScan, methodName, parameterTypes);
                } catch (NoSuchMethodException ignored) {
                    classToScan = classToScan.getSuperclass();
                }
            }
            throw new MethodNotFoundException(targetClass, methodName, parameterTypes);
        } catch (SecurityException e) {
            throw new RuntimeException(String.format("Security Exception during method %s.%s call", targetClass.getName(), methodName));
        }
    }

    private Object invoke(Object target, Method method, Object[] arguments) {
        try {
            boolean publicMethod = Modifier.isPublic(method.getModifiers());
            if (publicMethod) {
                method.setAccessible(true);
            }
            return method.invoke(target, arguments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Method %s.%s is not accessible", target.getClass().getName(), method.getName()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(String.format("Method %s.%s call is illegal", target.getClass().getName(), method.getName()));
        } catch (InvocationTargetException e) {
            throw new MethodInvocationException(target.getClass(), method.getName(), arguments, e.getCause());
        }
    }

    protected static class InvocationResult {

        private Object result;

        private Class<?> resultClass;

        public InvocationResult(Object result, Class<?> resultClass) {
            this.result = result;
            this.resultClass = resultClass;
        }

        public Object getResult() {
            return result;
        }

        public Class<?> getResultClass() {
            return resultClass;
        }
    }

}
