package com.example.myapplication;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LocalInfoActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        TextView contentTextView = findViewById(R.id.content);
        TextView viewMoreTextView = findViewById(R.id.view_more);
        TextView viewMoreGoneTextView = findViewById(R.id.view_moregone);
        LinearLayout imageTextContainer = findViewById(R.id.imageTextContainer);


        // 여기서 이미지와 텍스트를 감싸는 LinearLayout를 초기에 감춰둘 수 있습니다.
        imageTextContainer.setVisibility(View.GONE);

        TextView contentTextView2 = findViewById(R.id.content2);
        TextView viewMoreTextView2 = findViewById(R.id.view_more2);
        TextView viewMoreGoneTextView2 = findViewById(R.id.view_moregone2);
        LinearLayout imageTextContainer2 = findViewById(R.id.imageTextContainer2);

        TextView contentTextView3 = findViewById(R.id.content3);
        TextView viewMoreTextView3 = findViewById(R.id.view_more3);
        TextView viewMoreGoneTextView3 = findViewById(R.id.view_moregone3);
        LinearLayout imageTextContainer3 = findViewById(R.id.imageTextContainer3);

        TextView contentTextView4 = findViewById(R.id.content4);
        TextView viewMoreTextView4 = findViewById(R.id.view_more4);
        TextView viewMoreGoneTextView4 = findViewById(R.id.view_moregone4);
        LinearLayout imageTextContainer4 = findViewById(R.id.imageTextContainer4);


        setViewMore(contentTextView4, viewMoreTextView4, viewMoreGoneTextView4, imageTextContainer4);


        setViewMore(contentTextView, viewMoreTextView, viewMoreGoneTextView, imageTextContainer);
        setViewMore(contentTextView2, viewMoreTextView2, viewMoreGoneTextView2, imageTextContainer2);
        setViewMore(contentTextView3, viewMoreTextView3, viewMoreGoneTextView3, imageTextContainer3);
    }

    private void setViewMore(TextView contentTextView, TextView viewMoreTextView, TextView viewMoreGoneTextView, LinearLayout imageTextContainer) {
        // contentTextView.post 내에서 null 체크
        if (contentTextView == null || imageTextContainer == null) {
            Log.e("ViewMore", "contentTextView or imageTextContainer is null");
            return;
        }

        viewMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지와 텍스트를 담은 LinearLayout을 보이도록 설정
                imageTextContainer.setVisibility(View.VISIBLE);

                contentTextView.setMaxLines(Integer.MAX_VALUE);
                viewMoreTextView.setVisibility(View.GONE);
                viewMoreGoneTextView.setVisibility(View.VISIBLE);
            }
        });

        viewMoreGoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentTextView.setMaxLines(2);
                viewMoreGoneTextView.setVisibility(View.GONE);
                viewMoreTextView.setVisibility(View.VISIBLE);

                // 이미지와 텍스트를 담은 LinearLayout을 감추도록 설정
                imageTextContainer.setVisibility(View.GONE);
            }
        });
    }
}
