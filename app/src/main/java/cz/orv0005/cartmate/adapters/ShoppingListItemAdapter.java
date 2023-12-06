package cz.orv0005.cartmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.List;

import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.models.ShoppingListItem;
import cz.orv0005.cartmate.ui.shoppingLists.OnClickListener;

public class ShoppingListItemAdapter extends Adapter<RecyclerView.ViewHolder> {

    private final List<ShoppingListItem> items;
    private final OnClickListener listener;

    public ShoppingListItemAdapter(List<ShoppingListItem> items, OnClickListener listener) {

        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item_row, parent, false);
        return new ShoppingListItemViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ShoppingListItem item = items.get(position);
        ShoppingListItemViewHolder listViewHolder = (ShoppingListItemViewHolder) holder;

        listViewHolder.textViewName.setText(item.getName());
        listViewHolder.textViewCount.setText(item.getCount().toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ShoppingListItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        TextView textViewName;
        TextView textViewCount;

        ShoppingListItemViewHolder(@NonNull View itemView, OnClickListener listener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            textViewCount = itemView.findViewById(R.id.count);

            itemView.setOnClickListener(view -> {
                if (listener != null) {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onClick(pos);
                    }
                }
            });
        }

        @Override
        public void onClick(int position) {
        }
    }
}
