package com.ezen.haneor.mytreat;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PaperItemDialog extends Dialog {


    EditText editUser; //유저가 직접 추가할 내용
    ListView listpaper;

    public PaperItemDialog(@NonNull final Context context) {
        super(context);
        setContentView(R.layout.paper_dialog_item);

        listpaper = findViewById(R.id.list_paper_item); //추가한 항목을 나타내줄 리스트뷰

        TextView textView = (TextView) findViewById(R.id.text_paper_item);
        editUser = findViewById(R.id.edit_paper_user);

        Button buttonWin = findViewById(R.id.button_paper_win);
        buttonWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overSize(context, "당첨");
            }
        });//"당첨"을 자동으로 추가

        Button buttonLose = findViewById(R.id.button_paper_lose);
        buttonLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overSize(context, "꽝");
            }
        });//"꽝"을 자동으로 추가

        Button buttonUser = findViewById(R.id.button_paper_user);
        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editUser.length() == 0) {//아무 내용을 입력하지 않고 추가시
                    Toast.makeText(context, "최소 1자이상 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                String str = editUser.getText().toString();
                overSize(context, str);
            }
        });//입력한 내용을 추가

        Button buttonOk = findViewById(R.id.button_paper_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });//완료버튼
    }//con

    void overSize(Context context, String str) {
        if(PaperLotActivity.itemList.size() == 12) {
            Toast.makeText(context, "아이템은 12개를 넘길수 없습니다", Toast.LENGTH_SHORT).show();
        }else {
            PaperLotActivity.itemList.add(str);
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, PaperLotActivity.itemList);
        listpaper.setAdapter(adapter) ;
    }//overSize, 아이템 갯수가 12개가 넘었는지 확인

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return false;
    }
}
