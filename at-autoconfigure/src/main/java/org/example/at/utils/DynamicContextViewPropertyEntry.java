package org.example.at.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;

public class DynamicContextViewPropertyEntry implements Map.Entry<String, Object> {

    private final Object obj;
    private final DynamicContextViewInvocationHandler view;
    private final PropertyDescriptor property;

    DynamicContextViewPropertyEntry(Object obj, PropertyDescriptor property) {
        this.obj = obj;
        this.view = (DynamicContextViewInvocationHandler) Proxy.getInvocationHandler(obj);
        this.property = property;
    }

    @Override
    public String getKey() {
        return DynamicContext.getKey(property);
    }

    public boolean isReadable() {
        return property.getReadMethod() != null;
    }

    @Override
    public Object getValue() {
        if (!isReadable()) {
            throw new UnsupportedOperationException("Property " + getKey() + " is not readable");
        }

        try {
            return view.invoke(obj, property.getReadMethod(), new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isWritable() {
        return property.getWriteMethod() != null;
    }

    @Override
    public Object setValue(Object value) {
        if (!isWritable()) {
            throw new UnsupportedOperationException("Property " + getKey() + " is not writable");
        }

        try {
            return view.invoke(obj, property.getWriteMethod(), new Object[]{value});
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Map.Entry) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return Objects.equals(getKey(), e.getKey()) &&
                    Objects.equals(getValue(), e.getValue());
        }
        return false;
    }

    @Override
    public String toString() {
        var str = getKey();
        if (isReadable()) {
            str += "=" + getValue();
        }
        return str;
    }

}
