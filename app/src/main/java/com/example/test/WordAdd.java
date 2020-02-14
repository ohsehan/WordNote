package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class WordAdd extends AppCompatActivity {

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_add);

        //아이디 정의
        Button add_savebtn = (Button) findViewById(R.id.add_savebtn);
        final EditText add_english = (EditText) findViewById(R.id.add_english);
        final EditText add_korean = (EditText) findViewById(R.id.add_korean);

        //온클릭리스너
        add_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //edittext에 저장된 텍스트 Strig에 저장
                String get_english = add_english.getText().toString();
                String get_korean = add_korean.getText().toString();

                //hashmap 만들기
                HashMap result = new HashMap<>();
                result.put("english", get_english);
                result.put("korean", get_korean);

                //firebase 정의
                mDatabase = FirebaseDatabase.getInstance().getReference();
                //firebase에 저장
                mDatabase.child("article").push().setValue(result);

                add_english.setText("");
                add_korean.setText("");
                Toast.makeText(WordAdd.this, get_english + "단어 저장",Toast.LENGTH_SHORT).show();
            }
        });
    }

}