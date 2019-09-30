package com.ezen.haneor.mytreat.nav_item_activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezen.haneor.mytreat.R;
import com.ezen.haneor.mytreat.mDTOClass.UserDTO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

public class RequstActivity extends AppCompatActivity {

    private TextView textView;
    private TextView textname;
    private TextView textemail;
    private TextView textphone;
    private EditText name;
    private EditText email;
    private EditText phone;
    private Button button;

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firedatabase.getReference("MANAGER");//RealtimeDB

    String userUID;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requst);

        checkKakaoSignUp();

        Toolbar toolbar = findViewById(R.id.activity_request_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("관리자 신청");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView = findViewById(R.id.activity_request_textView);
        textView.setText("무분별한 이벤트 참여 기능을 방지 하기 위해\n 관리자 계정 전환을 개발자에게\n" +
                "문의 하시기 바랍니다.\n\n 때에 따라 계정 전환에\n 시간이 지체 될수 있습니다.\n\n 이용해 주셔서 감사합니다.");
        textname = findViewById(R.id.activity_request_textname);
        textemail = findViewById(R.id.activity_request_textemail);
        textphone = findViewById(R.id.activity_request_textphone);

        name = findViewById(R.id.activity_request_editname);
        email = findViewById(R.id.activity_request_editemail);
        phone = findViewById(R.id.activity_request_editphone);

        mAdView = findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        button = findViewById(R.id.activity_request_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPhone = phone.getText().toString();

                UserDTO userDTO = new UserDTO(userName, userEmail, userPhone, userUID);//이름, 이메일, 폰번호, UUID
                myRef.push().setValue(userDTO);

                Toast.makeText(RequstActivity.this, "신청이 완료되었습니다, 처리에 다소 시간이 걸릴 수 있습니다", Toast.LENGTH_SHORT).show();

                finish();
            }
        });//신청버튼

    }//onCreate

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
    }//백버튼

    void checkKakaoSignUp() {
        if (Session.getCurrentSession().isOpened()) { // 카톡 로그인중인지 체크
            UserManagement.getInstance().requestMe(new MeResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.d("Kakao Closed","세션 닫힘");
                }

                @Override
                public void onNotSignedUp() {
                    Log.d("Kakao singeup", "로그인정보없음");
                }

                @Override
                public void onSuccess(UserProfile result) {
                    Log.d("Kakao Success", "로그인정보불러옴");
                    userUID  = result.getUUID(); //관리자 권한을 체크하기 위해 UUID불러옴
                }//onSuccess
            });
        }
    }//카카오 로그인 체크


}
