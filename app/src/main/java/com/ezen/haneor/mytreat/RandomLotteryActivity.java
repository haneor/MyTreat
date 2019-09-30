package com.ezen.haneor.mytreat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Random;

public class RandomLotteryActivity extends AppCompatActivity {
    EditText editUser, editCount;//추가할 유저, 뽑을 갯수
    TextView textWinUser; //뽑힌 유저
    ImageView imageFire, imageFire1; //폭죽이미지
    ArrayList<String> listUser = new ArrayList<>(); //추가한 유저
    ArrayList<String> winUser = new ArrayList<>();  //리스트뷰 추가용인데 쓸모없어짐
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_lottery);

        Toolbar toolbar = findViewById(R.id.activity_basiclottery_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("랜덤 추첨");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView1 = findViewById(R.id.text_basic_count);
        TextView textView2 = findViewById(R.id.text_basic_user);
        editCount = findViewById(R.id.edit_basic_count);
        editUser = findViewById(R.id.edit_basic_user);
        textWinUser = findViewById(R.id.text_basic_winuser);
        final ListView listViewUser = (ListView) findViewById(R.id.listview_basic_user);
        imageFire1 = findViewById(R.id.image_basic_fire1);
        imageFire = findViewById(R.id.image_basic_fire);
        imageFire.setVisibility(View.INVISIBLE);
        imageFire1.setVisibility(View.INVISIBLE);

        winUser.clear();
        textWinUser.setText("");

        Button buttonPlus = findViewById(R.id.button_basic_plus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addusers();
                editUser.setText("");
                ArrayAdapter adapter = new ArrayAdapter(RandomLotteryActivity.this, android.R.layout.simple_list_item_1, listUser);
                listViewUser.setAdapter(adapter) ;
            }
        });//유저 추가 버튼

        Button buttonStart = findViewById(R.id.button_basic_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startButton();
                editCount.setText("");

            }
        });//시작 버튼

        mAdView = findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void addusers () {
        if(editUser.length() == 0) {// 값을 입력하지 않으면
            Toast.makeText(RandomLotteryActivity.this, "최소 1자이상 입력해주세요", Toast.LENGTH_SHORT).show();
        }else {
            if(listUser.size() == 0) { //추가한 유저가 없다면(0)
                listUser.add(editUser.getText().toString());
            }else {
                int checkOverlab = 0;//중복 체크용
                for(int i=0; i<listUser.size(); i++) {
                    if(editUser.getText().toString().equals(listUser.get(i))){//추가할 값이 저장된 값과 동일하다면
                        checkOverlab++;
                    }
                }
                if(checkOverlab > 0) { //중복이 있있다면
                    Toast.makeText(RandomLotteryActivity.this, "중복된 값입니다", Toast.LENGTH_SHORT).show();
                }else { //중복이 없다면
                    listUser.add(editUser.getText().toString());
                }
            }
        }
    }

    // 시작 버튼 눌렀을 경우 랜덤값으로 결과 확인
    private void startButton () {
        String winUser2 = "";
        if(editCount.length() == 0 ) { //갯수 설정을 안하면
            Toast.makeText(RandomLotteryActivity.this, "갯수를 설정해주세요", Toast.LENGTH_SHORT).show();
        }else if(listUser.size() == 0 || listUser.size() == 1) { //추가한 유저가 없거나 한명이면
            Toast.makeText(RandomLotteryActivity.this, "최소 2개이상의 항목이 필요합니다", Toast.LENGTH_SHORT).show();
        }else if(editCount.getText().equals("0")){ //갯수를 0으로 설정하면
            Toast.makeText(RandomLotteryActivity.this, "최소 1이상의 값을 입력해주세요", Toast.LENGTH_SHORT).show();
        }else if(listUser.size() <= Integer.parseInt(editCount.getText().toString())) { //유저보다 뽑을 갯수가 많으면
            Toast.makeText(RandomLotteryActivity.this, "갯수는 항목보다 작아야 합니다", Toast.LENGTH_SHORT).show();
        }else {
            Random ran = new Random();
            int userSize = listUser.size();
            int count = Integer.parseInt(editCount.getText().toString());
            String user[] = new String[count];

            for(int i=0; i<count; i++) {
                int selectRanUser = ran.nextInt(userSize); //유저 수만큼 랜덤 수 발생 0~max
                // winUser.add(listUser.get(selectRanUser));
                user[i] = listUser.get(selectRanUser); //배열에 랜덤 유저 저장

                for(int j=0; j<i; j++) {
                    if(user[i].equals(user[j])){
                        i--;
                        break;
                    }
                }//값 중복체크

            }//값 저장

            for(int i=0; i<count; i++) {
                if(i!=count-1) {
                    winUser2 += user[i] + ", ";
                }else {
                    winUser2 += user[i];
                }
            }//저장된 값 String에 저장

            textWinUser.setText(""); //초기화

            imageFire.setVisibility(View.VISIBLE);
            imageFire1.setVisibility(View.VISIBLE);//숨겨놨던 이미지 보여줌

//            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageFire);
//            Glide.with(this).load(R.raw.firework).into(imageFire);
//            GlideDrawableImageViewTarget imageViewTarget2 = new GlideDrawableImageViewTarget(imageFire1);
//            Glide.with(this).load(R.raw.firework).into(imageFire1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageFire.setVisibility(View.INVISIBLE);
                    imageFire1.setVisibility(View.INVISIBLE);
                }
            }, 5500);//5.5초(대략3번 터짐)후 숨김

            String str = "축하합니다!" +"\n" + winUser2;
            final SpannableStringBuilder sps = new SpannableStringBuilder(str);
            sps.setSpan(new AbsoluteSizeSpan(90), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textWinUser.append(sps); //0~6번 인덱스의 텍스트 사이즈를 조절함(마지막 6번은 제외)
        }
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
