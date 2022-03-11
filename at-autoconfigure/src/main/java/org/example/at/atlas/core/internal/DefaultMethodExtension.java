package org.example.at.atlas.core.internal;

import io.qameta.atlas.core.api.MethodExtension;
import io.qameta.atlas.core.internal.Configuration;
import io.qameta.atlas.core.util.MethodInfo;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class DefaultMethodExtension implements MethodExtension {
    @Override
    public boolean test(final Method method) {
        return method.isDefault();
    }

    @Override
    public Object invoke(final Object proxy, final MethodInfo methodInfo, final Configuration config) throws Throwable {
        var declaringClass = methodInfo.getMethod().getDeclaringClass();

        return MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup())
                .in(declaringClass)
                .unreflectSpecial(methodInfo.getMethod(), declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(methodInfo.getArgs());
    }
}
