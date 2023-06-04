package com.example.term;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {
    private InputMethodManager imm;
    private TextView rate_txt;
    private RatingBar ratingBar;
    private String str;
    private EditText edit;
    private Float star;
    private DatabaseHelper helper;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_layout);
        Intent idIntent = getIntent();
        id = idIntent.getIntExtra("ID", 0);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        star = 0.0f;

        ratingBar = findViewById(R.id.ratingBar);
        rate_txt = findViewById(R.id.rate_txt);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                Toast.makeText(getApplicationContext(), "New Rating: " + rating, Toast.LENGTH_SHORT).show();
                str = Float.toString(rating);
                star = rating;
                rate_txt.setText(str);
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button stbtn = (Button) findViewById(R.id.store_btn);
        edit = (EditText) findViewById(R.id.edittext);

        helper = new DatabaseHelper(this);

        stbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                helper.setReview(id, edit.getText().toString(), star);
                finish();
            }
        });

    }

    //화면 터치하면 키보드 내리기
    public final void hideKeyboard(View view) {
        InputMethodManager var = this.imm;
        if (var != null) {
            var.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
