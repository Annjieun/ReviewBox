package com.example.term;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.term.CartListActivity;
import com.example.term.ChildItem;
import com.example.term.DatabaseHelper;
import com.example.term.ItemData;
import com.example.term.ItemDetailActivity;
import com.example.term.ListviewAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView exListView;
    private ListviewAdapter adapter;
    private ArrayList<com.example.term.ParentItem> groupList = new ArrayList<>(); //부모 리스트
    private ArrayList<ArrayList<ChildItem>> childList = new ArrayList<>(); //자식 리스트
    private ArrayList<ArrayList<ChildItem>> monthArray = new ArrayList<>(); //1월 ~ 12월을 관리하기 위한 리스트

    private DatabaseHelper helper;
    private SQLiteDatabase database;
    private ArrayList<ItemData> itemList = new ArrayList<ItemData>();

    private EditText searchText;
    private Button searchButton;
    private Button homeButton;
    private ImageButton cartButton;
    private ListView searchListView;
    private ArrayAdapter<String> searchAdapter;
    private ArrayList<String> searchedItemList = new ArrayList<>();
    private ArrayList<Integer> searchedItemIDList = new ArrayList<>();
    private ArrayList<Category> categoryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exListView = (ExpandableListView) findViewById(R.id.expandable_list);
        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (Button) findViewById(R.id.searchButton);
        homeButton = (Button) findViewById(R.id.homeButton);
        cartButton = (ImageButton) findViewById(R.id.cartButton);

        searchListView = (ListView) findViewById(R.id.searchListView);
        searchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchedItemList);
        searchListView.setAdapter(searchAdapter);

        //monthArray에 1월~12월 배열을 모두 추가
        for (int i = 0; i < 12; i++)
            monthArray.add(new ArrayList<ChildItem>());

        addCategory("mirror","A","거울/테이블");
        addCategory("cleaning","B","정리용품");
        addCategory("clean_hands","C","세정용품");
        addCategory("delete","D","일회용품");
        addCategory("incandescent","E","전구/디퓨저");
        addCategory("board","F","보드/미술용품");
        addCategory("food","G","식품");
        addCategory("ic_baseline_water_drop_24","H","생수");
        addCategory("tissu","I","티슈/키친타올");
        addCategory("paper","J","사무문구");
        addCategory("car","K","차량용품");
        addCategory("fireplace","L","캠핑용품");

        //어댑터에 각각의 배열 등록
        adapter = new ListviewAdapter();
        adapter.parentItems = groupList;
        adapter.childItems = childList;

        exListView.setAdapter(adapter);
        exListView.setGroupIndicator(null); //리스트뷰 기본 아이콘 표시 여부

        setListItems();
        createDatabase();
        itemList = excuteQuery();

        setChildItems(itemList);

        //리스트 클릭시 지출 항목이 토스트로 나타난다
        exListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ItemData itemData = adapter.getChild(groupPosition, childPosition).getItemData();
                Toast.makeText(getApplicationContext(), itemData.getCategory(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                intent.putExtra("ID", itemData.getId());
                startActivity(intent);
                return true;
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchListView.setVisibility(View.INVISIBLE);
                exListView.setVisibility(View.VISIBLE);
                searchText.setText("");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exListView.setVisibility(View.INVISIBLE);
                searchListView.setVisibility(View.VISIBLE);
                showSearchResult();
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartListActivity.class);
                startActivity(intent);
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                intent.putExtra("ID", searchedItemIDList.get(i));
                startActivity(intent);
            }
        });
    }

    private void addCategory(String imgName, String area, String categoryName){
        Category category = new Category();
        category.imgSource = this.getResources().getIdentifier(imgName, "drawable", this.getPackageName());
        category.area = area;
        category.categoryName = categoryName;
        categoryArrayList.add(category);
    }

    public void setChildItems(ArrayList<ItemData> itemDataList) {
        for (int index = 0; index < itemDataList.size(); index++) {
            addChildItem(itemDataList.get(index), 1);
        }
    }

    public void addChildItem(ItemData itemData, int amount) {
        ChildItem item = new ChildItem(itemData, amount);
        int area_ascii = 0;
        String area = item.getItemData().getArea();
        if (!area.equals("")) {
            area_ascii = area.charAt(0);
        }
        // area는 아스키코드 'A' ~ 'Z' 사이 값이어야 함
        if (area_ascii >= 'A' && area_ascii <= 'Z') {
            monthArray.get(area_ascii - 'A').add(item); // A-Z 에서 아스키코드 'A'를 빼 배열 인덱스로 사용
            //childList.add(item);
        } else {
            Toast.makeText(getApplicationContext(), "area_ascii 오류", Toast.LENGTH_SHORT).show();
            monthArray.get(0).add(item);
        }
    }

    private void createDatabase() {
        helper = new DatabaseHelper(this);
        database = helper.getWritableDatabase();
        Toast.makeText(getApplicationContext(), "createDatabase", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ItemData> excuteQuery() {
        Cursor cursor = database.rawQuery("select ID, 구역, 상품명, 카테고리, 가격, 평점 from Product", null);
        int recordCount = cursor.getCount();
        ArrayList<ItemData> itemDatas = new ArrayList<>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            ItemData data = new ItemData();
            data.setId(cursor.getInt(0));
            data.setArea(cursor.getString(1));
            data.setName(cursor.getString(2));
            data.setCategory(cursor.getString(3));
            data.setPrice(cursor.getInt(4));
            data.setRating(cursor.getFloat(5));
            itemDatas.add(data);
        }
        cursor.close();
        return itemDatas;
    }


    //리스트 초기화 함수
    public void setListItems() {
        groupList.clear();
        childList.clear();

        childList.addAll(monthArray);

        Category tempCategory = new Category();
        //부모 리스트 내용 추가
        for (int i = 0; i < categoryArrayList.size(); i++) {
            tempCategory.setImgSource(categoryArrayList.get(i).getImgSource());
            tempCategory.setArea(categoryArrayList.get(i).getArea());
            tempCategory.setCategoryName(categoryArrayList.get(i).getCategoryName());
            groupList.add(new com.example.term.ParentItem(tempCategory.getImgSource(),tempCategory.getCategoryName(), tempCategory.getArea() + "구역"));
            //groupList.add(new ParentItem(String.valueOf(ch), childList.get((ch - 64) - 1).size() + "건"));
        }

        adapter.notifyDataSetChanged();
    }

    private ArrayList<ItemData> getSearchedItemList(String searchText) {
        ArrayList<ItemData> matchItems = new ArrayList<>();
        for(int i=0;i<itemList.size();i++){
            if(itemList.get(i).getName().contains(searchText)){
                matchItems.add(itemList.get(i));
            }
        }
        return matchItems;
    }

    private void showSearchResult(){
        ArrayList<ItemData> searchItems = getSearchedItemList(searchText.getText().toString());

        searchedItemList.clear();
        searchedItemIDList.clear();
        for (int i = 0; i < searchItems.size(); i++) {
            searchedItemList.add(searchItems.get(i).getName());
            searchedItemIDList.add(searchItems.get(i).getId());
        }
        searchAdapter.notifyDataSetChanged();
    }
}