package compiler;
import compiler.parser;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

final class Lexical_Analyzer {
    public final String [] Keywords = {
      //  int       float
        "tibuok", "lutaw",
     //    double   char      if    else
        "duhay", "karakter", "ug", "edi",
     // switch  case    for     while
        "ilis","kaha","alang","samtang",
     //    do       break   return   continue
        "buhata","bungka","mobalik","padayon",
     //   import    sout            sin     String
        "angkat","imprenta_gawas","sulod","karhan"
    };
    public class Token {
        public String type;
        public String value;

        public Token(String type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + " :\t" + value;
        }
    }

    
    public final String Functuations = "; , () {} = == ! != <= >= + - / *";
    public ArrayList<Token> tokens;
    public String code;
    
    public Lexical_Analyzer(String code){
        this.code = code;
        this.tokens = new ArrayList<>();
    }
    
    public void tokenize() {
        StringBuilder token = new StringBuilder();
        boolean isString = false;

        for (int i = 0; i < code.length(); i++) {
            char currentChar = code.charAt(i);

            if (currentChar == '"') {
                if (isString) {
                    token.append(currentChar);
                    addToken(token.toString());
                    token.setLength(0);
                    isString = false;
                } else {
                    if (token.length() > 0) {
                        addToken(token.toString());
                        token.setLength(0);
                    }
                    token.append(currentChar);
                    isString = true;
                }
            } else if (Functuations.indexOf(currentChar) != -1 && !isString) {
                if (token.length() > 0) {
                    addToken(token.toString());
                    token.setLength(0);
                }
                addToken(String.valueOf(currentChar));
            } else if (Character.isWhitespace(currentChar) && !isString) {
                if (token.length() > 0) {
                    addToken(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(currentChar);
            }
        }

        if (token.length() > 0) {
            addToken(token.toString());
        }
    }
    
    private void addToken(String raw) {
        raw = raw.trim();
        if (raw.isEmpty()) return;

        String type;
        if (isKeyword(raw)) {
            type = "KEYWORD";
        } else if (isPunctuation(raw)) {
            type = "PUNCTUATION";
        } else if (isIntegerLiteral(raw)) {
            type = "INT";
        } else if (isDoubleLiteral(raw)) {
            type = "DOUBLE";
        } else if (isBoolean(raw)) {
            type = "BOOLEAN";
        } else if (isStringLiteral(raw)) {
            type = "STRING";
        } else if (isIdentifier(raw)) {
            type = "IDENTIFIER";
        } else {
            type = "UNKNOWN";
        }

        tokens.add(new Token(type, raw));
    }
    
    public boolean isPunctuation(String token) {
        return token.length() == 1 && Functuations.indexOf(token.charAt(0)) != -1;
    }

    public boolean isKeyword(String token) {
        for (String keywords : Keywords) {
            if (token.equals(keywords)) {
                return true;
            }
        }
        return false;
    }
    public boolean isIntegerLiteral(String token) {
        return Pattern.matches("^-?\\d+$", token);
    }

    public boolean isDoubleLiteral(String token) {
        return Pattern.matches("^-?\\d+\\.\\d+$", token);
    }

    public boolean isBoolean(String token) {
        return token.equals("true") || token.equals("false");
    }

    public boolean isStringLiteral(String token) {
        return token.startsWith("\"") && token.endsWith("\"");
    }

    public boolean isIdentifier(String token) {
        return Pattern.matches("^[a-zA-Z_][a-zA-Z0-9_]*$", token);
    }

    public void analyze(){
        for(Token token : tokens){
            System.out.println(token);
        }
    }
    
    public void File_reader() {
        StringBuilder codeBuilder = new StringBuilder();

        // 1️⃣ Read the source file
        try (FileReader fr = new FileReader("src\\compiler\\proglang.txt");
             BufferedReader br = new BufferedReader(fr)) {

            String line;
            while ((line = br.readLine()) != null) {
                codeBuilder.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2️⃣ Store code
        String code = codeBuilder.toString();

        // 3️⃣ Lexical Analysis
        Lexical_Analyzer lexer = new Lexical_Analyzer(code);
        lexer.tokenize();

        System.out.println("🔹 TOKENS GENERATED:");
        lexer.analyze();  // Optional, shows tokens in console

        // 4️⃣ Parsing
        parser Parser = new parser(lexer.tokens);
        AST.Program program = Parser.parseProgram();

        // 5️⃣ Code Generation (NEW)
        CodeGenerator generator = new CodeGenerator();
        String output = generator.generate(program);

        
    }
}
