import java.util.Stack;

public class InfixCalculator {

    private boolean isOperator(char character) {
        return character == '+' || character == '-' || character == '*' || character == '/' || character == '%';
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-': return 1;
            case '*':
            case '/':
            case '%': return 2;
            default: return 0;
        }
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
            default: throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    public int evaluateInfix(String expression) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

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
                i--; // backtrack after last digit
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    int b = values.pop();
                    int a = values.pop();
                    char op = operators.pop();
                    values.push(applyOp(a, b, op));
                }
                if (operators.isEmpty() || operators.pop() != '(') {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
            } else if (isOperator(ch)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                    int b = values.pop();
                    int a = values.pop();
                    char op = operators.pop();
                    values.push(applyOp(a, b, op));
                }
                operators.push(ch);
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }

        while (!operators.isEmpty()) {
            int b = values.pop();
            int a = values.pop();
            char op = operators.pop();
            values.push(applyOp(a, b, op));
        }

        if (values.size() != 1) throw new IllegalArgumentException("Invalid infix expression");

        return values.pop();
    }

    public int evaluatePostfix(String expression) {
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
    }

    public static void main(String[] args) {
        InfixCalculator calculator = new InfixCalculator();

        //Valid Expression Case 1
        String expression1 = "( 4 + 2 ) * 3";
        try {
            int result1 = calculator.evaluateInfix(expression1);
            System.out.println("Result 1: " + result1);
        } catch (Exception e) {
            System.out.println("Error in expression 1: " + e.getMessage());
        }

        //Valid Expression Case 2
        String expression2 = "50+(3*7)";
        try {
            int result2 = calculator.evaluateInfix(expression2);
            System.out.println("Result 2: " + result2);
        } catch (Exception e) {
            System.out.println("Error in expression 2: " + e.getMessage());
        }

        // Missing parentheses Case Optional --> Valid Expression Case
        String expression3 = "3+4*2";
        try {
            int result3 = calculator.evaluateInfix(expression3);
            System.out.println("Result 3: " + result3);
        } catch (Exception e) {
            System.out.println("Error in expression 3: " + e.getMessage());
        }

        // Postfix Expression Case
        String expression4 = "4 2 + 3 *";
        try {
            int result4 = calculator.evaluatePostfix(expression4);
            System.out.println("Result 4: " + result4);
        } catch (Exception e) {
            System.out.println("Error in expression 4: " + e.getMessage());
        }
    }
}
