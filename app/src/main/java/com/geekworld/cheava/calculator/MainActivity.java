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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView expressionText;
    private String oldExpression = "";
    private String btnText = "";
    private Expression expression = new Expression();

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

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        public void handleMessage(Message msg){

            switch(msg.what){
                case CAL_WORK:
                    String newExpression = expression.Parser(btnText, oldExpression);
                    ResultDisplay(newExpression);
                    break;
            }
        }
    };

    private void ResultDisplay(String expression){
         expressionText.setText(expression);
    }
}
