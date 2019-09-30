package com.ezen.haneor.mytreat.eventActivity_item;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezen.haneor.mytreat.CreateLotteryActivity;
import com.ezen.haneor.mytreat.LotteryActivity;
import com.ezen.haneor.mytreat.MainActivity;
import com.ezen.haneor.mytreat.R;
import com.ezen.haneor.mytreat.SplashActivity;
import com.ezen.haneor.mytreat.mDTOClass.RoomDTO;
import com.ezen.haneor.mytreat.mDTOClass.UserDTO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter searchAdapter;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<String> alName = new ArrayList<>();
    private ArrayList<Integer> alJoinInUser = new ArrayList<>();
    private ArrayList<String> alTitle = new ArrayList<>();
    private ArrayList<String> alsub = new ArrayList<>();
    private ArrayList<String> aldate = new ArrayList<>();
    private ArrayList<String> alDataKey = new ArrayList<>();
    private ArrayList<String> alEventCode = new ArrayList<>();
    private ArrayList<String> userEventCode = new ArrayList<>();

    private ArrayList<String> alName_search = new ArrayList<>();
    private ArrayList<Integer> alJoinInUser_search = new ArrayList<>();
    private ArrayList<String> alTitle_search = new ArrayList<>();
    private ArrayList<String> alsub_search = new ArrayList<>();
    private ArrayList<String> aldate_search = new ArrayList<>();

    private ImageView add;
    private TextView textNoEvent;
    private SearchView searchView;

    private AdView mAdView;

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef1 = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB

    RoomDTO roomDTO;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Toolbar toolbar = findViewById(R.id.activity_event_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("이벤트 현황");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Session.getCurrentSession().isOpened()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("안내") // 제목부분 텍스트
                    .setMessage("이벤트 참여는 카카오톡 로그인을 해야합니다") // 내용부분 텍스트
                    .setPositiveButton("종료", //종료버튼을 누르면 앱 종료
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick( DialogInterface dialog, int which )
                                {
                                    startActivity(new Intent(EventActivity.this, MainActivity.class));
                                    finish(); //종료
                                }
                            }
                    ).show();
        }  //카카오톡 로그인상태 체크(사용할수도있으니 삭제 NOPE!)

        // 리사이클 뷰 넣기
        recyclerView = findViewById(R.id.activity_event_recyclerView);
        setRecyclerView();

        add = findViewById(R.id.activity_event_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventActivity.this, CreateLotteryActivity.class));
            }
        });

        textNoEvent = findViewById(R.id.text_event_noevent);
        textNoEvent.setVisibility(View.INVISIBLE);

        searchView = findViewById(R.id.activity_event_search);

        joinInUser();
        setSearchView();
        // 광고
        mAdView = findViewById(R.id.adView2);
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

    // 여기에 recyclerview 호출 메소드 넣기
    private void setRecyclerView () {
        myRef1 = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB
        myRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                alName.clear();
                alTitle.clear();
                alsub.clear();
                aldate.clear();
                alJoinInUser.clear();
                alEventCode.clear();
                alDataKey.clear();

                try {
                    roomDTO = dataSnapshot.getValue(RoomDTO.class);
                    if(roomDTO.getEndTime() == null) {
                        String manager = roomDTO.getManager().toString();
                        String title = roomDTO.getTitle();
                        String sub = roomDTO.getSub();
                        String date = roomDTO.getStartTime();
                        int joinInUser = roomDTO.getJoinInUser();
                        String eventCode = roomDTO.getEventCode();
                        String dataKey = dataSnapshot.getKey();

                        alName.add(manager);
                        alTitle.add(title);
                        alsub.add(sub);
                        aldate.add(date);
                        alJoinInUser.add(joinInUser);
                        alEventCode.add(eventCode);
                        int imageResource = R.drawable.icon;
                        alDataKey.add(dataKey);

                        mAdapter = new HLVAdapter(EventActivity.this, alTitle, imageResource, alName, alsub, aldate, alJoinInUser);
                        recyclerView.setAdapter(mAdapter);
                        count++;
                    }
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(count == 0) {
                            textNoEvent.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                alName.clear();
                alTitle.clear();
                alsub.clear();
                aldate.clear();
                alJoinInUser.clear();
                alEventCode.clear();
                alDataKey.clear();
                try {
                    if (roomDTO.getEndTime() == null) {
                        String manager = roomDTO.getManager().toString();
                        String title = roomDTO.getTitle();
                        String sub = roomDTO.getSub();
                        String date = roomDTO.getStartTime();
                        int joinInUser = roomDTO.getJoinInUser();
                        String eventCode = roomDTO.getEventCode();
                        String dataKey = dataSnapshot.getKey();

                        alName.add(manager);
                        alTitle.add(title);
                        alsub.add(sub);
                        aldate.add(date);
                        alJoinInUser.add(joinInUser);
                        alEventCode.add(eventCode);
                        int imageResource = R.drawable.icon;
                        alDataKey.add(dataKey);

                        RecyclerView.Adapter mAdapter = new HLVAdapter(EventActivity.this, alTitle, imageResource, alName, alsub, aldate, alJoinInUser);
                        recyclerView.setAdapter(mAdapter);
                        count++;
                    }
                }catch (NullPointerException e) {
                    e.printStackTrace();
                    textNoEvent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void joinInUser() {
        final DatabaseReference myRef = firedatabase.getReference("USER");//RealtimeDB
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                try {
                    userEventCode.add(userDTO.getEventCode());
                    for(int i=0; i<alEventCode.size(); i++) {
                        if (alEventCode.get(i).equals(userEventCode.get(i))){
                            int a = alJoinInUser.get(i);
                            a += 1;
                            alJoinInUser.set(i, a);

                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put(alDataKey.get(i)+"/joinInUser", alJoinInUser.get(i));
                            myRef1.updateChildren(taskMap);
                        }
                    }

                    setRecyclerView ();
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                for(int i = 0; i<alName.size(); i++) {
                    if(s.equals(alName.get(i)) || s.equals(alTitle.get(i))) {
                        alName_search.add(alName.get(i));
                        alTitle_search.add(alTitle.get(i));
                        alsub_search.add(alsub.get(i));
                        aldate_search.add(aldate.get(i));
                        alJoinInUser_search.add(alJoinInUser.get(i));
                        int imageResource = R.drawable.icon;
                        searchAdapter = new HLVAdapter(EventActivity.this, alTitle_search, imageResource, alName_search, alsub_search, aldate_search, alJoinInUser_search);
                        recyclerView.setAdapter(searchAdapter);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

//    void checkKakaoSignUp() {
//        if (Session.getCurrentSession().isOpened()) { // 카톡 로그인중인지 체크
//            UserManagement.getInstance().requestMe(new MeResponseCallback() {
//                @Override
//                public void onSessionClosed(ErrorResult errorResult) {
//                    Log.d("Kakao Closed","세션 닫힘");
//                }
//
//                @Override
//                public void onNotSignedUp() {
//                    Log.d("Kakao singeup", "로그인정보없음");
//                }
//
//                @Override
//                public void onSuccess(UserProfile result) {
//                    Log.d("Kakao Success", "로그인정보불러옴");
//                }
//            });
//
//        }else { //카톡 로그인이 안되어있다면
//            new AlertDialog.Builder(this)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("안내") // 제목부분 텍스트
//                    .setMessage("이벤트 참여는 카카오톡 로그인을 해야합니다") // 내용부분 텍스트
//                    .setPositiveButton("종료", //종료버튼을 누르면 앱 종료
//                            new DialogInterface.OnClickListener()
//                            {
//                                @Override
//                                public void onClick( DialogInterface dialog, int which )
//                                {
//                                    startActivity(new Intent(CreateLotteryActivity.this, MainActivity.class));
//                                    finish(); //종료
//                                }
//                            }
//                    ).show();
//        }
//    }//카카오 로그인 체크

}
