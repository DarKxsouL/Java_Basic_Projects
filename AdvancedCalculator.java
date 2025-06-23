import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class AdvancedCalculator extends JFrame implements ActionListener {

    private JTextField display;
    private JTextArea historyArea;
    private JScrollPane historyScroll;
    private String expression = "";
    private ArrayList<String> history = new ArrayList<>();
    private boolean isDarkTheme = false;
    private boolean isHistoryVisible = false;
    private JButton themeButton, historyButton;

    private final Color LIGHT_BG = Color.WHITE;
    private final Color DARK_BG = new Color(43, 43, 43);
    private final Color LIGHT_TEXT = Color.BLACK;
    private final Color DARK_TEXT = Color.WHITE;

    public AdvancedCalculator() {
        setTitle("Advanced Calculator");
        setSize(500, 640);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        display = new JTextField();
        display.setBounds(30, 20, 420, 40);
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 20));
        add(display);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyScroll = new JScrollPane(historyArea);
        historyScroll.setBounds(30, 70, 420, 100);

        String[] buttons = {
                "(", ")", "%", "C",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "<-", "+",
                "="
        };

        int x = 30, y = 180;
        for (int i = 0; i < buttons.length; i++) {
            JButton btn = new JButton(buttons[i]);
            btn.setBounds(x, y, 90, 50);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.addActionListener(this);
            add(btn);

            x += 100;
            if ((i + 1) % 4 == 0) {
                x = 30;
                y += 60;
            }
        }

        historyButton = new JButton("History");
        historyButton.setBounds(235, y + 20, 80, 30);
        historyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        historyButton.addActionListener(e -> {
            if (!isHistoryVisible) {
                add(historyScroll);
                historyScroll.setVisible(true);
                isHistoryVisible = true;
            } else {
                remove(historyScroll);
                isHistoryVisible = false;
            }
            revalidate();
            repaint();
        });
        add(historyButton);

        themeButton = new JButton("Dark");
        themeButton.setBounds(335, y + 20, 80, 30);
        themeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        themeButton.addActionListener(e -> {
            isDarkTheme = !isDarkTheme;
            themeButton.setText(isDarkTheme ? "Light" : "Dark");
            applyTheme();
        });
        add(themeButton);

        applyTheme();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = ((JButton) e.getSource()).getText();

        try {
            switch (input) {
                case "C":
                    expression = "";
                    display.setText(expression);
                    break;

                case "<-":
                    if (!expression.isEmpty()) {
                        expression = expression.substring(0, expression.length() - 1);
                        display.setText(expression);
                    }
                    break;

                case "=":
                    if (expression.isEmpty() || expression.matches(".*[\\+\\-\\*/\\.%]$")) {
                        JOptionPane.showMessageDialog(this, "Incomplete Expression", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        double result = evaluate(expression);
                        String full = expression + " = " + result;
                        display.setText(String.valueOf(result));
                        history.add(0, full);
                        updateHistory();
                        expression = "";
                    } catch (ArithmeticException err) {
                        JOptionPane.showMessageDialog(this, err.getMessage(), "Math Error", JOptionPane.ERROR_MESSAGE);
                        display.setText("");
                        expression = "";
                    }
                    break;

                case "%":
                    if (!expression.isEmpty()) {
                        double val = evaluate(expression);
                        expression = String.valueOf(val / 100);
                        display.setText(expression);
                    }
                    break;

                default:
                    expression += input;
                    display.setText(expression);
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Operation", "Error", JOptionPane.ERROR_MESSAGE);
            expression = "";
            display.setText("");
        }
    }

    private void updateHistory() {
        StringBuilder sb = new StringBuilder();
        for (String entry : history) {
            sb.append(entry).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    private void applyTheme() {
        Color bg = isDarkTheme ? DARK_BG : LIGHT_BG;
        Color fg = isDarkTheme ? DARK_TEXT : LIGHT_TEXT;

        getContentPane().setBackground(bg);
        display.setBackground(bg);
        display.setForeground(fg);
        historyArea.setBackground(bg);
        historyArea.setForeground(fg);
    }

    private boolean isBracketsBalanced(String expr) {
        Stack<Character> stack = new Stack<>();
        for (char ch : expr.toCharArray()) {
            if (ch == '(') stack.push(ch);
            else if (ch == ')') {
                if (stack.isEmpty() || stack.pop() != '(') return false;
            }
        }
        return stack.isEmpty();
    }

    private double evaluate(String expr) {
        expr = expr.trim();

        while (expr.endsWith("+") || expr.endsWith("-") || expr.endsWith("*") || expr.endsWith("/") || expr.endsWith(".")) {
            expr = expr.substring(0, expr.length() - 1);
        }

        if (!isBracketsBalanced(expr)) {
            throw new ArithmeticException("Unbalanced brackets");
        }

        List<String> postfix = infixToPostfix(expr);
        double result = evaluatePostfix(postfix);

        DecimalFormat df = new DecimalFormat("#.####");
        return Double.parseDouble(df.format(result));
    }

    private List<String> infixToPostfix(String expr) {
        List<String> output = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        StringBuilder number = new StringBuilder();

        Map<Character, Integer> precedence = Map.of(
                '+', 1, '-', 1, '*', 2, '/', 2, '%', 2
        );

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                number.append(ch);
            } else {
                if (number.length() > 0) {
                    output.add(number.toString());
                    number.setLength(0);
                }

                if (ch == '(') {
                    stack.push(ch);
                } else if (ch == ')') {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        output.add(String.valueOf(stack.pop()));
                    }
                    if (!stack.isEmpty()) stack.pop();
                } else if (precedence.containsKey(ch)) {
                    while (!stack.isEmpty() && precedence.getOrDefault(stack.peek(), 0) >= precedence.get(ch)) {
                        output.add(String.valueOf(stack.pop()));
                    }
                    stack.push(ch);
                }
            }
        }

        if (number.length() > 0) output.add(number.toString());
        while (!stack.isEmpty()) output.add(String.valueOf(stack.pop()));

        return output;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();

                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new ArithmeticException("Division by zero");
                        stack.push(a / b); break;
                    case "%": stack.push(a % b); break;
                }
            }
        }

        return stack.pop();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdvancedCalculator::new);
    }
}

