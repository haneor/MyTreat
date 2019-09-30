package com.ezen.haneor.mytreat.nav_item_activity;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezen.haneor.mytreat.R;
import com.ezen.haneor.mytreat.eventActivity_item.EventActivity;
import com.ezen.haneor.mytreat.eventActivity_item.HLVAdapter;
import com.ezen.haneor.mytreat.eventActivity_item.ListAdapter;
import com.ezen.haneor.mytreat.mDTOClass.RoomDTO;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout mSwipe;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<String> alName = new ArrayList<>();
    private ArrayList<Integer> alJoinInUser = new ArrayList<>();
    private ArrayList<String> alTitle = new ArrayList<>();
    private ArrayList<String> alsub = new ArrayList<>();
    private ArrayList<String> aldate = new ArrayList<>();
    private ArrayList<String> alDataKey = new ArrayList<>();
    private ArrayList<String> alEventCode = new ArrayList<>();
    private ArrayList<String> userEventCode = new ArrayList<>();

    private ImageView add;
    private TextView textNoEvent;

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();

    RoomDTO roomDTO;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.activity_list_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("종료된 이벤트");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textNoEvent = findViewById(R.id.text_list_noevent);
        textNoEvent.setVisibility(View.INVISIBLE);

        mSwipe = findViewById(R.id.swipeRefresh);
        mSwipe.setOnRefreshListener(this);

// 리사이클 뷰 넣기
        recyclerView = findViewById(R.id.activity_list_recyclerView);
        setRecyclerView();

//        TextView textView = findViewById(R.id.text_list_mak);
//        textView.bringToFront();
//        textView.setEnabled(false);
//        textView.setOnClickListener(null);
    }

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

    @Override
    public void onRefresh() {
        mSwipe.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //adapter.init();
                //adapter.notifyDataSetChanged();
                // 서버 통신
                //listView.setAdapter(adapter);
                mSwipe.setRefreshing(false);
            }
        }, 1000);
    }

    // 여기에 recyclerview 호출 메소드 넣기
    private void setRecyclerView () {
        // 내 주변 혹은 메뉴 스피너에서 항목의 리스트를 가져온다.(임시로 넣음)
        DatabaseReference myRef = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                roomDTO = dataSnapshot.getValue(RoomDTO.class);
                try {
                    if(roomDTO.getEndTime() != null) {
                        String manager = roomDTO.getManager().toString();
                        String title = roomDTO.getTitle();
                        String sub = roomDTO.getSub();
                        String date = roomDTO.getStartTime();
                        int joinInUser = roomDTO.getJoinInUser();
                        String eventCode = roomDTO.getEventCode();
                        String dataKey = dataSnapshot.getKey();

                        alName.add(manager);
                        alTitle.add(title);
                        alsub.add(sub);
                        aldate.add(date);
                        alJoinInUser.add(joinInUser);
                        alEventCode.add(eventCode);
                        int imageResource = R.drawable.icon;
                        alDataKey.add(dataKey);

                        mAdapter = new ListAdapter(ListActivity.this, alTitle, imageResource, alName, alsub, aldate, alJoinInUser);
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setClickable(false);
                        recyclerView.setEnabled(false);

                        count++;
                    }
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(count == 0) {
                            textNoEvent.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

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

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }
}
