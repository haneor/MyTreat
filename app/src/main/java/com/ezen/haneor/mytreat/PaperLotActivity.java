
package com.ezen.haneor.mytreat;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class PaperLotActivity extends AppCompatActivity {

    static ArrayList<String> itemList = new ArrayList<>();
    private Random random;
    private ImageView info;
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12;
    private Button[] buttonList ;
    String textNumber[] = new String[12];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_lot);

        Toolbar toolbar = findViewById(R.id.activity_paper_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("종이 뽑기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        info = findViewById(R.id.activity_paper_imageInfo);
        button1 = findViewById(R.id.activity_paper_button1);
        button2 = findViewById(R.id.activity_paper_button2);
        button3 = findViewById(R.id.activity_paper_button3);
        button4 = findViewById(R.id.activity_paper_button4);
        button5 = findViewById(R.id.activity_paper_button5);
        button6 = findViewById(R.id.activity_paper_button6);
        button7 = findViewById(R.id.activity_paper_button7);
        button8 = findViewById(R.id.activity_paper_button8);
        button9 = findViewById(R.id.activity_paper_button9);
        button10 = findViewById(R.id.activity_paper_button10);
        button11 = findViewById(R.id.activity_paper_button11);
        button12 = findViewById(R.id.activity_paper_button12);
        buttonList = new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12};

        for (int i = 0; i < textNumber.length; i++) {
            if(i<3) {
                textNumber[i] = "당첨";
            }else{
                textNumber[i] = ("꽝");
            }
        }//룰렛을 기본상태로 돌림( 당첨 - 꽝 )
        random = new Random();
        String emty = null;
        for(int m = 0; m<36; m++) {
            int j = random.nextInt(11);
            emty = textNumber[0];
            textNumber[0] = textNumber[j];
            textNumber[j] = emty;
        }

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemList.clear(); //리스트를 초기화 시킴
                PaperItemDialog paperItemDialog = new PaperItemDialog(PaperLotActivity.this);
                paperItemDialog.show();

                paperItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        String emty = null;
                        if (itemList.size() != 0) { //아이템을 한개라도 추가했다면
                            for (int i = 0; i < itemList.size(); i++) {
                                textNumber[i] = (itemList.get(i));
                            }
                        }
                        random = new Random();
                        for(int i = 0; i<36; i++) {
                            int j = random.nextInt(12);
                            emty = textNumber[0];
                            textNumber[0] = textNumber[j];
                            textNumber[j] = emty;
                        }

                    }
                });//dismiss 다이얼로그가 dismiss되면 불러옴

            }
        });//항목추가버튼 - 판에 항목을 추가

    }

    @Override
    // 메뉴 백버튼(왼쪽) 활성화
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFunctionSetting(View view) {

        switch (view.getId()) {
            case R.id.activity_paper_button1:
                button1.setBackgroundResource(R.drawable.paperitemback);
                button1.setText(textNumber[0]);
                break;
            case R.id.activity_paper_button2:
                button2.setBackgroundResource(R.drawable.paperitemback);
                button2.setText(textNumber[1]);
                break;
            case R.id.activity_paper_button3:
                button3.setBackgroundResource(R.drawable.paperitemback);
                button3.setText(textNumber[2]);
                break;
            case R.id.activity_paper_button4:
                button4.setBackgroundResource(R.drawable.paperitemback);
                button4.setText(textNumber[3]);
                break;
            case R.id.activity_paper_button5:
                button5.setBackgroundResource(R.drawable.paperitemback);
                button5.setText(textNumber[4]);
                break;
            case R.id.activity_paper_button6:
                button6.setBackgroundResource(R.drawable.paperitemback);
                button6.setText(textNumber[5]);
                break;
            case R.id.activity_paper_button7:
                button7.setBackgroundResource(R.drawable.paperitemback);
                button7.setText(textNumber[6]);
                break;
            case R.id.activity_paper_button8:
                button8.setBackgroundResource(R.drawable.paperitemback);
                button8.setText(textNumber[7]);
                break;
            case R.id.activity_paper_button9:
                button9.setBackgroundResource(R.drawable.paperitemback);
                button9.setText(textNumber[8]);
                break;
            case R.id.activity_paper_button10:
                button10.setBackgroundResource(R.drawable.paperitemback);
                button10.setText(textNumber[9]);
                break;
            case R.id.activity_paper_button11:
                button11.setBackgroundResource(R.drawable.paperitemback);
                button11.setText(textNumber[10]);
                break;
            case R.id.activity_paper_button12:
                button12.setBackgroundResource(R.drawable.paperitemback);
                button12.setText(textNumber[11]);
                break;
        }
    }

}
