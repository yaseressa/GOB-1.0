package Gob;

class AstPrinter  implements Expr.Visitor<String> {
    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", "", 1),
                        new Expr.Literal(123)), new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(null)));
        System.out.println(new AstPrinter().print(expression));
    }
    String print(Expr expr) {
        return expr.accept(this);
   }
    @Override
    public String visit(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }
    @Override
    public String visit(Expr.Grouping expr) {
        return parenthesize("koox", expr.expression);
    }
    @Override
    public String visit(Expr.Literal expr) {
        if (expr.value == null) return "ban";
        return expr.value.toString();
    }
    @Override
    public String visit(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
    private String ReversePolishNotation(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(" ");
        builder.append(name);
        return builder.toString();
    }
}
