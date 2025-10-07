package compiler;

import compiler.Lexical_Analyzer.Token;
import java.util.ArrayList;
import java.util.List;
import compiler.AST;

public class parser {
    private ArrayList<Token> tokens;
    private int current = 0;

    public parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    // =============== BASIC UTILITIES ===============
    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean check(String type) {
        if (isAtEnd()) return false;
        return peek().type.equals(type);
    }

    private boolean match(String... types) {
        for (String type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(String expectedType, String message) {
        if (check(expectedType)) {
            return advance();
        }
        throw new RuntimeException(message + " Found: " + peek().value);
    }

    private Token consumeIdentifier(String message) {
        if (check("IDENTIFIER")) return advance();
        throw new RuntimeException(message + " Found: " + peek().value);
    }

    // =============== EXPRESSIONS ===============
    private void expression() {
        logicOr();
    }

    private void logicOr() {
        logicAnd();
        while (match("or")) logicAnd();
    }

    private void logicAnd() {
        equality();
        while (match("and")) equality();
    }

    private void equality() {
        comparison();
        while (match("==", "!=")) comparison();
    }

    private void comparison() {
        term();
        while (match(">", ">=", "<", "<=")) term();
    }

    private void term() {
        factor();
        while (match("+", "-")) factor();
    }

    private void factor() {
        unary();
        while (match("*", "/")) unary();
    }

    private void unary() {
        if (match("-", "!")) unary();
        else primary();
    }

    private void primary() {
        if (match("NUMBER", "STRING", "IDENTIFIER")) return;
        if (match("(")) {
            expression();
            consume(")", "Expected ')' after expression.");
            return;
        }
        throw new RuntimeException("Unexpected token: " + peek().value);
    }

    // =============== STATEMENTS ===============
    private void expressionStatement() {
        expression();
        consume(";", "Expected ';' after expression.");
    }

    private void printStatement() {
        expression();
        consume(";", "Expected ';' after imprenta_gawas statement.");
        System.out.println("PRINT statement parsed");
    }

    private void ifStatement() {
        consume("(", "Expected '(' after 'ug'.");
        expression(); // condition
        consume(")", "Expected ')' after condition.");
        block(); // then branch
        if (match("edi")) block(); // else branch
    }

    private void whileStatement() {
        consume("(", "Expected '(' after 'samtang'.");
        expression();
        consume(")", "Expected ')' after condition.");
        statement();
    }

    private void forStatement() {
        consume("(", "Expected '(' after 'alang'.");

        // 1️⃣ Initializer
        if (match(";")) {
            // No initializer
        } else if (isType(peek().type)) {
            declaration();
        } else {
            expressionStatement();
        }

        // 2️⃣ Condition
        if (!check(";")) expression();
        consume(";", "Expected ';' after loop condition.");

        // 3️⃣ Increment
        if (!check(")")) expression();
        consume(")", "Expected ')' after for clauses.");

        // 4️⃣ Body
        statement();
    }

    private void block() {
        while (!check("}") && !isAtEnd()) {
            declaration();
        }
        consume("}", "Expected '}' after block.");
    }

    private void statement() {
        if (match("ug")) {
            ifStatement();
        } else if (match("samtang")) {
            whileStatement();
        } else if (match("alang")) {
            forStatement();
        } else if (match("imprenta_gawas")) {
            printStatement();
        } else {
            expressionStatement();
        }
    }

    // =============== DECLARATIONS ===============
    private boolean isType(String tokenType) {
        return tokenType.equals("tibuok") ||
               tokenType.equals("lutaw") ||
               tokenType.equals("duhay") ||
               tokenType.equals("karakter") ||
               tokenType.equals("karhan");
    }

    private AST.Stmt varDeclaration() {
        Token typeToken = previous(); // fixed type mismatch
        Token name = consumeIdentifier("Expected variable name after type.");

        AST.Expr initializer = null;
        if (match("=")) initializer = null; // placeholder for expression()

        consume(";", "Expected ';' after variable declaration.");
        return new AST.VarDecl(typeToken.value, name.value, initializer);
    }

    private AST.Stmt declaration() {
        if (match("tibuok", "lutaw", "duhay", "karakter", "karhan")) {
            return varDeclaration();
        } else {
            statement();
            return null; // statements do not return AST nodes yet
        }
    }

    // =============== PROGRAM ENTRY ===============
    public AST.Program parseProgram() {
        List<AST.Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return new AST.Program(statements);
    }
}
