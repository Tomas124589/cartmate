package cz.orv0005.cartmate.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.models.ShoppingList;
import cz.orv0005.cartmate.ui.shoppingLists.ShoppingListListener;

public class ShoppingListAdapter extends RecyclerView.Adapter {

    private List<ShoppingList> shoppingLists;
    private final ShoppingListListener listener;

    public ShoppingListAdapter(List<ShoppingList> shoppingLists, ShoppingListListener listener) {

        this.shoppingLists = shoppingLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_row, parent, false);
        return new ShoppingListViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

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

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder implements ShoppingListListener {
        TextView textViewName;
        TextView textViewShopName;
        TextView textViewDate;

        ShoppingListViewHolder(@NonNull View itemView, ShoppingListListener listener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            textViewShopName = itemView.findViewById(R.id.shop_name);
            textViewDate = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {

                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onClick(pos);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(int position) {

            Log.d("YOU", "AAA");
        }
    }
}
