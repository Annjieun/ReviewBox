package com.example.term;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {
    private ImageView floorOneImage;
    private ImageView floorTwoImage;
    private Button floorToggleButton;
    private boolean isFloorOne;

    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        floorOneImage = (ImageView) findViewById(R.id.floorOneImage);
        floorTwoImage = (ImageView) findViewById(R.id.floorTwoImage);
        floorToggleButton = (Button) findViewById(R.id.floorToggleButton);
        floorOneImage.setVisibility(View.VISIBLE);
        floorTwoImage.setVisibility(View.INVISIBLE);
        isFloorOne = true;

        floorToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFloorOne){
                    floorOneImage.setVisibility(View.INVISIBLE);
                    floorTwoImage.setVisibility(View.VISIBLE);
                    isFloorOne = false;
                }
                else{
                    floorOneImage.setVisibility(View.VISIBLE);
                    floorTwoImage.setVisibility(View.INVISIBLE);
                    isFloorOne = true;
                }

            }
        });

        backButton = (ImageButton) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backActivity();
            }
        });
    }

    public void backActivity() {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        startActivity(intent);
    }
}
