package com.example.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.FontsContract;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.Auth.KaKaoAuthViewModel;
import com.example.myapplication.data.LoginData;
import com.example.myapplication.data.LoginResponse;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;


import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailLoginButton;
    private Button mJoinButton;
   // private Button mNaverButton;
    private ProgressBar mProgressView;
    private ServiceApi service;
    private FirebaseAuth mAuth;
    private static String OAUTH_CLIENT_ID = "5PUJtf4KppIyLiTTS5cE";
    private static String OAUTH_CLIENT_SECRET = "9tTPsNdBRR";
    private static String OAUTH_CLIENT_NAME = "test";


    private Button mkakao_authbtn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //updateKakaoLoginUi();

        mAuth = FirebaseAuth.getInstance();
       // mNaverButton = findViewById(R.id.buttonOAuthLoginImg);


        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        mEmailLoginButton = (Button) findViewById(R.id.login_button);
        mJoinButton = (Button) findViewById(R.id.join_button);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);


        service = RetrofitClient.getClient().create(ServiceApi.class);


        mEmailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();



            }
        });

        //회원가입 버튼 눌렀을 시, JoinActivity로 연결
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

//        mkakao_authbtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
//                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
//                        @Override
//                        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
//                            if (oAuthToken != null) {
//
//                            }
//                            if (throwable != null) {
//
//                            }
//                            //updateKakaoLoginUi();
//                            return null;
//                        }
//                    });
//                } else {
//                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
//                        @Override
//                        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
//                            if (oAuthToken != null) {
//
//                            }
//                            if (throwable != null) {
//
//                            }
//                            //updateKakaoLoginUi();
//                            return null;
//                        }
//                    });
//                }
//            }
//        });


    }





    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mAuth = FirebaseAuth.getInstance();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                }
            }
        });

        boolean cancel = false;
        View focusView = null;

        // 패스워드의 유효성 검사
        if (password.isEmpty()) {
            mEmailView.setError("비밀번호를 입력해주세요.");
            focusView = mEmailView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("6자 이상의 비밀번호를 입력해주세요.");
            focusView = mPasswordView;
            cancel = true;
        }

        // 이메일의 유효성 검사
        if (email.isEmpty()) {
            mEmailView.setError("이메일을 입력해주세요.");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("@를 포함한 유효한 이메일을 입력해주세요.");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startLogin(new LoginData(email, password));
            showProgress(true);
        }
    }


    private void startLogin(LoginData data) {
        service.userLogin(data).enqueue(new Callback<LoginResponse>() {
            @Override

            //로그인 성공 시 기능
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse result = response.body();
                //Toast 메시지 ( 로그인 성공. )
                Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
                //로그인 시 화면 전환
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }


            //로그인 실패 시 기능
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 토스트 메시지
                Toast.makeText(LoginActivity.this, "로그인 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("로그인 에러 발생", t.getMessage());
                showProgress(false);

            }
        });
    }

    //이메일에 '@' 붙였을 때,
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    //패스워드 6글자 이상 제한
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

//    private void updateKakaoLoginUi() {
//        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
//            @Override
//            public Unit invoke(User user, Throwable throwable) {
//                if (user != null ){
//                    Log.i(TAG, "invoke: id=" + user.getId());
//                    assert user.getKakaoAccount() != null;
//                    Log.i(TAG, "invoke: email=" + user.getKakaoAccount().getEmail());
//                    Log.i(TAG, "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());
//                    Log.i(TAG, "invoke: gender=" + user.getKakaoAccount().getGender());
//                    Log.i(TAG, "invoke: age=" + user.getKakaoAccount().getAgeRange());
//
//
//                    //Glide.with(binding.profile).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(binding.profile);
//
//
//                }
//                return null;
//            }
//        });
//    }



}
