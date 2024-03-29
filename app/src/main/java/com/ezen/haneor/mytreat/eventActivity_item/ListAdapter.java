package com.ezen.haneor.mytreat.eventActivity_item;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezen.haneor.mytreat.CreateLotteryActivity;
import com.ezen.haneor.mytreat.LotteryActivity;
import com.ezen.haneor.mytreat.R;
import com.ezen.haneor.mytreat.mDTOClass.RoomDTO;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    ArrayList<String> alName;
    ArrayList<Integer> alImage;
    int imageResource;
    ArrayList<String> alTitle;
    ArrayList<String> alsub;
    ArrayList<String> aldate;
    ArrayList<Integer> alJoinInUser;

    Context context;
    public static String userUUID;
    ArrayList<String> alManagerUid = new ArrayList<>();

    FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firedatabase.getReference("USER");//RealtimeDB

    public ListAdapter(Context context, ArrayList<String> alTitle, int imageResource, ArrayList<String> alName, ArrayList<String> alsub, ArrayList<String> aldate, ArrayList<Integer>alJoinInUser  ) {
        super();
        loadData();
        this.context = context;
        this.alTitle = alTitle;
        this.imageResource = imageResource;
        this.alName = alName;
        this.alsub = alsub;
        this.aldate = aldate;
        this.alJoinInUser = alJoinInUser;
    }

    public ListAdapter(Context context, ArrayList<String> alName, ArrayList<Integer> alImage) {
        super();
        this.context = context;
        this.alName = alName;
        this.alImage = alImage;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageResource(imageResource);
        viewHolder.textView_name.setText(alName.get(position));
        viewHolder.textView_title.setText(alTitle.get(position));
        viewHolder.textView_sub.setText(alsub.get(position));
        viewHolder.textView_date.setText("생성시간 : "+ aldate.get(position));
        viewHolder.textView_people.setText(String.valueOf("참여인원 : "+alJoinInUser.get(position)));
        viewHolder.setClickListener(null);
    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imageView;
        public TextView textView_sub; //내용
        public TextView textView_name; //생성자
        public TextView textView_people; //참여인원
        public TextView textView_title, textView_date; //제목, 생성시간

        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.recycler_item_imageview);
            textView_name = (TextView) itemView.findViewById(R.id.recycler_text_name);
            textView_people = (TextView) itemView.findViewById(R.id.recycler_text_people);
            textView_sub = (TextView) itemView.findViewById(R.id.recycler_text_sub);
            textView_title =  (TextView) itemView.findViewById(R.id.recycler_text_title);
            textView_date =  (TextView) itemView.findViewById(R.id.recycler_text_date);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
//            clickListener.onClick(view, getPosition(), false);
            return;
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }

    void loadData () {
        DatabaseReference myRef = firedatabase.getReference("CURRENT_EVENT");//RealtimeDB
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RoomDTO roomDTO = dataSnapshot.getValue(RoomDTO.class);
                try {
                    String managerUID = roomDTO.getManagerUID();

                    alManagerUid.add(managerUID); //저장된 모든 방의 타이틀,내용,이벤트코드,UID불러옴
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
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

    }//loadData
}
