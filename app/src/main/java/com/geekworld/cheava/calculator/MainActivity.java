package com.geekworld.cheava.calculator;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView expressionText;
    private String oldExpression = "";
    private String newExpression = "";
    private String btnText = "";
    private Boolean calFinish = false;
    private static final int MAX_NUM_WIDTH = 7;
    private static final int MAX_INPUT_WIDTH = 18;

    private static final int  CAL_WORK = 1;


    private final String[] btnString = new String[]{
                    "7", "8", "9", "/",
                    "4", "5", "6", "x",
                    "1", "2", "3", "-",
                    ".", "0", "=", "+"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        getSupportActionBar().hide();

        Button clrbtn = (Button)findViewById(R.id.btnClearText);
        Button delbtn = (Button)findViewById(R.id.btnDeleteText);
        clrbtn.setOnClickListener(this);
        delbtn.setOnClickListener(this);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,0,1.0f);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        LinearLayout rowLinear = null;
        for(int ii = 0; ii < btnString.length; ii++){
            if( ii % 4 == 0){
                rowLinear = new LinearLayout(this);
                rowLinear.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(rowLinear,layoutParams);
            }

            Button btn = new Button(this);
            btn.setText(btnString[ii]);
            btn.setTextSize(40);
            btn.setHeight(1000);
            btn.setAllCaps(false);
            rowLinear.addView(btn);
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button)v;
        btnText = btn.getText().toString();

        expressionText = (TextView) findViewById(R.id.expressionTextView);
        oldExpression = expressionText.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = CAL_WORK;
                handler.sendMessage(msg);
            }
        }).start();
    }

    class ExpressionStack extends Stack{
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

    private Boolean isLastCharDigit(String string){
        if(!string.isEmpty()) {
            String lastchar = string.substring(string.length() - 1);
            return Character.isDigit(lastchar.charAt(0));
        }
        else{
            return false;
        }
    }

    private Boolean isHigherPriority(String symbolpri, String symbolnxt){
        return GetPriority(symbolpri) > GetPriority(symbolnxt);
    }

    private Boolean isOperatorSymbol(String string){
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

    private Boolean isInputOverload(){
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

    private Boolean isMoreDot(){
        String testexpression = oldExpression + btnText;
        //正则表达式为\S*\.\d+\.
        String pattern = "\\S*\\.\\d+\\.";
        //TODO: fix the regulation to match the string like "5.551.3"
        Boolean result = testexpression.matches(pattern);
        return result;
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

    private int FindOperator(String[] expression){
        for(int index = 0;index < expression.length;index++){
            if(isOperatorSymbol(expression[index])){
                return index;
            }
        }
        return 0;
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
                if (!isOperatorSymbol(singlechar)) {
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
                    while ((!symStack.empty()) && (!isHigherPriority(singlechar,(String)symStack.peek()))){
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

    private void ExpressionNumAppend(){
        if(calFinish)
        {
            oldExpression = "";
            calFinish = false;
        }
        if(!isInputOverload()){
            newExpression = oldExpression + btnText;
        }
    }

    private void ExpressionSymAppend(){
        if(isLastCharDigit(oldExpression) && !isMoreDot()){
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
            index = FindOperator(workArray);
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

    private void ExpressionCal(){
        if(isLastCharDigit(oldExpression)){
            String[] notation = toReversePolishNotation(oldExpression);
            newExpression = toCalExp(notation);
        }else {
            newExpression =  oldExpression;
        }
    }

    private void ExpressionClear(){
        newExpression = "";
    }

    private void ExpressionDel(){
        if(!oldExpression.isEmpty()) {
            if(calFinish)
            {
                ExpressionClear();
                calFinish = false;
            }else{
                newExpression = oldExpression.substring(0,oldExpression.length()-1);
            }
        }
    }

    private void ExpressionERR() {
        newExpression = "ERROR";
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case CAL_WORK:
                    ExpressionParser();
                    ResultDisplay();
                    break;
            }
        }
    };

    private void ExpressionParser(){
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
                ExpressionNumAppend();
                break;

            case ".":
            case "+":
            case "-":
            case "x":
            case "/":
                ExpressionSymAppend();
                break;

            case "=":
                ExpressionCal();
                break;

            case "Clear":
                ExpressionClear();
                break;

            case "Del":
                ExpressionDel();
                break;

            default:
                ExpressionERR();
                break;
        }
    }

    private void ResultDisplay(){
         expressionText.setText(newExpression);
    }

}
