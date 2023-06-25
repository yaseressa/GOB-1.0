package Gob;
import static Gob.TokenType.*;
public class Interpreter implements Expr.Visitor<Object>{
    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Gob.runtimeError(error);
        }
    }
    public Object evaluate(Expr expr){
        return expr.accept(this);
    }
    public boolean isTruthy(Object bool){
        if(bool == null) return false;
        if(bool instanceof Boolean) return (boolean) bool;
        return false;
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

                }
            }
            case STAR -> {
                checkOperand(expr.operator, left, right);
                return (double) left * (double) right ;
            }
            case SLASH -> {
                checkOperand(expr.operator, left, right);
                return (double) left / (double) right ;
            }
            case GREATER -> {
                checkOperand(expr.operator, left, right);
                return (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return (double) left >= (double) right;
            }
            case LESS -> {
                checkOperand(expr.operator, left, right);
                return (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return (double) left <= (double) right;
            }
            case BANG_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                checkOperand(expr.operator, left, right);
                return isEqual(left, right);
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
                return !isTruthy(right);
            }
        }
        return null;
    }
}
