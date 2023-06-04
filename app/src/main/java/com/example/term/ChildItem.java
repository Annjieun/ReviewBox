package com.example.term;

import com.example.term.ItemData;

public class ChildItem {

    private ItemData itemData;
    private int amount;

    public ChildItem() {
    }

    public ChildItem(ItemData itemData,int amount) {
        this.itemData = itemData;
        this.amount = amount;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

