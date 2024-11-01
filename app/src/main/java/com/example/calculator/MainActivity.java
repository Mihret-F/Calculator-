package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView result_tv, solution_tv;
    private boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        result_tv = findViewById(R.id.result_tv);
        solution_tv = findViewById(R.id.solution_tv);

        int[] buttonIds = {
                R.id.button_c, R.id.button_open_bracket, R.id.button_closebracket,
                R.id.button_divid, R.id.button_equal, R.id.button_dot, R.id.button_minus,
                R.id.button_add, R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8,
                R.id.button_9, R.id.button_multiply, R.id.button_ac
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        String buttonText = ((TextView) view).getText().toString();
        String currentExpression = solution_tv.getText().toString();

        if (buttonText.equals("AC")) {
            solution_tv.setText("");
            result_tv.setText("0");
            isResultDisplayed = false;
        } else if (buttonText.equals("C")) {
            if (!currentExpression.isEmpty()) {
                currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
            }
            solution_tv.setText(currentExpression);
            isResultDisplayed = false;
        } else if (buttonText.equals("=")) {
            if (!currentExpression.isEmpty()) {
                double finalResult = evaluateExpression(currentExpression);
                result_tv.setText(String.format("%.1f", finalResult)); // Ensure one decimal place
                isResultDisplayed = true;
            } else {
                result_tv.setText("0");
            }
        } else {
            if (isResultDisplayed) {
                // Start a new expression if a result was displayed
                currentExpression = buttonText;
                isResultDisplayed = false;
            } else {
                // Prevent leading zeros except for the first zero in the case of decimal numbers
                if (buttonText.equals("0") && (currentExpression.isEmpty() || currentExpression.equals("0"))) {
                    return; // Ignore adding leading zero
                }
                // Prevent adding multiple zeros and dots
                if (buttonText.equals(".") && currentExpression.contains(".")) {
                    return; // Ignore adding another dot
                }
                currentExpression += buttonText;
            }

            // Update the expression display
            solution_tv.setText(currentExpression);
        }

        // Update the result text view based on the current expression
        if (!currentExpression.isEmpty()) {
            double finalResult = evaluateExpression(currentExpression);
            result_tv.setText(String.format("%.1f", finalResult)); // Ensure one decimal place
        } else {
            result_tv.setText("0");
        }
    }

    private double evaluateExpression(String expression) {
        try {
            return eval(expression);
        } catch (Exception e) {
            return Double.NaN; // Return NaN for invalid expressions
        }
    }

    private double eval(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') {
                continue;
            }

            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length && (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.')) {
                    sb.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sb.toString()));
                i--;
            } else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } else if (isOperator(tokens[i])) {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(tokens[i])) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(tokens[i]);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}
