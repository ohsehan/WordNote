package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class StartActivity extends AppCompatActivity {

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        Button wordLook = (Button) findViewById(R.id.word_look);
        wordLook.setOnClickListener(new View.OnClickListener() {//단어 보기 액티비티 실행
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,WordLook.class);
                startActivity(intent);
            }
        });
        Button wordAdd = (Button) findViewById(R.id.word_add);
        wordAdd.setOnClickListener(new View.OnClickListener() {//단어 추가 액티비티 실행
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,WordAdd.class);
                startActivity(intent);
            }
        });
    }
}
