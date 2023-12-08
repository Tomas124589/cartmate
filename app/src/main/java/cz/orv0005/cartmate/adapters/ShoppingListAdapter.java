package cz.orv0005.cartmate.adapters;

import static androidx.recyclerview.widget.RecyclerView.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.models.ShoppingList;
import cz.orv0005.cartmate.ui.shoppingLists.OnClickListener;
import cz.orv0005.cartmate.ui.shoppingLists.OnLongClickListener;

public class ShoppingListAdapter extends Adapter<ViewHolder> {

    private final List<ShoppingList> shoppingLists;
    private final OnClickListener listener;
    private final OnLongClickListener longClickListener;

    public ShoppingListAdapter(
            List<ShoppingList> shoppingLists,
            OnClickListener listener,
            OnLongClickListener longClickListener) {

        this.shoppingLists = shoppingLists;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(
                R.layout.shopping_list_row,
                parent,
                false
        );
        return new ShoppingListViewHolder(view, listener, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingList shoppingList = shoppingLists.get(position);
        ShoppingListViewHolder listViewHolder = (ShoppingListViewHolder) holder;

        listViewHolder.textViewName.setText(shoppingList.getName());
        listViewHolder.textViewShopName.setText(shoppingList.getShopName());
        listViewHolder.textViewDate.setText(SQLiteHelper.localDate2str(shoppingList.getDate()));
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public static class ShoppingListViewHolder extends ViewHolder {

        TextView textViewName;
        TextView textViewShopName;
        TextView textViewDate;

        ShoppingListViewHolder(
                @NonNull View itemView,
                OnClickListener listener,
                OnLongClickListener longClickListener
        ) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            textViewShopName = itemView.findViewById(R.id.count);
            textViewDate = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(view -> {
                if (listener != null) {

                    int pos = getAdapterPosition();
                    if (pos != NO_POSITION) {
                        listener.onClick(pos);
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                if (longClickListener != null) {

                    int pos = getAdapterPosition();
                    if (pos != NO_POSITION) {
                        longClickListener.onLongClick(pos);
                    }

                    return true;
                }

                return false;
            });
        }
    }
}
