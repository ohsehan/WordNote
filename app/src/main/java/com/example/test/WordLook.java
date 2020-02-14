package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class WordLook extends AppCompatActivity {

    DatabaseReference mDatabase;
    ArrayList<String> word_array_english;
    ArrayAdapter<String> AdapterEnglish;

    ArrayList<String> word_array_korean;
    ArrayAdapter<String> AdapterKorean;

    TextView wordMean;
    Button lookKorean;
    ToggleButton toggleBtn;
    ListView listEnglish;
    ListView listKorean;

    int meanCount = 0;
    int pageNum = 1;
    long count = 0;
    final int wordPerPage = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_look);

        word_array_english = new ArrayList<String>();
        word_array_korean = new ArrayList<String>();

        AdapterEnglish = new ArrayAdapter<String>(WordLook.this,
                android.R.layout.simple_list_item_1, word_array_english);

        AdapterKorean = new ArrayAdapter<String>(WordLook.this,
                android.R.layout.simple_list_item_1, word_array_korean);

        listEnglish = (ListView)findViewById(R.id.word_list_english);
        listKorean = (ListView)findViewById(R.id.word_list_korean);
        registerForContextMenu(listKorean);
        listEnglish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listKorean.getVisibility()==View.INVISIBLE) {//listKorean이 안보이는 상태라면
//                    Toast.makeText(WordLook.this, word_array_korean.get(position + (pageNum - 1) * 10), Toast.LENGTH_SHORT).show();
//                    System.out.println(listKorean.getItemAtPosition(position));
//                    listKorean.setVisibility(View.VISIBLE);
                    wordMean.setText(word_array_korean.get(position + (pageNum - 1) * 10));
                }
            }
        });

        wordMean = (TextView)findViewById(R.id.word_mean);

        lookKorean = (Button)findViewById(R.id.look_korean);
        lookKorean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listKorean.getVisibility()==View.INVISIBLE){//listKorean이 안보이는 상태라면
                    Toast.makeText(WordLook.this,word_array_english.get(meanCount+(pageNum-1)*10), Toast.LENGTH_SHORT).show();
                    meanCount++;
                }
            }
        });

        toggleBtn = (ToggleButton)findViewById(R.id.toggle_btn);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();//처음 상태 꺼진채로 시작

                if(on){
                    listKorean.setVisibility(View.INVISIBLE);//켜져있을 때 감추기
                }
                else{
                    listKorean.setVisibility(View.VISIBLE);//꺼져있을 때 보이기
                    wordMean.setText("뜻 감추기 버튼을 누르고 영단어를 클릭해보세요");
                }
            }
        });

        final TextView page_num = (TextView)findViewById(R.id.page_num);

        Button prevBtn = (Button)findViewById(R.id.prev_btn);
        prevBtn.setOnClickListener(new View.OnClickListener() {//이전 페이지 가기
            @Override
            public void onClick(View v) {
                if(pageNum != 1){
                    wordMean.setText("뜻 감추기 버튼을 누르고 영단어를 클릭해보세요");
                    meanCount = 0;//페이지 이동하면 meanCount 초기화(처음부터 보는 것으로 간주)
                    pageNum--;
                    page_num.setText(pageNum + " / " + (int)Math.ceil(count/(float)wordPerPage));
                    changeListView();
                }
                else
                    Toast.makeText(WordLook.this,"첫 페이지입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        Button nextBtn = (Button)findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {//뒷 페이지 가기
            @Override
            public void onClick(View v) {
                if(Math.ceil(count/(float)wordPerPage) != pageNum){
                    wordMean.setText("뜻 감추기 버튼을 누르고 영단어를 클릭해보세요");
                    meanCount = 0;//페이지 이동하면 meanCount 초기화(처음부터 보는 것으로 간주)
                    pageNum++;
                    page_num.setText(pageNum + " / " + (int)Math.ceil(count/(float)wordPerPage));
                    changeListView();
                }
                else
                    Toast.makeText(WordLook.this,"마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                System.out.println("count : " + count + ", pageNum : " + pageNum + " -> " + Math.ceil(count/wordPerPage));
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdapterEnglish.clear();
                AdapterKorean.clear();

                count = dataSnapshot.child("article").getChildrenCount();
                page_num.setText(pageNum + " / " + (int)Math.ceil(count/(float)wordPerPage));

                for (DataSnapshot snapshot : dataSnapshot.child("article").getChildren()) {
                    if(snapshot.hasChild("english")) {
                        System.out.println(snapshot.child("english").getValue().toString());
                        word_array_english.add(snapshot.child("english").getValue().toString());
                        word_array_korean.add(snapshot.child("korean").getValue().toString());
                    }
                }

                changeListView();

                AdapterEnglish.notifyDataSetChanged();
                AdapterKorean.notifyDataSetChanged();
//                listEnglish.setSelection(10);
//                listKorean.setSelection(AdapterKorean.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FirebaseManager",databaseError.getMessage());
            }
        });
    }

    public void changeListView(){
        System.out.println("count : " + count + ", pageNum : " + pageNum);
        if(Math.ceil(count/(float)wordPerPage) == pageNum){//마지막 페이지이면
            AdapterEnglish = new ArrayAdapter<String>(WordLook.this,
                    android.R.layout.simple_list_item_1, word_array_english.subList((pageNum - 1) * 10, (int)count));

            AdapterKorean = new ArrayAdapter<String>(WordLook.this,
                    android.R.layout.simple_list_item_1, word_array_korean.subList((pageNum - 1) * 10, (int)count));
        }
        else {
            AdapterEnglish = new ArrayAdapter<String>(WordLook.this,
                    android.R.layout.simple_list_item_1, word_array_english.subList((pageNum - 1) * 10, pageNum * 10));

            AdapterKorean = new ArrayAdapter<String>(WordLook.this,
                    android.R.layout.simple_list_item_1, word_array_korean.subList((pageNum - 1) * 10, pageNum * 10));
        }
        listEnglish.setAdapter(AdapterEnglish);
        listKorean.setAdapter(AdapterKorean);

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int index=info.position;
        switch(item.getItemId())
        {
            case R.id.delete:
                final String english = word_array_english.get(index+(pageNum-1)*10);
                final String korean = word_array_korean.get(index+(pageNum-1)*10);
//                Toast.makeText(WordLook.this,english + " " + korean, Toast.LENGTH_SHORT).show();
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.child("article").getChildren()) {
                            if(snapshot.hasChild("english")) {
                                if (snapshot.child("english").getValue().toString() == english) {
                                    snapshot.child("english").getRef().removeValue();
                                    snapshot.child("korean").getRef().removeValue();
                                    System.out.println("실행");
                                    Toast.makeText(WordLook.this, "삭제 : " + english + " " + korean + " ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                return true;
        }
        return super.onContextItemSelected(item);
    }
}
