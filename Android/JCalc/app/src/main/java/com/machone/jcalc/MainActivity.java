package com.machone.jcalc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Context _context;
    private String expression = "";
    private String currentOperand = "";
    private char lastChar = '\0';
    private boolean expressionIsEquals = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _context = getBaseContext();

        registerClickListeners();
    }

    private void registerClickListeners() {
        View.OnClickListener buttonListener = new View.OnClickListener(){
            public void onClick(View v){
                String buttonText = ((Button)v).getText().toString();

                if (expressionIsEquals && buttonText.matches("[^0-9.]"))
                    expressionIsEquals = false;

                // Handle numbers/decimal
                if (buttonText.matches("[0-9.]")){
                    if (expressionIsEquals){
                        expression = "";
                        lastChar = '\0';
                        currentOperand = "";
                    }
                    if (currentOperand.equals("0") && !buttonText.equals(".")) {
                        currentOperand = "";
                        expression = "";
                    }
                    if (buttonText.equals(".")){
                        if (currentOperand.isEmpty() || currentOperand.equals(Operators.NEGATIVE)) {
                            currentOperand += "0";
                            expression += "0";
                        }
                        else if (currentOperand.contains(".")) return;
                    }
                    currentOperand += buttonText;
                }
                // Handle non-minus operators
                else if (buttonText.matches("[" + Operators.PLUS +
                        Operators.MULTIPLY +
                        Operators.DIVIDE + "]")){
                    if (lastChar == '\0') return; // Must enter a number first
                    // Last character is already an operator, negative, or decimal
                    if ((lastChar < '0' || lastChar > '9') && lastChar != ')')
                        expression = expression.substring(0, expression.length() - 1);

                    currentOperand = "";
                }
                // Handle minus/negative
                else if (buttonText.matches("[" + Operators.MINUS +
                        Operators.NEGATIVE + "]")){
                    if (currentOperand.isEmpty() && lastChar != ')') { // negative intent
                        buttonText = String.valueOf(Operators.NEGATIVE);
                        currentOperand += buttonText;
                    }
                    else { // minus intent
                        if (lastChar == '.')
                            expression = expression.substring(0, expression.length() - 1);
                        buttonText = String.valueOf(Operators.MINUS);
                        currentOperand = "";
                    }
                }
                // Handle left parenthesis
                else if (buttonText.equals("(")){
                    if (currentOperand.equals(Operators.NEGATIVE))
                        return;
                    if (lastChar == '.')
                        expression = expression.substring(0, expression.length() - 1);
                    currentOperand = "";
                }
                // Handle right parenthesis
                else if (buttonText.equals(")")){
                    if ((currentOperand.isEmpty() && lastChar != ')') ||
                            currentOperand.equals(Operators.NEGATIVE))
                        return;
                    if (lastChar == '.')
                        expression = expression.substring(0, expression.length() - 1);
                    currentOperand = "";
                }
                // Handle clear all
                else if (buttonText.equals("C")){
                    currentOperand = "";
                    lastChar = '\0';
                    expression = "";
                }
                // Handle clear entry
                else if (buttonText.equals("CE")){
                    if (currentOperand.isEmpty()) return;
                    expression = expression.substring(0, expression.lastIndexOf(currentOperand));
                    currentOperand = "";
                    lastChar = expression.charAt(expression.length() - 1);
                }
                // Handle equals
                else if (buttonText.equals("=")){
                    if (lastChar == '\0' || currentOperand.equals(Operators.NEGATIVE)) return;
                    else if (lastChar == '.')
                        expression = expression.substring(0, expression.length() - 1);
                    if ((lastChar < '0' || lastChar > '9') && lastChar != ')') {
                        Toast.makeText(MainActivity.this, "Invalid expression", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        expression = String.valueOf(Calculator.evaluateExpression(expression));
                    } catch (ArithmeticException ex) {
                        expression = "UNDEF";
                        currentOperand = "";
                        lastChar = '\0';
                        Toast.makeText(MainActivity.this, "Cannot divide by 0", Toast.LENGTH_SHORT).show();
                    }

                    if (expression.endsWith(".0"))
                        expression = expression.substring(0, expression.length() - 2);
                    currentOperand = expression;
                    lastChar = expression.charAt(expression.length() - 1);
                }

                if (!buttonText.equals("=") && !buttonText.equals("C") && !buttonText.equals("CE")) {
                    lastChar = buttonText.charAt(0);
                    expression += buttonText;
                }

                ((TextView) findViewById(R.id.input)).setText(expression);
                HorizontalScrollView inputScroll = (HorizontalScrollView) findViewById(R.id.inputScroll);
                inputScroll.setScrollX(inputScroll.getRight());
            }
        };

        findViewById(R.id.zero).setOnClickListener(buttonListener);
        findViewById(R.id.one).setOnClickListener(buttonListener);
        findViewById(R.id.two).setOnClickListener(buttonListener);
        findViewById(R.id.three).setOnClickListener(buttonListener);
        findViewById(R.id.four).setOnClickListener(buttonListener);
        findViewById(R.id.five).setOnClickListener(buttonListener);
        findViewById(R.id.six).setOnClickListener(buttonListener);
        findViewById(R.id.seven).setOnClickListener(buttonListener);
        findViewById(R.id.eight).setOnClickListener(buttonListener);
        findViewById(R.id.nine).setOnClickListener(buttonListener);
        findViewById(R.id.decimalBtn).setOnClickListener(buttonListener);

        findViewById(R.id.plusBtn).setOnClickListener(buttonListener);
        findViewById(R.id.minusBtn).setOnClickListener(buttonListener);
        findViewById(R.id.multBtn).setOnClickListener(buttonListener);
        findViewById(R.id.divBtn).setOnClickListener(buttonListener);
        findViewById(R.id.rightParenthBtn).setOnClickListener(buttonListener);

        findViewById(R.id.leftParenthBtn).setOnClickListener(buttonListener);
        findViewById(R.id.rightParenthBtn).setOnClickListener(buttonListener);

        findViewById(R.id.clearBtn).setOnClickListener(buttonListener);
        findViewById(R.id.clearEntryBtn).setOnClickListener(buttonListener);
    }
}
