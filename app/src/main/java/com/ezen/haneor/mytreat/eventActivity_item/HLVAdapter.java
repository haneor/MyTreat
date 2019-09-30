package com.ezen.haneor.mytreat.eventActivity_item;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.ezen.haneor.mytreat.MainActivity;
import com.ezen.haneor.mytreat.R;
import com.ezen.haneor.mytreat.mDTOClass.RoomDTO;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HLVAdapter extends RecyclerView.Adapter<HLVAdapter.ViewHolder> {

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

    public HLVAdapter(Context context, ArrayList<String> alTitle, int imageResource, ArrayList<String> alName, ArrayList<String> alsub, ArrayList<String> aldate, ArrayList<Integer>alJoinInUser  ) {
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

    public HLVAdapter(Context context, ArrayList<String> alName, ArrayList<Integer> alImage) {
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
        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(view.getContext(), "설정 변경??"+alName.get(position)+" longClick!!!", Toast.LENGTH_SHORT).show();
                } else {
                    LotteryActivity.position = position;
//                    context.startActivity(new Intent(context, LotteryActivity.class)); // 추첨 관련 화면 전환
                    try {
                        Log.d("Response Manager", alManagerUid.get(position));
                        if (userUUID.equals(alManagerUid.get(position))) {
                            //관리자가 자신의 이벤트를 클릭한다면 ⇒ 선택된 이벤트의 내용을 가져오고
                            CreateLotteryActivity.manager = true;
                            context.startActivity(new Intent(context, CreateLotteryActivity.class));

                        } else {
                            //관리자가 아닌 사람이 클릭한다면
                            context.startActivity(new Intent(context, LotteryActivity.class));
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        });
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
            clickListener.onClick(view, getPosition(), false);
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
