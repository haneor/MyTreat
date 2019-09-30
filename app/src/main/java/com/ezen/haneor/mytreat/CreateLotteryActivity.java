package com.ezen.haneor.mytreat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ezen.haneor.mytreat.eventActivity_item.HLVAdapter;
import com.ezen.haneor.mytreat.mDTOClass.RoomDTO;
import com.ezen.haneor.mytreat.mDTOClass.UserDTO;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CreateLotteryActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editTitle;
    private EditText editSub;
    private EditText editPass;
    private Button startButton, stopButton, changeButton;
    private TextView pass;
    private Switch switComment;

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB

    RoomDTO roomDTO;
    String userName, userUUID;

    ArrayList<String> userData = new ArrayList<>();
    public static boolean manager = false;

    private ArrayList<String> alTitle = new ArrayList<>();
    private ArrayList<String> alsub = new ArrayList<>();
    private ArrayList<String> alPW = new ArrayList<>();
    private ArrayList<String> alDataKey = new ArrayList<>();
    private ArrayList<Integer> alJoinInUser = new ArrayList<>();
    private ArrayList<String> alEventCode = new ArrayList<>();

    private ArrayList<String> alName = new ArrayList<>();
    private ArrayList<String> alEmail = new ArrayList<>();
    private ArrayList<String> alPhoneNumber = new ArrayList<>();

    private ArrayList<String> winUser = new ArrayList<>();

    EditText editWin;
    WinUserDialog winUserDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lottery);

        Toolbar toolbar = findViewById(R.id.activity_create_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("이벤트방 만들기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.activity_creat_imageview);
        editTitle = findViewById(R.id.activity_creat_editTitle);
        editSub = findViewById(R.id.activity_creat_sub);
        editPass = findViewById(R.id.activity_creat_pass);
        startButton = findViewById(R.id.activity_creat_start);
        switComment = findViewById(R.id.activity_creat_switch2);
        pass = findViewById(R.id.activity_creat_textPass);
        stopButton = findViewById(R.id.button_creat_stop);
        changeButton = findViewById(R.id.button_creat_change);
        editWin = findViewById(R.id.editText_creat_win);
        checkKakaoSignUp(); //카카오 로그인 체크
        onFunctionSetting();
        editWin.setVisibility(View.INVISIBLE);
        winUserDialog = new WinUserDialog(CreateLotteryActivity.this);

        if(manager) { //관리자가 다시 방을 터치해서 넘어온다면
            loadData();
            loadUserData ();
            startButton.setVisibility(View.INVISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            changeButton.setVisibility(View.VISIBLE);
            editWin.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editTitle.setText(alTitle.get(LotteryActivity.position));
                    editPass.setText(alPW.get(LotteryActivity.position));
                    editSub.setText(alsub.get(LotteryActivity.position));
                }
            }, 500);

            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(CreateLotteryActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("안내") // 제목부분 텍스트
                            .setMessage("이벤트를 중단하면 다시 시작할 수 없습니다") // 내용부분 텍스트
                            .setPositiveButton("종료", //종료버튼을 누르면 이벤트에 종료시간 추가
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick( DialogInterface dialog, int which ) {
                                            if(alJoinInUser.get(LotteryActivity.position) <= Integer.parseInt(editWin.getText().toString())){
                                                Toast.makeText(CreateLotteryActivity.this, "참여인원보다 당첨인원이 많습니다", Toast.LENGTH_SHORT).show();
                                            }else {

                                                Random random = new Random();
                                                int max1[] = new int[Integer.parseInt(editWin.getText().toString())];
                                                int maxRandom;
                                                for (int i = 0; i < Integer.parseInt(editWin.getText().toString()); i++) {
                                                    maxRandom = random.nextInt(alName.size());
                                                    max1[i] = maxRandom;//랜덤숫자 저장

                                                    for (int j = 0; j < i; j++) {
                                                        if (max1[i] == max1[j]) {
                                                            i--;
                                                            break;
                                                        }
                                                    }//값 중복체크

                                                }//값 저장

                                                final String user[] = new String[Integer.parseInt(editWin.getText().toString())];
                                                String starEmail = "";
                                                int a = 0;
                                                for (int i = 0; i < max1.length; i++) {
                                                    String email = alEmail.get(max1[i]);
                                                    user[i] = "닉네임 : " + alName.get(max1[i]) + " / 이메일(앞4자) : " + email.substring(0, 4);
//                                                    WinUserDialog.winUser.add("이름 : " + alName.get(max1[i]) +" 이메일 : "+ alEmail.get(max1[i]) +"\n 전화번호 : "+ alPhoneNumber.get(max1[i]));
                                                    WinUserDialog.winUser.add("이름 : " + alName.get(max1[i]) +" 이메일 : "+ alEmail.get(max1[i]));
                                                    //Log.d("Response user", user[i]);
                                                } //String에 값을 주고

                                            ArrayAdapter adapter = new ArrayAdapter(CreateLotteryActivity.this, android.R.layout.simple_list_item_1, user);
                                            winUserDialog.listView.setAdapter(adapter);

                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put(alDataKey.get(LotteryActivity.position)+"/endTime", time());
                                            myRef.updateChildren(taskMap);

                                            winUserDialog.show();
                                            }
                                        }
                                    }
                            ).setNegativeButton("취소", null)
                            .show();
                }
            }); //정지버튼

            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put(alDataKey.get(LotteryActivity.position)+"/title", editTitle.getText().toString());
                    taskMap.put(alDataKey.get(LotteryActivity.position)+"/sub", editSub.getText().toString());
                    taskMap.put(alDataKey.get(LotteryActivity.position)+"/pw", editPass.getText().toString());

                    myRef.updateChildren(taskMap);

                    finish();
                }
            }); //수정버튼

        }//관리자가 방을 들어온다면

    }//onCreate
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

                }//onSuccess

                @Override
                public void onNotSignedUp() {
                    Log.e("onNotSignedUp", "onNotSignedUp");
                }
            });
        }
    }//카카오톡 로그인 체크

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

    private void onFunctionSetting() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 클릭 시 갤러리로 전환 하여 그림 선택 후 사진 처리.
            }
        });

        switComment.setChecked(true);
        switComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    editSub.setEnabled(true);
                    editSub.setBackgroundResource(R.drawable.chatbox);
                }else {
                    editSub.setEnabled(false);
                    editSub.setBackgroundColor(Color.GRAY);
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                if(switComment.isChecked()) { //코멘트가 활성화 상태이면
                    if (editTitle.length() == 0 || editPass.length() == 0 || editSub.length() == 0) { //빈칸 체크
                        Toast.makeText(CreateLotteryActivity.this, "빈칸이 있으면 안됩니다", Toast.LENGTH_SHORT).show();
                    }else {
                        dataSave();
                    }
                } else { //코멘트가 활성화 상태가 아니면
                    if (editTitle.length() == 0 || editPass.length() == 0) { //빈칸 체크
                        Toast.makeText(CreateLotteryActivity.this, "빈칸이 있으면 안됩니다", Toast.LENGTH_SHORT).show();
                    }else {
                        dataSave();
                    }
                }
            }
        });//추첨시작
    }//onFunctionSetting

    public void dataSave() { //데이터 저장
        myRef = firedatabase.getReference("USER");//RealtimeDB
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                try {
                    if (userUUID.equals(userDTO.getUserUUID())) {
                       if(userDTO.getManager() == null){
                           Toast.makeText(CreateLotteryActivity.this, "관리자 권한이 없습니다", Toast.LENGTH_SHORT).show();
                       }else {
                           String title = editTitle.getText().toString();
                           String substance = editSub.getText().toString();
                           String password = editPass.getText().toString();

                           myRef = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB

                           if(substance.length() == 0 ) { //코멘트추가 안하면
                               roomDTO = new RoomDTO(title, password, "내용이 없습니다", userName, userUUID, userName+time(), 0, time());
                               //타이틀, 비번, 내용, 관리자명, 관리자UID, 이벤트코드, 참여인원, 시간
                               myRef.push().setValue(roomDTO);
                           }else { //코멘트 추가 하면
                               roomDTO = new RoomDTO(title, password, substance, userName, userUUID, userName+time(), 0, time()); //순서대로 타이틀, pw, 내용이 들어감(순서 맞게)
                               //타이틀, 비번, 내용, 관리자명, 관리자UID, 이벤트코드, 참여인원, 시간
                               myRef.push().setValue(roomDTO);
                           }

                           Toast.makeText(CreateLotteryActivity.this, "추첨을 시작 합니다.", Toast.LENGTH_SHORT).show();
                           finish();
                       }
                    }
                } catch (NullPointerException e) {
                    Log.d("Response", "이벤트생성 Null");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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

        Toast.makeText(CreateLotteryActivity.this, "추첨을 시작 합니다.", Toast.LENGTH_SHORT).show();
//                finish();
    }//dataSave

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
                }
            });

        }else { //카톡 로그인이 안되어있다면
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
                                    startActivity(new Intent(CreateLotteryActivity.this, MainActivity.class));
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
                        String pw = roomDTO.getPw();
                        String dataKey = dataSnapshot.getKey();
                        int joinInUser = roomDTO.getJoinInUser();
                        String eventCode = roomDTO.getEventCode();

                        alTitle.add(title);
                        alsub.add(sub);
                        alPW.add(pw);
                        alDataKey.add(dataKey);
                        alJoinInUser.add(joinInUser);
                        alEventCode.add(eventCode);
                    }
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    public String time() {
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        return formatDate;
    }//time

    void loadUserData () {
        DatabaseReference myRef = firedatabase.getReference("USER");//RealtimeDB
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                try {
                    String name = userDTO.getUserName();
                    String email = userDTO.getUserEmail();
                    String phoneNumber = userDTO.getUserPhoneNumber();
                    String eventCode = userDTO.getEventCode();
                    if(alEventCode.get(LotteryActivity.position).equals(eventCode)) {
                        alName.add(name);
                        alEmail.add(email);
                        alPhoneNumber.add(phoneNumber);
                    }
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    }//loadUserData

    public void requestSendMemo(ArrayList<String> winUser) {
        String message = "이벤트 당첨자 정보 알림 \n 상품 전달 후 메시지 삭제바랍니다!";
        KakaoTalkMessageBuilder builder = new KakaoTalkMessageBuilder();
        builder.addParam("MESSAGE", message);

        for(int i=0; i<winUser.size(); i++) {
            builder.addParam("DATA", winUser.get(i));
        }

        KakaoTalkService.getInstance().requestSendMemo(new KakaoTalkResponseCallback<Boolean>() {
                                                           @Override
                                                           public void onSuccess(Boolean result) {
                                                               Logger.d("send message to my chatroom : " + result);
                                                           }
                                                       }
                , "12332" // templateId
                , builder.build());
    }

    public class KakaoTalkMessageBuilder {
        public Map<String, String> messageParams = new HashMap<String, String>();

        public KakaoTalkMessageBuilder addParam(String key, String value) {
            messageParams.put("${" + key + "}", value);
            return this;
        }

        public Map<String, String> build() {
            return messageParams;
        }
    }

    public abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {

        @Override
        public void onNotKakaoTalkUser() {
            //KakaoToast.makeToast(self, "not a KakaoTalk user", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "not a KakaoTalk user", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            //KakaoToast.makeToast(self, "failure : " + errorResult, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "failure : " + errorResult, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
            Toast.makeText(getApplicationContext(), "세션이 닫혔습니다", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
            Toast.makeText(getApplicationContext(), "Signup 필요합니다", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDidStart() {
            //showWaitingDialog();
            Toast.makeText(getApplicationContext(), "onDidStart_showWaitingDialog()", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDidEnd() {
            //cancelWaitingDialog();
            Toast.makeText(getApplicationContext(), "onDidEnd_onDidEnd()", Toast.LENGTH_SHORT).show();
        }
    }
}
