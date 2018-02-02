package com.machone.jcalc;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Jordan on 2/1/2018.
 */

public class Calculator {
    public static double evaluateExpression(String expression){
        ArrayList<String> postfix = convertToPostfix(expression);
        Stack<String> calcStack = new Stack<String>();

        for (String token : postfix) {
            char charToken = token.charAt(0);
            if (charToken >= '0' && charToken <= '9')
                calcStack.push(token);
            else {
                double num2 = Double.parseDouble(calcStack.pop());
                double num1 = Double.parseDouble(calcStack.pop());
                double result = 0;
                if (charToken == '+')
                    result = num1 + num2;
                else if (charToken == Operators.MINUS)
                    result = num1 - num2;
                else if (charToken == Operators.MULTIPLY)
                    result = num1 * num2;
                else if (charToken == Operators.DIVIDE) {
                    if (num2 == 0)
                        throw new ArithmeticException("Cannot divide by 0");
                    result = num1 / num2;
                }
                calcStack.push(String.valueOf(result));
            }
        }
        return Double.parseDouble(calcStack.pop());
    }

    private static ArrayList<String> convertToPostfix(String infixExpression){
        Stack<Character> opStack = new Stack<Character>();
        ArrayList<String> postfix = new ArrayList<String>();

        char[] split = infixExpression.toCharArray();
        char prevChar = '\0';
        for (char c : split) {
            if ((c >= '0' && c <= '9') || c == '.') {
                String num = "";
                if ((prevChar >= '0' && prevChar <= '9') || prevChar == '.')
                    num = postfix.remove(postfix.size() - 1);

                postfix.add(num + String.valueOf(c));
            } else if (c == '(') {
                opStack.push(c);
            } else if (c == ')') {
                char pop = opStack.pop();
                while (pop != '(') {
                    postfix.add(String.valueOf(pop));
                    pop = opStack.pop();
                }
            } else {
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
        while (!opStack.isEmpty()) {
            postfix.add(String.valueOf(opStack.pop()));
        }

        return postfix;
    }
}
