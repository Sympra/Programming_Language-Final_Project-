package compiler;

import java.util.*;

public class CodeGenerator {

    private final StringBuilder output = new StringBuilder();
    private final Map<String, Object> variables = new HashMap<>();

    public String generate(AST.Program program) {
        for (AST.Stmt stmt : program.statements) {
            generateStmt(stmt);
        }
        return output.toString();
    }

    private void generateStmt(AST.Stmt stmt) {
        if (stmt instanceof AST.VarDecl v) {
            Object value = evaluateExpr(v.initializer);
            variables.put(v.name, value);
            output.append(v.type)
                  .append(" ")
                  .append(v.name)
                  .append(" = ")
                  .append(value)
                  .append(";\n");

        } else if (stmt instanceof AST.Print p) {
            Object value = evaluateExpr(p.expression);
            output.append("print(").append(value).append(");\n");

        } else if (stmt instanceof AST.ExpressionStmt e) {
            Object value = evaluateExpr(e.expression);
            output.append(value).append(";\n");

        } else if (stmt instanceof AST.Block b) {
            output.append("{\n");
            for (AST.Stmt s : b.statements) {
                generateStmt(s);
            }
            output.append("}\n");

        } else if (stmt instanceof AST.IfStmt i) {
            Object cond = evaluateExpr(i.condition);
            output.append("if (").append(cond).append(") ");
            generateStmt(i.thenBranch);
            if (i.elseBranch != null) {
                output.append("else ");
                generateStmt(i.elseBranch);
            }

        } else if (stmt instanceof AST.WhileStmt w) {
            output.append("while (")
                  .append(evaluateExpr(w.condition))
                  .append(") ");
            generateStmt(w.body);

        } else if (stmt instanceof AST.ForStmt f) {
            output.append("for (");
            if (f.initializer != null) generateStmt(f.initializer);
            output.append("; ");
            if (f.condition != null) output.append(evaluateExpr(f.condition));
            output.append("; ");
            if (f.increment != null) output.append(evaluateExpr(f.increment));
            output.append(") ");
            generateStmt(f.body);
        }
    }

    private Object evaluateExpr(AST.Expr expr) {
        if (expr == null) return "null";

        if (expr instanceof AST.Literal l) {
            return l.value;
        } else if (expr instanceof AST.Variable v) {
            return variables.getOrDefault(v.name, "undefined");
        } else if (expr instanceof AST.Binary b) {
            Object left = evaluateExpr(b.left);
            Object right = evaluateExpr(b.right);
            return "(" + left + " " + b.operator + " " + right + ")";
        } else if (expr instanceof AST.Assignment a) {
            Object val = evaluateExpr(a.value);
            variables.put(a.name, val);
            return a.name + " = " + val;
        } else if (expr instanceof AST.Grouping g) {
            return "(" + evaluateExpr(g.expression) + ")";
        }

        return "unknown_expr";
    }
}
