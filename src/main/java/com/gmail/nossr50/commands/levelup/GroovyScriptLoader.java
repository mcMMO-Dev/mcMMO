package com.gmail.nossr50.commands.levelup;

import groovy.lang.Binding;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

// Base class makes it easy to call a specific entry point
public abstract class GroovyScriptLoader extends Script {
    public Object run() {
        return main();
    }

    public abstract Object main(); // your script must implement this


    CompilerConfiguration cc = secureCompilerConfig();
cc.setScriptBaseClass(BaseScript .class.

    getName());

    GroovyClassLoader gcl = new GroovyClassLoader(getClass().getClassLoader(), cc);

    Class<?> cls = gcl.parseClass("""
              // Has access ONLY to binding vars you provide
              Object main() {
                return base * (1 + chance) // 'base' and 'chance' come from Binding
              }
            """);

    Binding binding = new Binding();
binding.setProperty("base",10);
binding.setProperty("chance",0.5);

    BaseScript script = (BaseScript) cls.getDeclaredConstructor().newInstance();
script.setBinding(binding);
    Object result = script.run(); // -> 15.0
}
