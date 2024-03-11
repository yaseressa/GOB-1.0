package Gob;

import java.util.List;
import java.util.*;

public class GobClass implements GobCalls{
    final String name;
    final GobClass superclass;
    private static int arity;
    private final Map<String, GobFunction> methods;
    private final Map<String, LinkedList<GobFunction>> initializers;
    GobClass(String name, GobClass superclass, Map<String, GobFunction> methods, Map<String, LinkedList<GobFunction>> initializers) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
        this.initializers = initializers;
    }
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        var method = findMethod(name);
        return method == null ? arity: method.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        arity = arguments.size();
        GobInstance instance = new GobInstance(this, Interpreter.objVars);
        GobFunction initializer = null;
        var chooseInit = initializers.get(name);
        for(GobFunction i : chooseInit){
            if(arguments.size() == i.arity()){
                initializer = i;
                break;
            }
        }
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    public GobFunction findMethod(String lexeme) {
        try {

            return methods.get(lexeme);
        }catch (Exception e) {

            return null;
        }

    }
}
