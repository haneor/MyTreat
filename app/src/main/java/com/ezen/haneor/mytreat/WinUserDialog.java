package com.ezen.haneor.mytreat;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ezen.haneor.mytreat.mDTOClass.RoomDTO;
import com.ezen.haneor.mytreat.mDTOClass.UserDTO;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Handler;

import static android.content.Context.CLIPBOARD_SERVICE;

public class WinUserDialog extends Dialog {

    public static int winUserCount;
    public static String dialogEventCode;

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB

    public ListView listView;

    public static ArrayList<String> winUser = new ArrayList<>();

    Button buttonMe, buttonOk, buttonCC;

    public WinUserDialog(@NonNull final Context context) {
        super(context);
        setContentView(R.layout.winuser_dialog);

        listView =findViewById(R.id.list_winuser);
        buttonMe = findViewById(R.id.button_winuser_link);
        buttonOk = findViewById(R.id.button_winuser_ok);
        buttonCC = findViewById(R.id.button_winuser_cc);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(getContext(), MainActivity.class));
            }
        });//확인

        buttonMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSendMemo();
            }
        });//나에게 보내기

        buttonCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data ="";
                for(int i=0; i<winUser.size(); i++) {
                    data += winUser.get(i)+"\n";
                }
                ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", data);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, "복사되었습니다, 확인 후 삭제해주세요!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void requestSendMemo() {
        String message = "이벤트 당첨자 정보 알림 \n 상품 전달 후 메시지 삭제바랍니다!";
        KakaoTalkMessageBuilder builder = new KakaoTalkMessageBuilder();
        builder.addParam("MESSAGE", message);

        for(int i=0; i<winUser.size(); i++) {
            builder.addParam("DATA", winUser.get(i));
        }

        KakaoTalkService.getInstance().requestSendMemo(new TalkResponseCallback<Boolean>() {
                                                           @Override
                                                           public void onSessionClosed(ErrorResult errorResult) {
                                                           }
                                                           @Override
                                                           public void onNotSignedUp() {
                                                           }
                                                           @Override
                                                           public void onNotKakaoTalkUser() {
                                                           }
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


}
