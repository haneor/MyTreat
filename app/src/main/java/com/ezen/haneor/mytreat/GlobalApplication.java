package com.ezen.haneor.mytreat;


import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                    //로그인 타입 설정(현재 : 모든 방식 지원)
                    // KAKAO_TALK  : 카카오톡 로그인 타입
                    // KAKAO_STORY : 카카오스토리 로그인 타입
                    // KAKAO_ACCOUNT : 웹뷰 다이얼로그를 통한 계정연결 타입
                    // KAKAO_TALK_EXCLUDE_NATIVE_LOGIN : 카카오톡 로그인 타입과 함께 계정생성을 위한 버튼을 함께 제공
                    // KAKAO_LOGIN_ALL : 모든 로그인 방식을 제공
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                } //로그인 웹뷰에서 퍼즈와 리즘시 타이머 설정 - CPU 절약

                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }
                // 일반 사용자가 아닌 Kakao와 제휴 된 앱에서 사용되는 값
                // 값을 지정하지 않을 경우, ApprovalType.INDIVIDUAL 값으로 사용

                @Override
                public boolean isSaveFormData() {
                    return true;
                }// 로그인 웹뷰에서 email 입력 폼의 데이터를 저장할 지 여부를 지정합니다.
            };
        }

        // Application이 가지고 있는 정보를 얻기 위한 인터페이스 입니다.
        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                public Activity getTopActivity() {
                    return null;
                }

                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }
}

