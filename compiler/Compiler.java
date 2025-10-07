package compiler;

public class Compiler {
    public static void main(String[] args) {
        // 1️⃣ Create an instance of Lexical_Analyzer (the file path is already inside File_reader)
        Lexical_Analyzer lexer = new Lexical_Analyzer("");

        // 2️⃣ Call the File_reader method to read the file, tokenize, parse, and generate code
        lexer.File_reader();
    }
}
