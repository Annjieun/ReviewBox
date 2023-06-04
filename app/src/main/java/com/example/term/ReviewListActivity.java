package com.example.term;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ReviewListActivity extends AppCompatActivity {
    private ArrayList<String> rdata = new ArrayList<String>();
    private ListView rlist;
    private ArrayAdapter<String> adapter;
    private int id;
    private DatabaseHelper helper;
    private ArrayList<ReviewData> reviewDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviewlist_layout);
        Intent idIntent = getIntent();
        id = idIntent.getIntExtra("ID", 0);

        rlist = (ListView) findViewById(R.id.reviewList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rdata);
        rlist.setAdapter(adapter);

        helper = new DatabaseHelper(this);

        Button rvbtn = (Button) findViewById(R.id.rv_btn);
        rvbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ReviewListActivity.this, ReviewActivity.class);
                intent.putExtra("ID", id);
                startActivityResult.launch(intent);
            }
        });
        ImageButton rvbbtn = (ImageButton) findViewById(R.id.rvback_btn);
        rvbbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDisable();
            }
        });
        showReviews();
    }


    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        showReviews();
                    }
                }
            });

    private void showReviews() {
        reviewDatas = helper.getReviewDatasById(id);
        rdata.clear();
        for (int i = 0; i < reviewDatas.size(); i++) {
            //Intent intent = result.getData();
            String msg = reviewDatas.get(i).getReview();
            Float num = reviewDatas.get(i).getRating();

            rdata.add("★ " + num + " " + " :  " + msg);
        }
        adapter.notifyDataSetChanged();
    }

    public float getItemRating() {
        if (reviewDatas == null) reviewDatas = helper.getReviewDatasById(id);

        float totalRating = 0.0f;

        for (int i = 0; i < reviewDatas.size(); i++) {
            totalRating += reviewDatas.get(i).getRating();
        }
        if (reviewDatas.size() > 0) return totalRating / reviewDatas.size();
        else return 0.0f;
    }

    // 해당 액티비티에서 뒤로가기 했을 경우
    private void onDisable(){
        Intent intent = new Intent();
        intent.putExtra("rating", getItemRating());
        setResult(RESULT_OK, intent);
        finish();   //현재 액티비티 종료
    }
}

