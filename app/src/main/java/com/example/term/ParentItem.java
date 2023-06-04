package com.example.term;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragAndDropPermissions;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ParentItem extends AppCompatActivity {
    private int imgSource;
    private String area;
    private String category;


    public ParentItem(int imgSource, String category, String area) {
        this.imgSource = imgSource;
        this.category = category;
        this.area = area;
    }

    public ParentItem(String category, String area) {

        this.category = category;
        this.area = area;
    }

    public int getImgSource() {
        return imgSource;
    }

    public void setImgSource(int imgSource) {
        this.imgSource = imgSource;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}