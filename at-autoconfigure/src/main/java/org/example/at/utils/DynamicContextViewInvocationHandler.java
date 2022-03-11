package org.example.at.utils;

import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public class DynamicContextViewInvocationHandler implements InvocationHandler {

    private final DynamicContext dynamicContext;
    private final Collection<PropertyDescriptor> properties;

    public DynamicContextViewInvocationHandler(DynamicContext dynamicContext, Collection<PropertyDescriptor> properties) {
        this.dynamicContext = dynamicContext;
        this.properties = properties;
    }

    public DynamicContext getDynamicContext() {
        return dynamicContext;
    }

    public Collection<PropertyDescriptor> getProperties() {
        return properties;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            return invokeDefault(proxy, method, args);
        }

        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }

        if (method.getDeclaringClass().equals(DynamicContext.GroovySupport.class)) {
            if ("asType".equals(method.getName())) {
                if (args[0] == DynamicContext.class) {
                    return dynamicContext;
                }
                throw new ClassCastException("DynamicContext view can only be casted to DynamicContext");
            }
        }

        for (PropertyDescriptor descriptor : properties) {
            if (method.equals(descriptor.getReadMethod())) {
                return dynamicContext.getStorage().get(DynamicContext.getKey(descriptor));
            } else if (method.equals(descriptor.getWriteMethod())) {
                if (args[0] == null) {
                    dynamicContext.getStorage().remove(DynamicContext.getKey(descriptor));
                } else {
                    dynamicContext.getStorage().put(DynamicContext.getKey(descriptor), args[0]);
                }
                return null;
            }
        }

        throw new NoSuchMethodException(method.getName());
    }

    private Object invokeDefault(Object proxy, Method method, Object[] args) throws Throwable {
        return MethodHandles.lookup()
                .findSpecial(method.getDeclaringClass(),
                        method.getName(),
                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                        method.getDeclaringClass())
                .bindTo(proxy)
                .invokeWithArguments(args);
    }

    @Override
    public String toString() {
        return dynamicContext.toString();
    }

}
