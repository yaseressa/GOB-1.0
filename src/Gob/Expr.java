package Gob;

import java.util.List;

abstract class Expr {
 interface Visitor<R> {
 R visit(Binary expr);
 R visit(Grouping expr);
 R visit(Literal expr);
 R visit(Unary expr);
 }
 static class Binary extends Expr {
 Binary(Expr left, Token operator, Expr right) {
 this.left = left;
 this.operator = operator;
 this.right = right;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Expr left;
 final Token operator;
 final Expr right;
 }
 static class Grouping extends Expr {
 Grouping(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Expr expression;
 }
 static class Literal extends Expr {
 Literal(Object value) {
 this.value = value;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Object value;
 }
 static class Unary extends Expr {
 Unary(Token operator, Expr right) {
 this.operator = operator;
 this.right = right;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Token operator;
 final Expr right;
 }

 abstract <R> R accept(Visitor<R> visitor);
}
