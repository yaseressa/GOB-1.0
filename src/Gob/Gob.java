package Gob;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Gob {
    static boolean hasError = false;
    static boolean hadRuntimeError = false;
    static Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {

        if (args.length > 1) {
            System.out.println("Istimaal: [programka]");
                    System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }

        }
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hasError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        while(true) {
            System.out.print("Qor ==>> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hasError = false;
        }
    }
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        var parser = new Parser(tokens);
        Expr expr = parser.parse();
        if (hasError) return;
        interpreter.interpret(expr);

    }
    static void error(int line, String message) {
        report(line, "", message);
    }
    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "Qalad" + where + ": " + message + "   ( laynka " + line + " ).");
        hasError = true;
    }
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " halka u dambaysa", message);
        } else {
            report(token.line, " isticmaal '" + token.lexeme + "'", message);
        }
    }
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n   ( laynka " + error.token.line + " ).");
        hadRuntimeError = true;
    }
    }
