package com.ezen.haneor.mytreat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Random;

public class NumberActivity extends AppCompatActivity {

    private int startNumber = 0;
    private EditText editMax, editCount;//최대 숫자, 갯수
    private TextView textWin; //뽑은숫자
    private ArrayList<String> listviewString = new ArrayList<>();//리스트뷰 뿌려주기 용
    private Switch switchMin, switchEdit;//최소값, 입력값 설정
    private ListView listview;
    private boolean check, checkMin = false;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        Toolbar toolbar = findViewById(R.id.activity_number_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("번호 추첨");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView1 = (TextView) findViewById(R.id.text_number_max);  // 이미지뷰 클릭 시 데이터를 전송할 수 있게 다이얼로그 발생.
        TextView textView2 = (TextView) findViewById(R.id.text_number_count);
        textWin = (TextView) findViewById(R.id.text_number_win); //뽑힌 숫자
        editCount = (EditText) findViewById(R.id.edit_number_count); //갯수
        editMax =  (EditText) findViewById(R.id.edit_number_max); //최대값
        switchMin = findViewById(R.id.switch_number_min);
        switchEdit= findViewById(R.id.switch_number_edit);
        listview = (ListView) findViewById(R.id.listview);
        listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        final ArrayAdapter adapter = new ArrayAdapter(NumberActivity.this, android.R.layout.simple_list_item_1, listviewString);
        listview.setAdapter(adapter);

        switchMin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    checkMin = true;
                    switchMin.setText("0부터 시작");
                    editMax.setHint("1~숫자입력");
                    startNumber = 0;
                }else {
                    checkMin = false;
                    switchMin.setText("1부터 시작");
                    editMax.setHint("0~숫자입력");
                    startNumber = 1;
                }
            }
        });//최소값 설정상태 확인
        switchEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {//on상태일때
                    check = true;
                    switchEdit.setText("입력값 초기화");
                }else {
                    check = false;
                    switchEdit.setText("입력값 유지");
                }
            }
        });//초기화 설정상태 확인

        Button btnStart = findViewById(R.id.button_number_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int max = Integer.parseInt(editMax.getText().toString()); //최대값 받아옴
                int count = Integer.parseInt(editCount.getText().toString()); //갯수 받아옴

                int maxRandom;
                String winNumber = "";
                if(editMax.getText().length() == 0 || editCount.getText().length() == 0) { //최대 or 갯수를 입력 안하면
                    Toast.makeText(NumberActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if (editCount.getText().toString().equals("0")) { //갯수에 0을 입력하면
                    Toast.makeText(NumberActivity.this, "최소 1이상의 값을 입력해주세요", Toast.LENGTH_SHORT).show();

                }else if(max<=count) {
                    Toast.makeText(NumberActivity.this, "뽑을 갯수가 최대값보다 크거나 같을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Random random = new Random();
                    int max1[] = new int[count];

                    for(int i=0; i<count; i++) {
                        maxRandom = random.nextInt(max) + startNumber;
                        max1[i] = maxRandom;//랜덤숫자 저장

                        for(int j=0; j<i; j++) {
                            if(max1[i] == max1[j]){
                                i--;
                                break;
                            }
                        }//값 중복체크

                    }//값 저장

                    for(int i=0; i<max1.length; i++) {
                        winNumber += max1[i]+"  ";
                    } //String에 값을 주고

                    textWin.setText(winNumber);
                    listviewString.add(winNumber);
                    //값 출력, 저장

                    //리스트뷰에 리스트에 저장한 값을 뿌림
                    ArrayAdapter adapter = new ArrayAdapter(NumberActivity.this, android.R.layout.simple_list_item_1, listviewString);
                    listview.setAdapter(adapter) ;
                }
                if(check) {
                    reFreshEditText();
                }
            }
        });//시작버튼

        mAdView = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

    private void reFreshEditText() {
        editCount.setText(""); editMax.setText("");
    }
}
