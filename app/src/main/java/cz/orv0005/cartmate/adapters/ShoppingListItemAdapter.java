package cz.orv0005.cartmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.List;

import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.mappers.ShoppingListItemMapper;
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

        View view = LayoutInflater.from(
                parent.getContext()).inflate(
                R.layout.shopping_list_item_row,
                parent,
                false
        );
        return new ShoppingListItemViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShoppingListItem item = items.get(position);
        ShoppingListItemViewHolder itemViewHolder = (ShoppingListItemViewHolder) holder;

        itemViewHolder.textViewName.setText(item.getName());
        itemViewHolder.textViewCount.setText(
                item.getCount().toString() + "/" + item.getCountToBuy().toString()
        );

        itemViewHolder.btnMinus.setOnClickListener(view -> {
            item.setCount(item.getCount() - 1);

            if (item.getCount() < 0)
                item.setCount(0);

            ShoppingListItemMapper m = new ShoppingListItemMapper(
                    new SQLiteHelper(view.getContext())
            );
            m.save(item);

            notifyItemChanged(position);
        });

        itemViewHolder.btnPlus.setOnClickListener(view -> {
            item.setCount(item.getCount() + 1);

            if (item.getCount() > item.getCountToBuy())
                item.setCount(item.getCountToBuy());

            ShoppingListItemMapper m = new ShoppingListItemMapper(
                    new SQLiteHelper(view.getContext())
            );
            m.save(item);

            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewCount;
        Button btnMinus;
        Button btnPlus;

        ShoppingListItemViewHolder(@NonNull View itemView, OnClickListener listener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            textViewCount = itemView.findViewById(R.id.count);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        listener.onClick(pos);
                }
            });
        }
    }
}
