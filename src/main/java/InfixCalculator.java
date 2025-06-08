import java.util.Stack;

public class InfixCalculator {

    private boolean isOperator(char charactor) {
        return charactor == '+' || charactor == '-' || charactor == '*' || charactor == '/' || charactor == '%';
    }

    private int precedence(char operator) {
        if (operator == '+' || operator == '-') return 1;
        if (operator == '*' || operator == '/' || operator == '%') return 2;
        return 0;
    }

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

    private void requireParentheses(String expression) {
        if (!expression.contains("(") || !expression.contains(")")) {
            throw new IllegalArgumentException("Missing Parentheses");
        }
    }

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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return Integer.MIN_VALUE;
        }
    }

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
        String expression1 = "(4+2)*3";
        System.out.println("Result 1: " + calculator.evaluateInfix(expression1));

        //Valid Expression Case 2
        String expression2 = "5+(3*7)";
        System.out.println("Result 2: " + calculator.evaluateInfix(expression2));

        // Missing parentheses Case
        String expression3 = "3 + 4 * 2";
        System.out.println("Result 3: " + calculator.evaluateInfix(expression3));

        // Postfix Expression Case
        String expression4 = "4 2 + 3 *";
        System.out.println("Result 4: " + calculator.evaluatePostfix(expression4));
    }
}
