package org.example.at.utils;

import groovy.transform.Trait;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Proxy;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicContext extends AbstractMap<String, Object> {

    private final Map<String, Object> storage = new LinkedHashMap<>();

    private final Map<Class, Object> views = new HashMap<>();

    public final Map<String, Object> getStorage() {
        return storage;
    }

    public final Map<Class, Object> getViews() {
        return views;
    }

    public <T> T asType(Class<T> propertiesInterface) {
        if (propertiesInterface.getAnnotation(Trait.class) != null) {
            throw new IllegalArgumentException("Groovy traits are not supported");
        }

        return propertiesInterface.cast(
                views.computeIfAbsent(propertiesInterface,
                        interf -> Proxy.newProxyInstance(getClass().getClassLoader(),
                                new Class[]{interf, GroovySupport.class},
                                new DynamicContextViewInvocationHandler(DynamicContext.this, getPropertyDescriptors(interf, null)))
                )
        );
    }

    private Collection<PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass, Collection<PropertyDescriptor> descriptors) {
        if (descriptors == null) {
            descriptors = new ArrayList<>();
        }
        try {
            descriptors.addAll(Arrays.asList(Introspector.getBeanInfo(beanClass).getPropertyDescriptors()));

            for (var i : beanClass.getInterfaces()) {
                getPropertyDescriptors(i, descriptors);
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return descriptors;
    }

    @Override
    public Object put(String key, Object value) {
        var entry = viewEntries(null, true)
                .filter(it -> Objects.equals(it.getKey(), key))
                .findAny();
        if (entry.isPresent()) {
            return entry.get().setValue(value);
        } else {
            return storage.put(key, value);
        }
    }

    @Override
    public Object remove(Object key) {
        var entry = viewEntries(null, true)
                .filter(it -> Objects.equals(it.getKey(), key))
                .findAny();
        if (entry.isPresent()) {
            return entry.get().setValue(null);
        } else {
            return storage.remove(key);
        }
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        var entries = Stream.concat(storage.entrySet().stream(), viewEntries(true, null))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSet(entries);
    }

    private Stream<Entry<String, Object>> viewEntries(Boolean requireRead, Boolean requireWrite) {
        return views.values().stream()
                .flatMap(obj -> {
                    var view = (DynamicContextViewInvocationHandler) Proxy.getInvocationHandler(obj);
                    return view.getProperties().stream()
                            .map(property -> new DynamicContextViewPropertyEntry(obj, property))
                            .filter(property -> (requireRead == null || requireRead == property.isReadable()) &&
                                    (requireWrite == null || requireWrite == property.isWritable()));
                });
    }

    @Override
    public void clear() {
        storage.clear();
        views.clear();
    }

    /**
     * @throws IllegalArgumentException if not a DynamicContext or it's view
     */
    public static DynamicContext unwrap(Object view) {
        if (view instanceof DynamicContext) {
            return (DynamicContext) view;
        } else if (Proxy.isProxyClass(view.getClass())) {
            var handler = Proxy.getInvocationHandler(view);
            if (handler instanceof DynamicContextViewInvocationHandler) {
                return ((DynamicContextViewInvocationHandler) handler).getDynamicContext();
            }
        }
        throw new IllegalArgumentException("Not a DynamicContext or it's view");
    }

    static String getKey(PropertyDescriptor descriptor) {
        return descriptor.getName();
    }

    public interface GroovySupport {
        Object asType(Class<?> type);
    }

}
