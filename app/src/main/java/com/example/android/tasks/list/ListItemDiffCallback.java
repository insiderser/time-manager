package com.example.android.tasks.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

/**
 * A {@link DiffUtil.ItemCallback} that compares {@link ListItem}s.
 */
class ListItemDiffCallback extends DiffUtil.ItemCallback<ListItem> {

    @Override
    public boolean areItemsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
        return ListItem.sameIds(oldItem, newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
        return oldItem.equals(newItem);
    }
}
