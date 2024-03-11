package Gob;

import java.util.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    final Environment globals = new Environment();
    private Map<Expr, Integer> locals = new HashMap<>();
    private Environment ENV = globals;
    public static Map<String, Object> objVars = new HashMap<>();

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    Interpreter() {
        globals.declare("saacad", new GobCalls() {
            @Override
            public int arity() { return 0; }
            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }
            @Override
            public String toString() { return "<native qabte>"; }
        });
    }
    void interpret(List<Stmt> expression) {
        try {
            for (var expr: expression) {
                execute(expr);
            }

        } catch (RuntimeError error) {
            Gob.runtimeError(error);
        }
    }
    void executeBlock(List<Stmt> statements,
                      Environment environment) {
        Environment previous = this.ENV;
        try {
            this.ENV = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.ENV = previous;
        }
    }

    private void execute(Stmt expr) {
        expr.accept(this);
    }

    public Object evaluate(Expr expr){
        return expr.accept(this);
    }
    public Object isTruthy(Object bool){
        if(bool == "ban") return false;
        if(bool == "run")
            return "run";
        if(bool == "been")
            return "been";
        return "been";
    }
    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return ENV.getAt(distance, name.lexeme);
        } else {
            return globals.getVariable(name);
        }
    }

    private boolean isEqual(Object left, Object right) {
        return (left == null && right == null) || left.equals(right);
    }
    private void checkOperand(Token operator, Object... objects) {
        for (var obj: objects) {
            if (!(obj instanceof Double)) throw new RuntimeError(operator, "Number waa inay ahaadan Labaduba.");
        }
    }
    private String stringify(Object object) {
        if (object == null) return "ban";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    @Override
    public Object visit(Expr.Binary expr) {
        Object right = evaluate(expr.right);
        Object left = evaluate(expr.left);
        switch (expr.operator.type) {
            case MINUS -> {
                checkOperand(expr.operator, left, right);
                return (double) left - (double) right ;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) return (double) left + (double) right;
                if(right instanceof String || left instanceof String){
                    if(right instanceof Double) return left.toString() + String.valueOf(((Double) right).intValue());
                    if(left instanceof Double) return String.valueOf(((Double) left).intValue()) + right.toString();
                    if(right instanceof String && left instanceof String) return (String) left + (String) right;
                }
            }
            case STAR -> {
                checkOperand(expr.operator, left, right);
                return (double) left * (double) right ;
            }
            case PERCENT -> {
                checkOperand(expr.operator, left, right);
                return (double) left % (double) right ;
            }
            case SLASH -> {
                checkOperand(expr.operator, left, right);
                return (double) left / (double) right ;
            }
            case GREATER -> {
                checkOperand(expr.operator, left, right);
                return ((double) left > (double) right) ? "run" : "been";
            }
            case GREATER_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return (double) left >= (double) right  ? "run" : "been";
            }
            case LESS -> {
                checkOperand(expr.operator, left, right);
                return (double) left < (double) right  ? "run" : "been";
            }
            case LESS_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return (double) left <= (double) right  ? "run" : "been";
            }
            case BANG_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return !isEqual(left, right)  ? "run" : "been";
            }
            case EQUAL_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return isEqual(left, right)  ? "run" : "been";
            }

        }
        return null;
    }
    @Override
    public Object visit(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visit(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visit(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case MINUS -> {
                return -(double) right;
            }
            case BANG -> {
                if(right == "ban") return "been";
                if(right == "run")
                    return "been";
                if(right == "been")
                    return "run";
            }
        }
        return null;
    }

    @Override
    public Void visit(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(ENV));
        return null;
    }

    @Override
    public Void visit(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof GobClass)) {
                throw new RuntimeError(stmt.superclass.name,
                        "Waalid Waa Inuu cayn Ahaadaa. ");
            }
        }
        ENV.declare(stmt.name.lexeme, null);
        if (stmt.superclass != null) {
            ENV = new Environment(ENV);
            ENV.declare("ab", superclass);
        }
        Map<String, GobFunction> methods = new HashMap<>();
        Map<String, LinkedList<GobFunction>> initializers = new HashMap<>();
        for (Object method : stmt.methods) {
            if (method instanceof Stmt.Function) {
                if (((Stmt.Function) method).name.lexeme.equals(stmt.name.lexeme)) {
                    var function = new GobFunction((Stmt.Function) method, ENV, true);
                    if (!initializers.containsKey(((Stmt.Function) method).name.lexeme)) {
                        initializers.put(((Stmt.Function) method).name.lexeme, new LinkedList<>(List.of(function)));
                    } else {
                        initializers.get(((Stmt.Function) method).name.lexeme).add(function);
                    }
                } else {
                    var function = new GobFunction((Stmt.Function) method, ENV, false);
                    if (!methods.containsKey(((Stmt.Function) method).name.lexeme)) {
                        methods.put(((Stmt.Function) method).name.lexeme, function);
                    } else {
                        throw new RuntimeError((((Stmt.Function) method).name), ((Stmt.Function) method).name.lexeme + ":  qabte Hore Loomagacaabay");

                    }
                }
            }
            else if(method instanceof Stmt.Var){
                Object ev = evaluate(((Stmt.Var) method).initializer);
                objVars.put(((Stmt.Var) method).name.lexeme, ev);

            }
        }
        GobClass klass = new GobClass(stmt.name.lexeme,(GobClass) superclass, methods, initializers);
        if (superclass != null) {
            ENV = ENV.enclosing;
        }
        ENV.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Object visit(Expr.Get expr) {
        Object object = evaluate(expr.object);

        if (object instanceof GobInstance) {
            return ((GobInstance) object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "walxaha kaliya Ayuunba Leh sifooyin");
    }

    @Override
    public Void visit(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visit(Stmt.Print stmt) {
        System.out.println(stringify(evaluate(stmt.expression)));
        return null;
    }
    @Override
    public Void visit(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        ENV.declare(stmt.name.lexeme, value);
        return null;
    }



    @Override
    public Void visit(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition)) == "run") {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }else if(stmt.elseIF != null){
            stmt.elseIF.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Stmt.Else stmt) {
        return null;
    }

    @Override
    public Void visit(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition)) == "run") {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Object visit(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Object visit(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if (distance != null) {
            ENV.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visit(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left) == "run") return left;
        } else {
            if (isTruthy(left) == "been") return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visit(Expr.Set expr) {
       Object  currentObject = evaluate(expr.object);
        if (!(currentObject instanceof GobInstance)) {
            throw new RuntimeError(expr.name,
                    "Walxaha Ayuunbaa Lahan kara sifooyin.");
        }
        Object value = evaluate(expr.value);
        ((GobInstance)currentObject).set(expr.name, value);

        return value;
    }

    @Override
    public Object visit(Expr.Super expr) {
        int distance = locals.get(expr);
        GobClass superclass = (GobClass)ENV.getAt(
                distance, "ab");
        GobInstance object = (GobInstance)ENV.getAt(
                distance - 1, "kan");
        GobFunction method = superclass.findMethod(expr.method.lexeme);
        if (method == null) {
            throw new RuntimeError(expr.method,
                    "Sifo Aan La Magacabin '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);
    }

    @Override
    public Object visit(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visit(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if(!(callee instanceof GobCalls)){
            throw new RuntimeError(expr.paren, "Waxa Loo Yeedhi Karaa qabte Ama cayn Uun");
        }
        GobCalls function = (GobCalls) callee;
        var call = function.call(this, arguments);
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "La Filayey " +
                    function.arity() + " masalo Lakin Waxa La Helay " +
                    arguments.size() + ".");
        }
        return call;
    }
    @Override
    public Void visit(Stmt.Function func) {
        var function = new GobFunction(func, ENV, false);
        ENV.declare(func.name.lexeme, function);
        return null;    }

    @Override
    public Void visit(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);
        throw new Return(value);
    }
}
