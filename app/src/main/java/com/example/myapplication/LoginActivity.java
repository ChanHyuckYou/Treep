package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.LoginData;
import com.example.myapplication.data.LoginResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;


import java.util.Objects;

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


    private ImageButton mkakao_authbtn;

    private final String context = "LoginActivity";





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
        mkakao_authbtn = (ImageButton) findViewById(R.id.kakao_authbtn);


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

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                updateKakaoLoginUi();
                return null;
            }
        };


        mkakao_authbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if( UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                }else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
            }
        });




    }





    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mAuth = FirebaseAuth.getInstance();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Password: " + password);

        boolean cancel = false;
        View focusView = null;
        //어드민 로그인시 유효성검사 안함
        if ("admin".equals(email) && "12345678".equals(password)) {
            showProgress(false);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Toast.makeText(LoginActivity.this, "admin계정으로 접속합니다.",Toast.LENGTH_SHORT).show();
            startActivity(intent);
            return;
        }

        // 패스워드의 유효성 검사
        if (password.isEmpty()) {
            mPasswordView.setError("비밀번호를 입력해주세요.");
            focusView = mPasswordView;
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
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse result = response.body();
                    // Toast 메시지 ( 로그인 성공. )
                    assert result != null;
                    int resultCode = result.getCode();
                    String message = result.getMessage();

                    if (resultCode == 200) {
                        // 로그인 성공
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        showProgress(false);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        // 로그인 실패 또는 다른 상태 코드
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                } else {
                    // HTTP 응답이 실패한 경우
                    Log.e(TAG, "HTTP 응답 실패: " + response.code());
                    showProgress(false);
                }
            }


            //로그인 실패 시 기능
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // 토스트 메시지
                Toast.makeText(LoginActivity.this, "로그인 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("로그인 에러 발생", Objects.requireNonNull(t.getMessage()));
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

    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null ){
                    Log.i(TAG, "invoke: id=" + user.getId());
                    assert user.getKakaoAccount() != null;
                    Log.i(TAG, "invoke: email=" + user.getKakaoAccount().getEmail());
                    Log.i(TAG, "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());
                    Log.i(TAG, "invoke: gender=" + user.getKakaoAccount().getGender());
                    Log.i(TAG, "invoke: age=" + user.getKakaoAccount().getAgeRange());


                    //Glide.with(binding.profile).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(binding.profile);


                } else {
                    Log.d(TAG, "Login Fallid");
                }
                return null;
            }
        });
    }



}
