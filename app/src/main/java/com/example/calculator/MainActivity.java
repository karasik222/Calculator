package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private StringBuilder currentInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
    }

    public void onDigitClick(View view) {
        Button button = (Button) view;
        currentInput.append(button.getText());
        tvResult.setText(currentInput.toString());
    }

    public void onOperatorClick(View view) {
        Button button = (Button) view;
        currentInput.append(" ").append(button.getText()).append(" ");
        tvResult.setText(currentInput.toString());
    }

    public void onClearClick(View view) {
        currentInput.setLength(0);
        tvResult.setText("0");
    }

    public void onBackspaceClick(View view) {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            if (currentInput.length() == 0) {
                tvResult.setText("0");
            } else {
                tvResult.setText(currentInput.toString());
            }
        }
    }

    public void onParenthesesClick(View view) {
        Button button = (Button) view;
        currentInput.append(button.getText());
        tvResult.setText(currentInput.toString());
    }

    public void onDecimalClick(View view) {
        if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
            tvResult.setText(currentInput.toString());
        }
    }

    public void onEqualClick(View view) {
        String result = evaluateExpression(currentInput.toString());
        tvResult.setText(result);
        currentInput.setLength(0);
    }

    private String evaluateExpression(String expression) {
        try {
            List<String> tokens = tokenize(expression);
            List<String> rpn = toRPN(tokens);
            return calculateRPN(rpn);
        } catch (Exception e) {
            return "Error";
        }
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (char ch : expression.toCharArray()) {
            if (Character.isDigit(ch) || ch == '.') {
                currentToken.append(ch);
            } else if (ch == ' ') {
                continue;
            } else {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(ch));
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.matches("[0-9.]+")) {
                output.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                operators.pop();
            } else {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }

    private String calculateRPN(List<String> rpn) {
        Stack<Double> values = new Stack<>();

        for (String token : rpn) {
            if (token.matches("[0-9.]+")) {
                values.push(Double.parseDouble(token));
            } else {
                double b = values.pop();
                double a = values.pop();
                values.push(applyOperator(a, b, token));
            }
        }

        return String.valueOf(values.pop());
    }

    private int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "÷":
                return 2;
            default:
                return 0;
        }
    }

    private double applyOperator(double a, double b, String operator) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "×":
                return a * b;
            case "÷":
                return a / b;
            default:
                return 0;
        }
    }
}
