package com.machone.jcalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private String expression = "";
    private boolean decimal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerClickListeners();
    }

    private void registerClickListeners(){
        View.OnClickListener numberListener = new View.OnClickListener(){
            public void onClick(View v){
                expression += ((Button)v).getText().toString();
                int length = expression.length();
                if (length == 2 && expression.startsWith("0")) expression = expression.substring(1, length);
                ((TextView)findViewById(R.id.input)).setText(expression);

                //findViewById(R.id.inputScroll).scrollTo(findViewById(R.id.input).getRight()+50, 0);
                HorizontalScrollView inputScroll = (HorizontalScrollView)findViewById(R.id.inputScroll);
                inputScroll.setScrollX(inputScroll.getRight());

            }
        };

        View.OnClickListener operatorListener = new View.OnClickListener(){
            public void onClick(View v){
                int lastIdx = expression.length() - 1;
                if (lastIdx < 0) return;

                char last = expression.charAt(lastIdx);
                String operator = ((Button)v).getText().toString();

                if (last == '.')
                    expression += "0";
                else if (last == '+' || last == '-' || last == '×' || last == '÷' || last =='(')
                    expression = expression.substring(0,lastIdx);

                expression += operator;
                decimal = false;
                ((TextView) findViewById(R.id.input)).setText(expression);
            }
        };

        View.OnClickListener parenthesisListener = new View.OnClickListener(){
            public void onClick(View v){
                int lastIdx = expression.length() - 1;
                char last = '\0';
                if (lastIdx >= 0) last = expression.charAt(lastIdx);
                String parenthesis = ((Button)v).getText().toString();

                if ((lastIdx == -1 && parenthesis.equals(")")) || (parenthesis.equals(")") && last == '(')) return;

                expression += parenthesis;
                decimal = false;
                ((TextView) findViewById(R.id.input)).setText(expression);
            }
        };

        findViewById(R.id.zero).setOnClickListener(numberListener);
        findViewById(R.id.one).setOnClickListener(numberListener);
        findViewById(R.id.two).setOnClickListener(numberListener);
        findViewById(R.id.three).setOnClickListener(numberListener);
        findViewById(R.id.four).setOnClickListener(numberListener);
        findViewById(R.id.five).setOnClickListener(numberListener);
        findViewById(R.id.six).setOnClickListener(numberListener);
        findViewById(R.id.seven).setOnClickListener(numberListener);
        findViewById(R.id.eight).setOnClickListener(numberListener);
        findViewById(R.id.nine).setOnClickListener(numberListener);

        findViewById(R.id.plusBtn).setOnClickListener(operatorListener);
        findViewById(R.id.minusBtn).setOnClickListener(operatorListener);
        findViewById(R.id.multBtn).setOnClickListener(operatorListener);
        findViewById(R.id.divBtn).setOnClickListener(operatorListener);
        findViewById(R.id.leftParenthBtn).setOnClickListener(parenthesisListener);
        findViewById(R.id.rightParenthBtn).setOnClickListener(parenthesisListener);

        findViewById(R.id.decimalBtn).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (decimal) return;
                int length = expression.length();
                if (length < 1 || expression.charAt(length - 1) < '0' || expression.charAt(length - 1) > '9')
                    expression += "0";

                expression += ".";
                decimal = true;
                ((TextView)findViewById(R.id.input)).setText(expression);
            }
        });

        findViewById(R.id.eqBtn).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (expression.length() < 1) return;
                char last = expression.charAt(expression.length() - 1);
                if (last == '.')
                    expression += '0';
                if (last == '+' || last == '-' || last == '×' || last == '÷' || last == '(') {
                    Toast.makeText(MainActivity.this, "Invalid expression", Toast.LENGTH_SHORT).show();
                    return;
                }

                String result;
                try {
                    result = String.valueOf(evaluateExpression());
                }
                catch(IllegalArgumentException ex){
                    result = "ERR";
                    Toast.makeText(MainActivity.this, "Internal error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (ArithmeticException ex){
                    result = "UNDEF";
                    Toast.makeText(MainActivity.this, "Cannot divide by 0", Toast.LENGTH_SHORT).show();
                }

                decimal = false;
                expression = "";

                if (result.endsWith(".0"))
                    result = result.substring(0,result.length() - 2);
                ((TextView)findViewById(R.id.input)).setText(result);
            }
        });

        findViewById(R.id.clearBtn).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                decimal = false;
                expression = "";
                ((TextView)findViewById(R.id.input)).setText(expression);
            }
        });

        findViewById(R.id.clearEntryBtn).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int i = expression.length() - 1;
                if (i < 1) return;

                int plus = expression.lastIndexOf('+');
                int minus = expression.lastIndexOf('-');
                int mult = expression.lastIndexOf('×');
                int div = expression.lastIndexOf('÷');

                if (i == plus || i == minus || i == mult || i == div)
                    expression = expression.substring(0,i);
                else {
                    if (plus > minus && plus > mult && plus > div)
                        expression = expression.substring(0,plus+1);
                    else if (minus > plus && minus > mult && minus > div)
                        expression = expression.substring(0,minus+1);
                    else if (mult > plus && mult > minus && mult > div)
                        expression = expression.substring(0,mult+1);
                    else if (div > plus && div > minus && div > mult)
                        expression = expression.substring(0,div+1);
                    else
                        expression = "";
                }
                decimal = false;
                ((TextView)findViewById(R.id.input)).setText(expression);
            }
        });
    }

    private float evaluateExpression(){
        ArrayList<String> postfix = convertToPostfix();
        Stack<String> calcStack = new Stack<String>();

        for (String token : postfix){
            char charToken = token.charAt(0);
            if (charToken >= '0' && charToken <= '9')
                calcStack.push(token);
            else {
                float num2 = Float.parseFloat(calcStack.pop());
                float num1 = Float.parseFloat(calcStack.pop());
                float result;
                if (charToken == '+')
                    result = num1 + num2;
                else if (charToken == '-')
                    result = num1 - num2;
                else if (charToken == '×')
                    result = num1 * num2;
                else if (charToken == '÷') {
                    if (num2 == 0)
                        throw new ArithmeticException("Cannot divide by 0");
                    result = num1 / num2;
                }
                else
                    throw new IllegalArgumentException("Unknown token: " + token);
                calcStack.push(String.valueOf(result));
            }
        }
        return Float.parseFloat(calcStack.pop());
    }

    private ArrayList<String> convertToPostfix(){
        Stack<Character> opStack = new Stack<Character>();
        ArrayList<String> postfix = new ArrayList<String>();

        char[] split = expression.toCharArray();
        char prevChar = '\0';
        for (char c : split){
            if ((c >= '0' && c <= '9') || c == '.'){
                String num = "";
                if ((prevChar >= '0' && prevChar <= '9') || prevChar == '.')
                    num = postfix.remove(postfix.size() - 1);

                postfix.add(num + String.valueOf(c));
            }
            else if (c == '('){
                opStack.push(c);
            }
            else if (c == ')'){
                char pop = opStack.pop();
                while (pop != '('){
                    postfix.add(String.valueOf(pop));
                    pop = opStack.pop();
                }
            }
            else{
                if (!opStack.isEmpty()) {
                    char prev = opStack.peek();
                    while (((c == '+' || c == '-') && prev != '(')
                            || ((c == '×' || c == '÷') && (prev == '×' || prev == '÷'))) {
                        postfix.add(String.valueOf(opStack.pop()));
                        if (opStack.isEmpty()) break;
                        prev = opStack.peek();
                    }
                }
                opStack.push(c);
            }
            prevChar = c;
        }
        while (!opStack.isEmpty()){
            postfix.add(String.valueOf(opStack.pop()));
        }

        return postfix;
    }
}
