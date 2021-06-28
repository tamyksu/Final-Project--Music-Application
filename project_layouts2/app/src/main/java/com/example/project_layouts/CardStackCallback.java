package com.example.project_layouts;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class CardStackCallback extends DiffUtil.Callback {

    private List<Users> old, baru;

    public CardStackCallback(ArrayList<Users> old, ArrayList<Users> baru) {
        this.old = old;
        this.baru = baru;
    }

    @Override
    public int getOldListSize() {
        return old.size();
    }

    @Override
    public int getNewListSize() {
        return baru.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).getId() == baru.get(newItemPosition).getId();
    }

  /*  @Override
    public boolean areItemsTheSame(String oldID,String newID) {
        return oldID == newID;
    }*/

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition) == baru.get(newItemPosition);
    }
}