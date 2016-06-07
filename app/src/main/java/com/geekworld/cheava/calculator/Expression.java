package com.geekworld.cheava.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by wangzh on 2016/6/7.
 */
public class Expression {
    private Boolean calFinish = false;
    private String oldExpression = "";
    private String newExpression = "";
    private String btnText = "";
    private final StringUtil tools = new StringUtil();

    class ExpressionStack extends Stack {
        private String[] popAll(){
            String[] result = new String[this.size()];
            int i = this.size() - 1;
            while (!this.empty())
            {
                result[i] = (String)this.pop();
                i--;
            }
            return result;
        }
    }

    private String[] toReversePolishNotation(String expression){
        String singlechar;
        StringBuffer floatNum = new StringBuffer();
        String storeString ;
        ExpressionStack expressionStack = new ExpressionStack();
        ExpressionStack symStack = new ExpressionStack();
        for(int i =0; i < expression.length();i++){
            singlechar = expression.substring(i,i+1);
            try {
                if (!tools.isOperatorSymbol(singlechar)) {
                    //将浮点数存在缓冲变量中
                    floatNum.append(singlechar);
                    //将表达式最后一个数字压栈
                    if( i == (expression.length() - 1)){
                        expressionStack.push(floatNum.toString());
                    }
                } else {
                    //将缓冲变量压栈
                    expressionStack.push(floatNum.toString());
                    floatNum = new StringBuffer() ;
                    while ((!symStack.empty()) && (!tools.isHigherPriority(singlechar,(String)symStack.peek()))){
                        storeString = (String) symStack.pop();
                        expressionStack.push(storeString);
                    }
                    symStack.push(singlechar);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        while (!symStack.empty()){
            storeString = (String)symStack.pop();
            expressionStack.push(storeString);
        }
        String[] result = expressionStack.popAll();
        return result;
    }

    private void NumAppend(){
        if(calFinish)
        {
            oldExpression = "";
            calFinish = false;
        }
        if(!tools.isInputOverload(oldExpression)){
            newExpression = oldExpression + btnText;
        }
    }

    private void SymAppend(){
        if(tools.isLastCharDigit(oldExpression) && !tools.isMoreDot(oldExpression,btnText)){
            newExpression = oldExpression + btnText;
            calFinish = false;
        }
    }

    private String DoCalculate(String num1 , String operator, String num2){
        final int scale = 7;
        BigDecimal bigDecimal1 = new BigDecimal(num1);
        BigDecimal bigDecimal2 = new BigDecimal(num2);
        BigDecimal resultBigdecimal = bigDecimal1;
        String convString;
        String finalString;
        switch (operator){
            case "+":
                resultBigdecimal = bigDecimal1.add(bigDecimal2);
                break;

            case "-":
                resultBigdecimal = bigDecimal1.subtract(bigDecimal2);
                break;

            case "x":
                resultBigdecimal = bigDecimal1.multiply(bigDecimal2);
                break;

            case "/":
                try{
                    resultBigdecimal = bigDecimal1.divide(bigDecimal2, scale,BigDecimal.ROUND_HALF_UP );
                }catch (ArithmeticException  e){
                    return "∞";
                }
                break;

            default:
                break;
        }
        calFinish = true;
        Double result = resultBigdecimal.doubleValue();
        convString = Double.toString(result);
        if(convString.matches("\\d+\\.0$"))
        {
            //结果为整数，还原为整数字符串
            finalString = Integer.toString(resultBigdecimal.intValue());
        }else {
            finalString = convString;
        }
        return finalString;
    }

    private String toCalExp(String[] expression){
        String[] workArray = expression;
        String[] newArray = workArray;
        String result ;

        int index ;
        while (newArray.length > 1){
            workArray = newArray;
            index = tools.FindOperator(workArray);
            if(index == 0 ){
                return Arrays.toString(expression);
            }
            result = DoCalculate(String.valueOf(workArray[index - 2]),String.valueOf(workArray[index]),String.valueOf(workArray[index - 1]));
            List<String> list= Arrays.asList(workArray);
            List<String> arrayList = new ArrayList<>(list);
            arrayList.set(index,result);
            arrayList.remove(index - 1);
            arrayList.remove(index - 2);
            newArray = arrayList.toArray(new String[arrayList.size()]);
        }
        result = newArray[0];
        return result;
    }

    private void Cal(){
        if(tools.isLastCharDigit(oldExpression)){
            String[] notation = toReversePolishNotation(oldExpression);
            newExpression = toCalExp(notation);
        }else {
            newExpression =  oldExpression;
        }
    }

    private void Clear(){
        newExpression = "";
    }

    private void Del(){
        if(!oldExpression.isEmpty()) {
            if(calFinish)
            {
                Clear();
                calFinish = false;
            }else{
                newExpression = oldExpression.substring(0,oldExpression.length()-1);
            }
        }
    }

    private void ERR() {
        newExpression = "ERROR";
    }

    public String Parser(String btnText, String oldExpression){
        this.oldExpression = oldExpression;
        this.btnText =  btnText;

        switch(btnText){
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "0":
                NumAppend();
                break;

            case ".":
            case "+":
            case "-":
            case "x":
            case "/":
                SymAppend();
                break;

            case "=":
                Cal();
                break;

            case "Clear":
                Clear();
                break;

            case "Del":
                Del();
                break;

            default:
                ERR();
                break;
        }
        return newExpression;
    }
}
