package compiler;

import java.util.*;

public class SemanticAnalyzer {

    private final Map<String, String> variables = new HashMap<>(); // varName -> type
    private final List<String> errors = new ArrayList<>();

    public void analyze(AST.Program program) {
        for (AST.Stmt stmt : program.statements) {
            analyzeStmt(stmt);
        }

        if (!errors.isEmpty()) {
            System.out.println("\nSemantic Errors Found:");
            errors.forEach(System.out::println);
        } else {
            System.out.println("\nSemantic Analysis Passed! No errors found.");
        }
    }

    // --- Analyze Statements ---
    private void analyzeStmt(AST.Stmt stmt) {
        if (stmt instanceof AST.VarDecl v) {
            if (variables.containsKey(v.name)) {
                errors.add("Variable '" + v.name + "' already declared.");
            } else {
                variables.put(v.name, v.type);
                analyzeExpr(v.initializer);
            }
        } else if (stmt instanceof AST.Print p) {
            analyzeExpr(p.expression);
        } else if (stmt instanceof AST.ExpressionStmt e) {
            analyzeExpr(e.expression);
        } else if (stmt instanceof AST.Block b) {
            Map<String, String> oldScope = new HashMap<>(variables);
            for (AST.Stmt s : b.statements) {
                analyzeStmt(s);
            }
            variables.clear();
            variables.putAll(oldScope); // restore outer scope
        } else if (stmt instanceof AST.IfStmt i) {
            String condType = analyzeExpr(i.condition);
            if (!condType.equals("BOOLEAN")) {
                errors.add("Condition in IF statement must be BOOLEAN.");
            }
            analyzeStmt(i.thenBranch);
            if (i.elseBranch != null) analyzeStmt(i.elseBranch);
        } else if (stmt instanceof AST.WhileStmt w) {
            String condType = analyzeExpr(w.condition);
            if (!condType.equals("BOOLEAN")) {
                errors.add("Condition in WHILE loop must be BOOLEAN.");
            }
            analyzeStmt(w.body);
        } else if (stmt instanceof AST.ForStmt f) {
            if (f.initializer != null) analyzeStmt(f.initializer);
            if (f.condition != null && !analyzeExpr(f.condition).equals("BOOLEAN")) {
                errors.add("Condition in FOR loop must be BOOLEAN.");
            }
            if (f.body != null) analyzeStmt(f.body);
            if (f.increment != null) analyzeExpr(f.increment);
        }
    }

    // --- Analyze Expressions ---
    private String analyzeExpr(AST.Expr expr) {
        if (expr == null) return "VOID";

        if (expr instanceof AST.Literal l) {
            if (l.value instanceof Integer) return "TIBUOK";
            if (l.value instanceof Double) return "LUTAW";
            if (l.value instanceof Boolean) return "BOOLEAN";
            if (l.value instanceof String) return "STRING";
        } else if (expr instanceof AST.Variable v) {
            if (!variables.containsKey(v.name)) {
                errors.add("Variable '" + v.name + "' not declared.");
                return "UNKNOWN";
            }
            return variables.get(v.name);
        } else if (expr instanceof AST.Assignment a) {
            String varType = variables.get(a.name);
            if (varType == null) {
                errors.add("Variable '" + a.name + "' not declared.");
                return "UNKNOWN";
            }
            String valType = analyzeExpr(a.value);
            if (!varType.equals(valType)) {
                errors.add("Type mismatch: cannot assign " + valType + " to " + varType + " '" + a.name + "'");
            }
            return varType;
        } else if (expr instanceof AST.Binary b) {
            String leftType = analyzeExpr(b.left);
            String rightType = analyzeExpr(b.right);

            // Simplified type rules
            if (b.operator.matches("[+-/*]")) {
                if (!leftType.equals(rightType)) {
                    errors.add("Type mismatch in binary operation: " + leftType + " " + b.operator + " " + rightType);
                }
                return leftType;
            } else if (b.operator.matches("[><==!=]")) {
                return "BOOLEAN";
            }
        } else if (expr instanceof AST.Grouping g) {
            return analyzeExpr(g.expression);
        }

        return "UNKNOWN";
    }
}
