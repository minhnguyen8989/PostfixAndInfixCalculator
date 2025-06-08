import java.util.Stack;

public class InfixCalculator {

    /**
     * Checks if a character is a supported operator.
     * @param charactor the character to check
     * @return true if the character is '+', '-', '*', '/', or '%', false otherwise
     */
    private boolean isOperator(char charactor) {
        return charactor == '+' || charactor == '-' || charactor == '*' || charactor == '/' || charactor == '%';
    }

    /**
     * Returns the precedence level of an operator.
     * Higher number means higher precedence.
     * @param operator the operator character
     * @return precedence value (1 for '+' and '-', 2 for '*', '/', '%', 0 if not an operator)
     */
    private int precedence(char operator) {
        if (operator == '+' || operator == '-') return 1;
        if (operator == '*' || operator == '/' || operator == '%') return 2;
        return 0;
    }

    /**
     * Applies the given operator to two integer operands.
     * @param a first operand
     * @param b second operand
     * @param operator operator to apply ('+', '-', '*', '/', '%')
     * @return the result of the operation
     * @throws ArithmeticException if division or modulo by zero occurs
     */
    private int applyOp(int a, int b, char operator) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            case '%':
                if (b == 0) throw new ArithmeticException("Modulo by zero");
                return a % b;
            default: return 0;
        }
    }

    /**
     * Validates that parentheses in the expression are balanced and properly nested.
     * @param expression the string expression to check
     * @throws IllegalArgumentException if parentheses are unmatched or improperly nested
     */
    private void validateParentheses(String expression) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched closing parenthesis at position " + i);
                }
                stack.pop();
            }
        }
        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Unmatched opening parenthesis.");
        }
    }

    /**
     * Checks that the expression contains at least one pair of parentheses.
     * @param expression the string expression to check
     * @throws IllegalArgumentException if expression does not contain '(' or ')'
     */
    private void requireParentheses(String expression) {
        if (!expression.contains("(") || !expression.contains(")")) {
            throw new IllegalArgumentException("Invalid infix expression");
        }
    }

    /**
     * Evaluates a mathematical expression in infix notation.
     * Supports operators '+', '-', '*', '/', '%', and parentheses.
     * @param expression the infix expression to evaluate
     * @return the computed integer result or Integer.MIN_VALUE if invalid
     */
    public int evaluateInfix(String expression) {
        try {
            requireParentheses(expression);
            validateParentheses(expression);
            Stack<Integer> values = new Stack<>();
            Stack<Character> characters = new Stack<>();

            for (int i = 0; i < expression.length(); i++) {
                char ch = expression.charAt(i);

                if (Character.isWhitespace(ch)) continue;

                if (Character.isDigit(ch)) {
                    int val = 0;
                    while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                        val = val * 10 + (expression.charAt(i) - '0');
                        i++;
                    }
                    values.push(val);
                    i--;
                }

                else if (ch == '(') {
                    characters.push(ch);
                }

                else if (ch == ')') {
                    while (!characters.isEmpty() && characters.peek() != '(') {
                        int b = values.pop();
                        int a = values.pop();
                        char op = characters.pop();
                        values.push(applyOp(a, b, op));
                    }
                    if (characters.isEmpty() || characters.pop() != '(') {
                        throw new IllegalArgumentException("Mismatched parentheses");
                    }
                }

                else if (isOperator(ch)) {
                    while (!characters.isEmpty() && precedence(characters.peek()) >= precedence(ch)) {
                        int b = values.pop();
                        int a = values.pop();
                        char op = characters.pop();
                        values.push(applyOp(a, b, op));
                    }
                    characters.push(ch);
                }

                else {
                    throw new IllegalArgumentException("Invalid character: " + ch);
                }
            }

            while (!characters.isEmpty()) {
                int b = values.pop();
                int a = values.pop();
                char op = characters.pop();
                values.push(applyOp(a, b, op));
            }

            if (values.size() != 1) throw new IllegalArgumentException("Invalid expression");

            return values.pop();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid infix expression");
            return Integer.MIN_VALUE;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Evaluates a mathematical expression in postfix (Reverse Polish) notation.
     * Tokens must be space-separated.
     * Supports operators '+', '-', '*', '/', '%'.
     * @param expression the postfix expression to evaluate
     * @return the computed integer result or Integer.MIN_VALUE if invalid
     */
    public int evaluatePostfix(String expression) {
        try {
            Stack<Integer> stack = new Stack<>();
            String[] tokens = expression.trim().split("\\s+");

            for (String token : tokens) {
                if (token.matches("-?\\d+")) {
                    stack.push(Integer.parseInt(token));
                } else if (token.matches("[+\\-*/%]")) {
                    if (stack.size() < 2) throw new IllegalArgumentException("Invalid postfix expression");
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(applyOp(a, b, token.charAt(0)));
                } else {
                    throw new IllegalArgumentException("Invalid token: " + token);
                }
            }

            if (stack.size() != 1) throw new IllegalArgumentException("Invalid postfix expression");
            return stack.pop();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return Integer.MIN_VALUE;
        }
    }

    public static void main(String[] args) {
        InfixCalculator calculator = new InfixCalculator();

        //Valid Expression Case 1
        String expression1 = "( 4 + 2 ) * 3";
        int result1 = calculator.evaluateInfix(expression1);
        if (result1 != Integer.MIN_VALUE) System.out.println("Result 1: " + result1);

        //Valid Expression Case 2
        String expression2 = "50+(3*7)";
        int result2 = calculator.evaluateInfix(expression2);
        if (result2 != Integer.MIN_VALUE) System.out.println("Result 2: " + result2);

        // Missing parentheses Case
        String expression3 = "3+4*2";
        int result3 = calculator.evaluateInfix(expression3);
        if (result3 != Integer.MIN_VALUE) System.out.println("Result 3: " + result3);

        // Postfix Expression Case
        String expression4 = "4 2 + 3 *";
        int result4 = calculator.evaluatePostfix(expression4);
        if (result4 != Integer.MIN_VALUE) System.out.println("Result 4: " + result4);
    }
}
