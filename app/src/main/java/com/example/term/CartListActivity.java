package com.example.term;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartListActivity extends AppCompatActivity {
    private ListView cartListView;
    private ArrayAdapter<String> searchAdapter;
    private ArrayList<String> searchedItemList = new ArrayList<>();
    private ArrayList<Integer> searchedItemIDList = new ArrayList<>();
    private ArrayList<com.example.term.ItemData> searchedItemDataList = new ArrayList<>();
    private DatabaseHelper helper;

    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListView = (ListView) findViewById(R.id.cartListView);
        searchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchedItemList);
        cartListView.setAdapter(searchAdapter);

        helper = new DatabaseHelper(this);

        searchedItemIDList = helper.getCartIDList();
        for(int i=0;i<searchedItemIDList.size();i++){
            searchedItemDataList.add(helper.getItemDataById(searchedItemIDList.get(i)));
        }

        searchedItemList.clear();
        for (int i = 0; i < searchedItemIDList.size(); i++) {
            searchedItemList.add(searchedItemDataList.get(i).getName());
        }
        searchAdapter.notifyDataSetChanged();

        cartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), com.example.term.ItemDetailActivity.class);
                intent.putExtra("ID", searchedItemIDList.get(i));
                startActivity(intent);
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
        Intent intent = new Intent(this, com.example.term.MainActivity.class);
        startActivity(intent);
    }
}
