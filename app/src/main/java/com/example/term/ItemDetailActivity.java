package com.example.term;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 100;
    private com.example.term.ItemData itemData;
    private DatabaseHelper helper;

    private TextView nameText;
    private TextView priceText;
    private TextView ratingText;
    private ImageView itemImage;
    private Button reviewBtn;
    private Button mapButton;
    private ImageButton backButton;
    private ImageButton addToCartButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item);
        Intent idIntent = getIntent();
        helper = new DatabaseHelper(this);
        itemData = helper.getItemDataById(idIntent.getIntExtra("ID", 0));

        priceText = (TextView) findViewById(R.id.priceText);
        nameText = (TextView) findViewById(R.id.nameText);
        ratingText = (TextView) findViewById(R.id.ratingText);
        itemImage = (ImageView) findViewById(R.id.itemImage);
        reviewBtn = (Button) findViewById(R.id.reviewBtn);
        backButton = (ImageButton) findViewById(R.id.back_btn);
        mapButton = (Button) findViewById(R.id.mapBtn);
        addToCartButton = (ImageButton) findViewById(R.id.addToCartButton);

        ratingText.setText(String.format("%.2f", itemData.getRating()));
        priceText.setText(String.valueOf(itemData.getPrice()));
        nameText.setText(itemData.getName());

        int drawableResId = this.getResources().getIdentifier('a' + String.valueOf(itemData.getId()), "drawable", this.getPackageName());
        itemImage.setImageResource(drawableResId);

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), itemData.getCategory(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), com.example.term.ReviewListActivity.class);
                intent.putExtra("ID", itemData.getId());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), com.example.term.MapActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backActivity();
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.addToCart(itemData.getId());
                Toast.makeText(getApplicationContext(), "'" + itemData.getName() + "'가 담겼습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            float currentRating = data.getFloatExtra("rating" , 0.0f);
            float savedRating = itemData.getRating();
            if (savedRating != currentRating)
                helper.onUpdateRating(itemData.getId(), currentRating);
            setRatingText(currentRating);
        }
    }

    private void setRatingText(float currentRating) {
        float savedRating = itemData.getRating();
        //float currentRating = helper.getItemRatingById(itemData.getId());
        // 데이터가 갱신되지 않았다면 갱신 후 데이터 적용
        if (savedRating != currentRating)
            helper.onUpdateRating(itemData.getId(), currentRating);
        ratingText.setText(String.format("%.2f", currentRating));
    }

    public void backActivity() {
        Intent intent = new Intent(this, com.example.term.MainActivity.class);
        startActivity(intent);
    }

}
