package Gob;

import java.util.List;

abstract class Stmt {
 interface Visitor<R> {
 R visit(Block stmt);
 R visit(Class stmt);
 R visit(Expression stmt);
 R visit(Print stmt);
 R visit(Var stmt);
 R visit(Array stmt);
 R visit(Function stmt);
 R visit(Return stmt);
 R visit(If stmt);
 R visit(Else stmt);
 R visit(While stmt);
 }
 static class Block extends Stmt {
 Block(List<Stmt> statements) {
 this.statements = statements;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final List<Stmt> statements;
 }
 static class Class extends Stmt {
 Class(Token name, Expr.Variable superclass, List<Object> methods) {
 this.name = name;
 this.superclass = superclass;
 this.methods = methods;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Token name;
 final Expr.Variable superclass;
 final List<Object> methods;
 }
 static class Expression extends Stmt {
 Expression(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Expr expression;
 }
 static class Print extends Stmt {
 Print(Expr expression) {
 this.expression = expression;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Expr expression;
 }
 static class Var extends Stmt {
 Var(Token name, Expr initializer) {
 this.name = name;
 this.initializer = initializer;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Token name;
 final Expr initializer;
 }
 static class Array extends Stmt {
 Array(Token name, ArrayList<Expr> initializer) {
 this.name = name;
 this.initializer = initializer;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Token name;
 final ArrayList<Expr> initializer;
 }
 static class Function extends Stmt {
 Function(Token name, List<Token> params, List<Stmt> body) {
 this.name = name;
 this.params = params;
 this.body = body;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Token name;
 final List<Token> params;
 final List<Stmt> body;
 }
 static class Return extends Stmt {
 Return(Token keyword, Expr value) {
 this.keyword = keyword;
 this.value = value;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Token keyword;
 final Expr value;
 }
 static class If extends Stmt {
 If(Expr condition, Stmt thenBranch, Stmt elseBranch, Stmt elseIF) {
 this.condition = condition;
 this.thenBranch = thenBranch;
 this.elseBranch = elseBranch;
 this.elseIF = elseIF;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Expr condition;
 final Stmt thenBranch;
 final Stmt elseBranch;
 final Stmt elseIF;
 }
 static class Else extends Stmt {
 Else(Stmt statement) {
 this.statement = statement;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Stmt statement;
 }
 static class While extends Stmt {
 While(Expr condition, Stmt body) {
 this.condition = condition;
 this.body = body;
 }

 @Override
 <R> R accept(Visitor<R> visitor) {
 return visitor.visit(this);
 }

 final Expr condition;
 final Stmt body;
 }

 abstract <R> R accept(Visitor<R> visitor);
}
