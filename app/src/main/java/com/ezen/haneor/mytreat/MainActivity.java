package com.ezen.haneor.mytreat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ezen.haneor.mytreat.eventActivity_item.EventActivity;
import com.ezen.haneor.mytreat.eventActivity_item.HLVAdapter;
import com.ezen.haneor.mytreat.mDTOClass.UserDTO;
import com.ezen.haneor.mytreat.nav_item_activity.InformationActivity;
import com.ezen.haneor.mytreat.nav_item_activity.ListActivity;
import com.ezen.haneor.mytreat.nav_item_activity.ManagerActivity;
import com.ezen.haneor.mytreat.nav_item_activity.PrivateActivity;
import com.ezen.haneor.mytreat.nav_item_activity.RequstActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.Session;
//import com.kakao.kakaolink.KakaoLink;
//import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firedatabase.getReference("USER");//RealtimeDB

    UserDTO userDTO;
    int count;

    private AdView mAdView;

    public static ResponseCallback<KakaoLinkResponse> callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 메인 6번째 항목 제목 입력
        TextView textView = findViewById(R.id.activity_main_image6);
        textView.setText("정보 & 문의");

        userDataSave(Session.getCurrentSession().isOpened());

        callback = new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {} //공유 실패시
            @Override
            public void onSuccess(KakaoLinkResponse result) { //공유 성공시 Toast메시지 띄움
                Toast.makeText(getApplicationContext(), "어플을 친구들과 공유해주세요!", Toast.LENGTH_SHORT).show();
            }
        };//callback 메소드

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_kakao) {
            shareKakaoLink();
        } else if (id == R.id.action_SMS) {
            Toast.makeText(this, "업데이트 예정입니다", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void shareKakaoLink() {//어플공유
        try {

            FeedTemplate params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder("내가쏜다 어플을 지금 다운로드 받으세요!",
                            "https://lorzx77.dothome.co.kr/icecream.png",
                            LinkObject.newBuilder().setWebUrl("https://developers.kakao.com") //마켓주소(아이콘클릭시)
                                    .setMobileWebUrl("https://developers.kakao.com").build()) //마켓주소(클릭시)
                            .build())
                    .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                            .setMobileWebUrl("'https://developers.kakao.com") //마켓주소(버튼클릭시)
                            .setAndroidExecutionParams("key1=value1")
                            .setIosExecutionParams("key1=value1")
                            .build()))
                    .build();

            Map<String, String> serverCallbackArgs = new HashMap<String, String>();
            serverCallbackArgs.put("user_id", "${current_user_id}");
            serverCallbackArgs.put("product_id", "${shared_product_id}");

            KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Logger.e(errorResult.toString());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
    }//카카오 링크 공유~~

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_end_event) {
            startActivity(new Intent(this, ListActivity.class));
        } else if (id == R.id.nav_manager) {
            startActivity(new Intent(this, ManagerActivity.class));
        } else if (id == R.id.nav_request) {
            startActivity(new Intent(this, RequstActivity.class));
        } else if (id == R.id.nav_private) {
            startActivity(new Intent(this, PrivateActivity.class));
        } else if (id == R.id.nav_logout)  {
            onClickLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickImageView(View view) {
        switch (view.getId()) {
            case R.id.activity_main_image1:
                startActivity(new Intent(this, EventActivity.class));
                break;
            case R.id.activity_main_image2:
                startActivity(new Intent(this, NumberActivity.class));
                break;
            case R.id.activity_main_image3:
                startActivity(new Intent(this, RandomLotteryActivity.class));
                break;
            case R.id.activity_main_image4:
                startActivity(new Intent(this, WheelActivity.class));
                break;
            case R.id.activity_main_image5:
                startActivity(new Intent(this, PaperLotActivity.class));
                break;
            case R.id.activity_main_image6:
                startActivity(new Intent(this, InformationActivity.class));
                break;
        }
    }

    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
            }
        });
    } //카카오톡 로그아웃

    void userDataSave (boolean signOn){ // 카톡 로그인중인지 체크
        if(signOn) {
            UserManagement.getInstance().requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("onFailure", errorResult + "");
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("onSessionClosed", errorResult + "");
                }

                @Override
                public void onSuccess(final UserProfile userProfile) {
                    Log.e("onSuccess", "연결 성공!");
                    myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            try {
                                String uid = userProfile.getUUID(); //로그인한 유저의 UUID
                                userDTO = dataSnapshot.getValue(UserDTO.class);
                                String dbUID = userDTO.getUserUUID(); //데이터 베이스의 UUID
                                HLVAdapter.userUUID = uid;
                                if (uid.equals(dbUID)) { //같은값이 있는지 확인
                                    count++;
                                } //중복확인
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (count == 0) {//일치하는 값이 없다면(=> count ==0)
                                try {
                                    if(userProfile.getEmail() == null) { //이메일 동의를 안했다면
                                        userDTO = new UserDTO(userProfile.getNickname(), "이메일설정안함"
                                                , "00000000000", userProfile.getUUID());
                                        //순서대로 유저이름, 이미지, 이메일, 폰번호, UUID
                                        myRef.child(userProfile.getUUID()).setValue(userDTO);//정보저장
                                    }else {
                                        userDTO = new UserDTO(userProfile.getNickname(), userProfile.getEmail()
                                                , "00000000000", userProfile.getUUID());
                                        //순서대로 유저이름, 이미지, 이메일, 폰번호, UUID
                                        myRef.child(userProfile.getUUID()).setValue(userDTO);//정보저장
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 2000); //중복확인후 데이터 저장

                }//onSuccess

                @Override
                public void onNotSignedUp() {
                    Log.e("onNotSignedUp", "onNotSignedUp");
                }
            });
        }
    }//유저데이터 저장


}
