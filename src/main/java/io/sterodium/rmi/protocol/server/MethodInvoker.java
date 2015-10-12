package io.sterodium.rmi.protocol.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.sterodium.rmi.protocol.MethodInvocationDto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 23/09/2015
 */
class MethodInvoker {

    private static final Logger LOGGER = Logger.getLogger(MethodInvoker.class.getName());

    private static final Map<Class<?>, Class<?>> IMPLEMENTATIONS = Maps.newHashMap();

    static {
        IMPLEMENTATIONS.put(CharSequence.class, String.class);
    }

    private final Gson gson = new Gson();

    private ObjectLocator objectLocator;

    public MethodInvoker(ObjectLocator objectLocator) {
        this.objectLocator = objectLocator;
    }

    public InvocationResult invoke(Object target, MethodInvocationDto invocation) {
        String methodName = invocation.getMethod();
        LOGGER.info("*** Calling " + methodName + " on " + target.getClass().getName());
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
                throw new RuntimeException("Class " + argumentClassesNames[i] + " not found");
            }
        }
        return argumentClasses;
    }

    protected Object[] toObjects(String[] argumentStrings, Class<?>[] parameterTypes) {
        Object[] arguments = new Object[argumentStrings.length];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = toObject(argumentStrings[i], parameterTypes[i]);
        }
        return arguments;
    }

    private Object toObject(String value, Class<?> targetClass) {
        // TODO: MVO: add support for complex attributes
        if (String.class.equals(targetClass)) {
            return value;
        } else if (int.class.equals(targetClass) || Integer.class.equals(targetClass)) {
            return Integer.valueOf(value);
        } else if (long.class.equals(targetClass) || Long.class.equals(targetClass)) {
            return Long.valueOf(value);
        } else if (char.class.equals(targetClass) || Character.class.equals(targetClass)) {
            return value.toCharArray()[0];
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
                return gson.fromJson(value, arrayType);
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
        try {
            Class<?> targetClass = target.getClass().equals(Class.class) ? (Class<?>) target : target.getClass();
            return targetClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("Method %s.%s not found (argument types: %s)",
                    target.getClass().getName(), methodName, Arrays.toString(parameterTypes)));
        } catch (SecurityException e) {
            throw new RuntimeException(String.format("Security Exception during method %s.%s call",
                    target.getClass().getName(), methodName));
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
            String errorMessage = String.format("Method %s.%s threw exception", target.getClass().getName(), method.getName());
            LOGGER.log(Level.WARNING, errorMessage, e);
            throw new RuntimeException(errorMessage, e);
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
