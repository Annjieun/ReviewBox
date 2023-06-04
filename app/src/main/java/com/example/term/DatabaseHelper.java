package com.example.term;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.term.ItemData;
import com.example.term.ReviewData;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static int reviewIndex;
    public static String NAME = "itemDB.db";
    public static int VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old, int recent) {
        if (recent > 1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Product");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Review");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Cart");
        }
    }

    private void createTable(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists Product(" +
                " ID INTEGER PRIMARY KEY autoincrement," +
                " 구역 TEXT, " +
                " 상품명 TEXT NOT NULL UNIQUE, " +
                " 카테고리 TEXT," +
                " 가격 INTEGER NOT NULL," +
                " 평점 REAL);";
        sqLiteDatabase.execSQL(sql);

        sqLiteDatabase.execSQL("PRAGMA foreign_keys = 1");  // 외래 키 활성화

        String sql2 = "create table if not exists Review(" +
                " 댓글ID INTEGER PRIMARY KEY autoincrement," +
                " ID INTEGER NOT NULL, " +
                " Review TEXT, " +
                " Rating REAL, " +
                "FOREIGN KEY(ID) REFERENCES Product(ID)" +
                ");";

        sqLiteDatabase.execSQL(sql2);

        String sql3 = "create table if not exists Review(" +
                " ID INTEGER NOT NULL UNIQUE, " +
                "FOREIGN KEY(ID) REFERENCES Product(ID)" +
                ");";

        sqLiteDatabase.execSQL(sql3);
    }

    public ItemData getItemDataById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select ID, 구역, 상품명, 카테고리, 가격, 평점 from Product where ID=" + id, null);
        if (cursor.getCount() != 1) return null;
        cursor.moveToNext();
        ItemData data = new ItemData();
        data.setId(cursor.getInt(0));
        data.setArea(cursor.getString(1));
        data.setName(cursor.getString(2));
        data.setCategory(cursor.getString(3));
        data.setPrice(cursor.getInt(4));
        data.setRating(cursor.getFloat(5));
        cursor.close();
        return data;
    }

    public void setReview(int product_id, String review, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select 댓글ID, ID, Review, Rating from Review", null);
        cv.put("댓글ID", cursor.getCount() + 1);
        cv.put("ID", product_id);
        cv.put("Review", review);
        cv.put("Rating", rating);
        long result = db.insert("Review", null, cv);
        if (result == -1) {
            System.out.println("setReview failed");
        }
/*        String query = "INSERT INTO " + "Review" + " SET " +
                "Rating=" + rating + " " +
                "Review=" + review + " " +
                "WHERE ID=" + product_id;*/
        /*        db.execSQL(query);*/
    }

    public ArrayList<ReviewData> getReviewDatasById(int product_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select 댓글ID, ID, Review, Rating from Review where ID=" + product_id, null);
        int recordCount = cursor.getCount();
        ArrayList<ReviewData> reviewDatas = new ArrayList<>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            ReviewData data = new ReviewData();
            data.setId(cursor.getInt(0));
            data.setProduct_id(cursor.getInt(1));
            data.setReview(cursor.getString(2));
            data.setRating(cursor.getFloat(3));
            reviewDatas.add(data);
        }
        cursor.close();
        return reviewDatas;
    }

    public void onUpdateRating(int id, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE Product SET " +
                "평점=" + rating + " " +
                "WHERE ID=" + id;
        db.execSQL(query);
    }

    public void addToCart(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = db.rawQuery("select ID from Cart", null);
        cv.put("ID", id);
        long result = db.insert("Cart", null, cv);
        if (result == -1) {
            System.out.println("setReview failed");
        }
    }

    public void deleteFromCartById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Cart where ID="+id, null);
    }

    public ArrayList<Integer> getCartIDList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select ID from Cart", null);
        int recordCount = cursor.getCount();
        ArrayList<Integer> idList = new ArrayList<>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            idList.add(cursor.getInt(0));
        }
        cursor.close();
        return idList;
    }
}
