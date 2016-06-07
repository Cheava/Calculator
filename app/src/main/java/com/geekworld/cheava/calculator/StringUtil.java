package com.geekworld.cheava.calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangzh on 2016/6/7.
 */
class StringUtil {

    private static final int MAX_NUM_WIDTH = 7;
    private static final int MAX_INPUT_WIDTH = 18;

    public int FindOperator(String[] expression){
        for(int index = 0;index < expression.length;index++){
            if(isOperatorSymbol(expression[index])){
                return index;
            }
        }
        return 0;
    }

    private int GetPriority(String symbol){
        int priority = 0;
        if((symbol.equals("x"))||(symbol.equals("/"))){
            priority = 1;
        }
        if((symbol.equals("+"))||(symbol.equals("-"))){
            priority = 0;
        }
        return priority;
    }

    public Boolean isLastCharDigit(String string){
        if(!string.isEmpty()) {
            String lastchar = string.substring(string.length() - 1);
            return Character.isDigit(lastchar.charAt(0));
        }
        else{
            return false;
        }
    }

    public Boolean isHigherPriority(String symbolpri, String symbolnxt){
        return GetPriority(symbolpri) > GetPriority(symbolnxt);
    }

    public Boolean isOperatorSymbol(String string){
        switch (string){
            case "+":
            case "-":
            case "x":
            case "/":
                return true;

            case "=":
                throw new RuntimeException();

            default:
                return false;
        }
    }

    public Boolean isInputOverload(String oldExpression){
        if( oldExpression.length()>= MAX_INPUT_WIDTH){
            return true;
        }

        int inputNumWidth = 0;
        String pattern = "\\d+";
        Matcher matcher =  Pattern.compile(pattern).matcher(oldExpression);
        while(matcher.find()){
            inputNumWidth = matcher.end() - matcher.start();
        }
        //最后的操作数
        return (inputNumWidth >= MAX_NUM_WIDTH) && (isLastCharDigit(oldExpression));
    }

    public Boolean isMoreDot(String oldExpression , String btnText){
        String testexpression = oldExpression + btnText;
        //正则表达式为\S*\.\d+\.
        String pattern = "\\S*\\.\\d+\\.";
        Boolean result = testexpression.matches(pattern);
        return result;
    }
}
