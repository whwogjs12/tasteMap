package com.example.tastemap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Score extends AppCompatActivity implements View.OnClickListener {

    int position;
    String name;
    String memoString;
    int rate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("pos");
//        Log.d("TEST", "onCreate: 포지션 확인 "+position); // 제대로 동작함
        name = intent.getExtras().getString("name");
        rate = intent.getExtras().getInt("rate");
        memoString = intent.getExtras().getString("memo");

        TextView tv = (TextView) findViewById(R.id.score_name);
        tv.setText(name);

        //텍스트 가져오기
        EditText memo = (EditText) findViewById(R.id.memo);
        if(!memoString.equals("null")){
            String tmp = memo.getText().toString();
            memo.setText(memoString);
            Log.d("memoString",memoString);
            //이어쓰기
            memoString = memoString +"\n"+ tmp ;
        } else {
            String memoString = memo.getText().toString();
        }



        RatingBar rb = (RatingBar)findViewById(R.id.score_ratingBar);
        Log.d("테스트", "onCreate: rate " +rate);
        if(rate != 0){
            rb.setRating(rate);
        }
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //ratingBar 에 채워진 별 개수 반환
                float ratingValue = ratingBar.getRating();
                Log.d("TEST", "onRatingChanged: 확인용 "+ratingValue); //입력한 별 만큼 숫자로 반환됨

                rate = (int)ratingValue;
                Log.d("Test", "onRatingChanged: 별점 " +ratingValue); //입력한 대로 나옴
            }
        });



        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.saveButton:
                //텍스트 가져오기
                EditText memo = findViewById(R.id.memo);
                String memoString = memo.getText().toString();
                Log.d("TEST", "onCreate: 메모 " + memoString);
                //어플 내에서는 확인 X
                pullData(rate, memoString, position);
                break;
        }
    }

    public void pullData(int rate, String memoString, int position){
        Intent intent;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("rate", rate);
        intent.putExtra("memo", memoString);
        Log.d("TEST", "pullData: intent 완료");
        setResult(RESULT_OK, intent);
        Log.d("TEST", "pullData: 확인용 "+rate); //입력한 대로 나옴
        finish();
    }

}