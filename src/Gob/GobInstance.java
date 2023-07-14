package Gob;

import java.util.*;

public class GobInstance {
    private final GobClass klass;
    private  Map<String, Object> fields = new HashMap<>();

    GobInstance(GobClass klass, Map<String, Object> prexisting) {
        this.klass = klass;
        this.fields = prexisting;
    }
    @Override
    public String toString() {
        return "walax: " + klass.name;
    }

        Object get(Token name) {
            if (fields.containsKey(name.lexeme)) {
                return fields.get(name.lexeme);
            }
            GobFunction method = klass.findMethod(name.lexeme);
            if (method != null) return method.bind(this);
            throw new RuntimeError(name,
                    "sifada '" + name.lexeme + "' Mamagacabna.");
        }

    public void set(Token name, Object value) {

        fields.put(name.lexeme, value);
    }
}
