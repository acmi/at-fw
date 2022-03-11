package org.example.at.utils;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.Map;

public class Eval {

    private Eval() {
    }

    public static Object eval(String scriptText) {
        return eval(scriptText, null);
    }

    public static Object eval(String scriptText, Object context) {
        return eval(scriptText, getProperties(context));
    }

    public static Object eval(String scriptText, Map<String, Object> variables) {
        return eval(scriptText, variables, PropertyScript.class.getName());
    }

    public static Object eval(String scriptText, Map<String, Object> variables, String scriptBaseClass) {
        var binding = new Binding();
        if (variables != null) {
            variables.forEach(binding::setVariable);
        }

        var config = CompilerConfiguration.DEFAULT;
        if (scriptBaseClass != null) {
            config = new CompilerConfiguration();
            config.setScriptBaseClass(scriptBaseClass);
        }

        var shell = new GroovyShell(Eval.class.getClassLoader(), binding, config);
        var result = shell.evaluate(scriptText);
        return postProcess(result);
    }

    private static Object postProcess(Object object) {
        if (object instanceof CharSequence) {
            return object.toString();
        }
        return object;
    }

    public static Map<String, Object> getProperties(Object context) {
        try {
            return DynamicContext.unwrap(context);
        } catch (IllegalArgumentException ignore) {
        }

        var properties = DefaultGroovyMethods.getProperties(context);
        properties.remove("class");
        return properties;
    }

}
