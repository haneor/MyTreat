package com.ezen.haneor.mytreat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

import com.ezen.haneor.mytreat.eventActivity_item.EventActivity;
import com.ezen.haneor.mytreat.eventActivity_item.HLVAdapter;
import com.ezen.haneor.mytreat.mDTOClass.RoomDTO;
import com.ezen.haneor.mytreat.mDTOClass.UserDTO;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.User;
import com.kakao.usermgmt.response.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LotteryActivity extends AppCompatActivity {

    private TextView substance;
    private String toolBarTitle;
    private TextView name;
    private TextView nickname;
    private TextView email;
    private TextView phone;
    private TextView comment;
    private EditText edit_name;
    private EditText edit_nickname;
    private EditText edit_email;
    private EditText edit_phone;
    private EditText edit_comment;
    private EditText edit_pw;
    private Button saveButton;

    String userUUID, userName, userEmail;//현재 사용자의 정보

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firedatabase.getReference("USER");//RealtimeDB

    private ArrayList<String> alTitle = new ArrayList<>();
    private ArrayList<String> alsub = new ArrayList<>();
    private ArrayList<String> alEventCode = new ArrayList<>();
    private ArrayList<String> alManagerUid = new ArrayList<>();
    private ArrayList<Integer> alJoinInUser = new ArrayList<>();
    private ArrayList<String> alDataKey = new ArrayList<>();
    private ArrayList<String> alPw = new ArrayList<>();
    public static int position; //어뎁터에서 포지션 받음
    String roomEventCode; //이벤트코드
    Toolbar toolbar;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        loadData();//방정보(타이틀, 내용, 이벤트코드)를 불러옴

        toolbar = findViewById(R.id.activity_lottery_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //title = "선택된 방의 제목"; // 선택된 방의 제목을 가져온다.
        substance = findViewById(R.id.activity_lottery_sub);
        name = findViewById(R.id.activity_lottery_text1);
        nickname = findViewById(R.id.activity_lottery_text2);
        email = findViewById(R.id.activity_lottery_text3);
        phone = findViewById(R.id.activity_lottery_text4);
        comment = findViewById(R.id.activity_lottery_textComment);

        edit_pw = findViewById(R.id.activity_lottery_edit);
        edit_name = findViewById(R.id.activity_lottery_edit1);
        edit_nickname = findViewById(R.id.activity_lottery_edit2);
        edit_email = findViewById(R.id.activity_lottery_edit3);
        edit_phone = findViewById(R.id.activity_lottery_edit4);
        edit_comment = findViewById(R.id.activity_lottery_editComment);

        saveButton = findViewById(R.id.activity_lottery_saveButton);
        // 여기서 name/nickname/email/phone의 정보를 불러 온다.

        checkKakaoSignUp();//카카오 로그인 체크

        // 기본 기능 설정
        onFunctionSetting();

    }//onCreate

    @Override
    // 메뉴 백버튼(왼쪽) 활성화
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                startActivity(new Intent(this, EventActivity.class));
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onFunctionSetting() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = edit_name.getText().toString();
                String nickName = edit_nickname.getText().toString();
                final String email = edit_email.getText().toString();
                final String phone = edit_phone.getText().toString();
                final String usercomment = edit_comment.getText().toString(); // 서버로 저장

                final DatabaseReference myRef1 = firedatabase.getReference("USER");//RealtimeDB
                    myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                            try {
                                String dbuid = userDTO.getUserUUID();
                                if(userUUID.equals(dbuid)) { //현재사용자의UID와 같을때
                                    if(email.length() == 0 || phone.length() == 0) {
                                        Toast.makeText(LotteryActivity.this, "모든 내용을 작성해주세요", Toast.LENGTH_SHORT).show();
                                    }else {
                                        if (usercomment.length() == 0) { //코멘트 입력 안하면
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put(userUUID + "/eventCode", roomEventCode);
                                            taskMap.put(userUUID + "/userEmail", email);
                                            taskMap.put(userUUID + "/userRealName", name);
                                            taskMap.put(userUUID + "/comment", "코멘트를 입력하지 않았습니다");
                                            taskMap.put(userUUID + "/phoneNumber", phone);

                                            if(edit_pw.getText().toString().equals(alPw.get(position))) {
                                                myRef1.updateChildren(taskMap); //현재 유저의 UUID노드(child)에 데이터 업데이트(추가)
                                                Toast.makeText(LotteryActivity.this, "응모에 참여해 주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else {
                                                Toast.makeText(LotteryActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                            }
                                        } else { //코멘트 입력 하면
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put(userUUID + "/eventCode", roomEventCode);
                                            taskMap.put(userUUID + "/userEmail", email);
                                            taskMap.put(userUUID + "/userRealName", name);
                                            taskMap.put(userUUID + "/comment", usercomment);
                                            taskMap.put(userUUID + "/phoneNumber", phone);
                                            Log.d("Response", alPw.get(position));
                                            if(edit_pw.getText().toString().equals(alPw.get(position))) {
                                                myRef1.updateChildren(taskMap); //현재 유저의 UUID노드(child)에 데이터 업데이트(추가)
                                                Toast.makeText(LotteryActivity.this, "응모에 참여해 주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else {
                                                Toast.makeText(LotteryActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                            String eventCode = alEventCode.get(position);
                            if(eventCode.equals(userDTO.getEventCode())){
                                count= alJoinInUser.get(position);
                                count++;
                            }
                            String nodeKey = alDataKey.get(position);
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put(nodeKey + "/joinInUser", count);
                            Log.d("Response change", dataSnapshot.getKey());

                            DatabaseReference myRef1 = firedatabase.getReference("CURRENT_EVENT");
                            myRef1.updateChildren(taskMap);
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
        });//응모 버튼


    }//onFunctionSetting

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
                    userUUID  = result.getUUID(); //관리자 권한을 체크하기 위해 UUID불러옴
                    userName  = result.getNickname();

                    edit_name.setText(userName);
                    edit_nickname.setText(userName);
                    try {
                        userEmail = result.getEmail();
                        edit_email.setText(userEmail);//이메일 동의했으면 이메일정보 가져옴
                    }catch (NullPointerException e) {
                        e.printStackTrace();
                    }


                }//onSuccess
            });
        }else { //카톡 로그인이 안되어있다면
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("안내") // 제목부분 텍스트
                    .setMessage("이벤트 참여는 카카오톡 로그인을 해야합니다") // 내용부분 텍스트
                    .setPositiveButton("확인", //종료버튼을 누르면 앱 종료
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick( DialogInterface dialog, int which )
                                {
                                    startActivity(new Intent(LotteryActivity.this, MainActivity.class));
                                    finish(); //종료
                                }
                            }
                    ).show();
        }
    }//카카오 로그인 체크

    void loadData () {
        DatabaseReference myRef = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RoomDTO roomDTO = dataSnapshot.getValue(RoomDTO.class);
                try {
                    if(roomDTO.getEndTime() == null) {
                        String title = roomDTO.getTitle();
                        String sub = roomDTO.getSub();
                        String eventCode = roomDTO.getEventCode();
                        String managerUID = roomDTO.getManagerUID();
                        int joinInUser = roomDTO.getJoinInUser();
                        String pw = roomDTO.getPw();

                        alPw.add(pw);
                        alTitle.add(title);
                        alsub.add(sub);
                        alEventCode.add(eventCode);
                        alManagerUid.add(managerUID); //저장된 모든 방의 타이틀,내용,이벤트코드,UID불러옴
                        alJoinInUser.add(joinInUser);
                        alDataKey.add(dataSnapshot.getKey());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toolBarTitle = alTitle.get(position);
                                toolbar.setTitle(toolBarTitle);
                                substance.setText(alsub.get(position));
                                roomEventCode = alEventCode.get(position); //사용자가 선택한 아이템의 포지션에 따라 데이터 저장시켜줌

                            }
                        }, 500);

                    }
                }catch (NullPointerException e) {
                    e.printStackTrace();

                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RoomDTO roomDTO = dataSnapshot.getValue(RoomDTO.class);
                if(roomDTO.getEndTime() == null) {
                    String title = roomDTO.getTitle();
                    String sub = roomDTO.getSub();
                    String eventCode = roomDTO.getEventCode();
                    String managerUID = roomDTO.getManagerUID();
                    int joinInUser = roomDTO.getJoinInUser();
                    String pw = roomDTO.getPw();

                    alPw.add(pw);
                    alTitle.add(title);
                    alsub.add(sub);
                    alEventCode.add(eventCode);
                    alManagerUid.add(managerUID); //저장된 모든 방의 타이틀,내용,이벤트코드,UID불러옴
                    alJoinInUser.add(joinInUser);
                    alDataKey.add(dataSnapshot.getKey());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toolBarTitle = alTitle.get(position);
                            toolbar.setTitle(toolBarTitle);
                            substance.setText(alsub.get(position));
                            roomEventCode = alEventCode.get(position); //사용자가 선택한 아이템의 포지션에 따라 데이터 저장시켜줌

                        }
                    }, 500);

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

    }//loadData
}
