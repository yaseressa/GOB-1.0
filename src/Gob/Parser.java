package Gob;

import static Gob.TokenType.*;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    private Expr expression() {
        return equality();
    }
    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }
    private void synchronize(){
        advance();

        if(!isAtEnd()){
            if(previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }
        }
        advance();
    }
    private ParseError error(Token token, String message) {
        Gob.error(token, message);
        return new ParseError();
    }
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }
    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);
        if(match(NUMBER, STRING)){
            // match methods advance increment current by 1 we use previous or current - 1
            return new Expr.Literal(previous().literal);
        }
        if(match(LEFT_BRACE)){
            Expr expr = expression();
            consume(RIGHT_PAREN, "La Filanayey ')' Tacbiir Kadib.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "La Filayey Tacbiir ");
    }
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            return new Expr.Unary(operator, unary());
        }
        return primary();
    }
    private Expr factor() {
        Expr expr = unary();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr term() {
        Expr expr = factor();
        while (match(  MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr comparison() {
        Expr expr = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr equality() {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Token peek(){
        return tokens.get(current);
    }
    private boolean isAtEnd(){
        if(peek().type == EOF) return true;
        return false;
    }
    private boolean check(TokenType t){
        if(isAtEnd()) return false;
        return t == peek().type;
    }
    private Token previous(){
        return tokens.get(current - 1);
    }
    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
}
