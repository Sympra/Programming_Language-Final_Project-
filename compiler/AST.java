package compiler;

import java.util.List;

public class AST {

    // Root Node
    public static class Program {
        public final List<Stmt> statements;
        public Program(List<Stmt> statements) {
            this.statements = statements;
        }
    }

    // ---------------- STATEMENTS ----------------
    public interface Stmt {}

    public static class VarDecl implements Stmt {
        public final String type;
        public final String name;
        public final Expr initializer;

        public VarDecl(String type, String name, Expr initializer) {
            this.type = type;
            this.name = name;
            this.initializer = initializer;
        }
    }

    public static class Print implements Stmt {
        public final Expr expression;
        public Print(Expr expression) {
            this.expression = expression;
        }
    }

    public static class ExpressionStmt implements Stmt {
        public final Expr expression;
        public ExpressionStmt(Expr expression) {
            this.expression = expression;
        }
    }

    public static class Block implements Stmt {
        public final List<Stmt> statements;
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }
    }

    public static class IfStmt implements Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;

        public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
    }

    public static class WhileStmt implements Stmt {
        public final Expr condition;
        public final Stmt body;

        public WhileStmt(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }
    }

    public static class ForStmt implements Stmt {
        public final Stmt initializer;
        public final Expr condition;
        public final Expr increment;
        public final Stmt body;

        public ForStmt(Stmt initializer, Expr condition, Expr increment, Stmt body) {
            this.initializer = initializer;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }
    }

    // ---------------- EXPRESSIONS ----------------
    public interface Expr {}

    public static class Literal implements Expr {
        public final Object value;
        public Literal(Object value) {
            this.value = value;
        }
    }

    public static class Variable implements Expr {
        public final String name;
        public Variable(String name) {
            this.name = name;
        }
    }

    public static class Binary implements Expr {
        public final Expr left;
        public final String operator;
        public final Expr right;
        public Binary(Expr left, String operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    public static class Grouping implements Expr {
        public final Expr expression;
        public Grouping(Expr expression) {
            this.expression = expression;
        }
    }

    public static class Assignment implements Expr {
        public final String name;
        public final Expr value;
        public Assignment(String name, Expr value) {
            this.name = name;
            this.value = value;
        }
    }
}
