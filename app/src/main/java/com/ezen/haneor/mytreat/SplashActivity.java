package com.ezen.haneor.mytreat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ezen.haneor.mytreat.mDTOClass.UserDTO;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class SplashActivity extends AppCompatActivity {


    private RelativeLayout relativeLayout;
    private String splash_message;
    private ImageView imageView;
    public static Boolean KEEPLOGIN = false; // 재 로그인 처리를 위한 변수 선언

    SessionCallback callback;//콜백클래스

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firedatabase.getReference("USER");//RealtimeDB

    UserDTO userDTO;
    int count;

    LoginButton kakaoButton;
    ImageView kakaotalk;

    public SharedPreferences shortcutSharedPref;
    public boolean isInstalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        relativeLayout = findViewById(R.id.splashactivity_relativeLayout);
        imageView = findViewById(R.id.splashactivity_image);
        kakaoButton = findViewById(R.id.com_kakao_login);
        kakaotalk = findViewById(R.id.kakaotalk);
        kakaotalk.bringToFront();//카톡이미지 앞으로(기존 로그인버튼을 덮음)

        if (splash_message != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.create().show();
        } else {
            selectImage();
        }

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen(); //카카오톡 로그인 관련


         if (Session.getCurrentSession().isOpened()) {
             startActivity(new Intent(SplashActivity.this, MainActivity.class));
             finish();
         } else {
             Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
         }  //카카오톡 로그인상태 체크(사용할수도있으니 삭제 NOPE!)


        shortcutSharedPref = getSharedPreferences("what", MODE_PRIVATE);
        isInstalled = shortcutSharedPref.getBoolean("isInstalled", false);
        Log.e("installed: ", String.valueOf(isInstalled));

        if (!isInstalled) {
            addAppIconToHomeScreen(this);
        }

        getHashKey();
        getAppKeyHash();

//        byte[] sha1 = {
//                0x3F, 0x4C, (byte) 0xBE, 0x71, (byte)0xF7, (byte)0xEA, 0x0B, 0x77, (byte)0x80, 0x4D, (byte)0xE7, (byte)0xC3, (byte)0x94, (byte)0xA1, 0x15, 0x5C, 0x4A, 0x24, (byte)0x81, 0x2E
//        };
//        Log.e("keyhash", Base64.encodeToString(sha1, Base64.NO_WRAP));
    }//onCreate

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    private void addAppIconToHomeScreen(Context context) {
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClassName(context, getClass().getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));

        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        sendBroadcast(intent);

        SharedPreferences.Editor editor = shortcutSharedPref.edit();
        editor.putBoolean("isInstalled", true);
        editor.commit();
    }

    private void selectImage() {
        kakaotalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoButton.performClick();
                //여기에 startActivity하면 performClick호출 안댐!
            }
        });

        findViewById(R.id.imageButton_non).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태
            //일반적으로 로그인 후의 다음 activity로 이동한다
            if (Session.getCurrentSession().isOpened()) { // 한 번더 세션을 체크
                requestMe();
            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }//SessionCallback


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private void requestMe() {

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
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }//onSuccess

            @Override
            public void onNotSignedUp() {
                Log.e("onNotSignedUp", "onNotSignedUp");
            }
        });
    }//rquestme(Kakao)



}