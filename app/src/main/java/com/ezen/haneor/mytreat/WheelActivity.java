package com.ezen.haneor.mytreat;

import android.content.DialogInterface;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class WheelActivity extends AppCompatActivity {
    /**
     *  아이템을 추가하지 않아도 돌릴수 있음 .
     *  아이템을 6개 다 채우지 않아도 돌릴수 있음.
     */

    static ArrayList<String> itemList = new ArrayList<>(); //리스트뷰에 보여줄 아이템리스트
    ImageView imageWheel; //룰렛이미지
    TextView textone, texttwo, textthree, textfour, textfive, textsix; //빨강색(one)부터 시계방향으로
    ConstraintLayout constraintLayout; //텍스트를 돌려줄 레이아웃
    int degree = 0; //시작각도
    int degree_last = 360; //끝 각도


    final Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg){
            degree = msg.what;
//            rotate(constraintLayout, degree, degree_last);
            rotate(constraintLayout, degree, degree_last);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        constraintLayout = findViewById(R.id.wheel_layout);

        Toolbar toolbar = findViewById(R.id.activity_wheel_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("행운의 룰렛");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textone = findViewById(R.id.text_wheel_one);
        texttwo = findViewById(R.id.text_wheel_two);
        textthree = findViewById(R.id.text_wheel_three);
        textfour = findViewById(R.id.text_wheel_four);
        textfive = findViewById(R.id.text_wheel_five);
        textsix = findViewById(R.id.text_wheel_six);

        final TextView textNumber[] = {textone, texttwo, textthree, textfour, textfive, textsix};//텍스트뷰 배열

        final int animID[] = {R.anim.rotate1, R.anim.rotate2, R.anim.rotate3, R.anim.rotate4, R.anim.rotate5, R.anim.rotate6, R.anim.rotate7
                ,R.anim.rotate8 , R.anim.rotate9, R.anim.rotate10, R.anim.rotate11, R.anim.rotate12, R.anim.rotate13, R.anim.rotate14,
                R.anim.rotate15, R.anim.rotate16, R.anim.rotate17, R.anim.rotate18,R.anim.rotate19, R.anim.rotate20, R.anim.rotate21};
        //애니메이션

        final int effect[] = {R.anim.bounce, R.anim.constant, R.anim.fastandslow, R.anim.overshoot, R.anim.veryfastandslow,
                R.anim.slowandfast
        };
        //효과

        Button button = findViewById(R.id.button_wheel_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random ran = new Random();
                int ranAnim = ran.nextInt(animID.length);
                int ranEffect = ran.nextInt(effect.length);

                Animation rote = AnimationUtils.loadAnimation(
                        WheelActivity.this, animID[ranAnim]); //랜덤 애니메이션 생성
                rote.setFillAfter(true); //애니가 끝나도 상태 유지
                rote.setInterpolator(AnimationUtils.loadInterpolator(WheelActivity.this, effect[ranEffect])); //랜덤 효과
                constraintLayout.startAnimation(rote);

            }
        });//시작버튼 - 판을 돌림

        Button buttonPlus = findViewById(R.id.button_wheel_plus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemList.clear(); //리스트를 초기화 시킴
                for(int i=0; i<textNumber.length; i++) {
                    if(i == 0 || i%2 == 0) {
                        textNumber[i].setText("당첨");
                    }else {
                        textNumber[i].setText("꽝");
                    }
                }//룰렛을 기본상태로 돌림( 당첨 - 꽝 )

                WheelItemDialog wheelItemDialog = new WheelItemDialog(WheelActivity.this);
                wheelItemDialog.show();

                wheelItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(itemList.size() != 0) { //아이템을 한개라도 추가했다면
                            for(int i=0; i<itemList.size(); i++) {
                                textNumber[i].setText(itemList.get(i));
                            }
                        }
                    }
                });//dismiss 다이얼로그가 dismiss되면 불러옴

            }
        });//항목추가버튼 - 판에 항목을 추가

    }//onCreate

    private void rotate(View view, double fromDegrees, double toDegrees) {
        final RotateAnimation rotate = new RotateAnimation((float) fromDegrees, (float) toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(2000);
        rotate.setFillAfter(true);

        view.startAnimation(rotate);
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

}
