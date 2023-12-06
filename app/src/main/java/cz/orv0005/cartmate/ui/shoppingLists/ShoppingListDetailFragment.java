package cz.orv0005.cartmate.ui.shoppingLists;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.orv0005.cartmate.MainActivity;
import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.adapters.ShoppingListItemAdapter;
import cz.orv0005.cartmate.mappers.ShoppingListItemMapper;
import cz.orv0005.cartmate.models.ShoppingListItem;

public class ShoppingListDetailFragment extends Fragment {

    private ShoppingListItemMapper shoppingListItemMapper;
    private List<ShoppingListItem> items = new ArrayList<>();
    private RecyclerView itemsRecyclerView;
    private long shoppingListId;


    public ShoppingListDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();

        if (args != null && args.containsKey("shoppingListId")) {

            this.shoppingListId = args.getLong("shoppingListId");
        }

        View view = inflater.inflate(R.layout.fragment_shopping_list_detail, container, false);

        this.shoppingListItemMapper = new ShoppingListItemMapper(new SQLiteHelper(getContext()));
        this.items = this.shoppingListItemMapper.fetchAllForList(this.shoppingListId);

        this.itemsRecyclerView = view.findViewById(R.id.itemsRecyclerView);
        this.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.itemsRecyclerView.setAdapter(new ShoppingListItemAdapter(this.items, position -> {
            ShoppingListItem i = items.get(position);

            Log.d("itemName", i.getName());
        }));

        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            FloatingActionButton fab = mainActivity.findViewById(R.id.addShoppingListFab);

            if (fab != null)
                fab.hide();
        }

        view.findViewById(R.id.addListItemFab).setOnClickListener(view1 -> showAddListItem());

        return view;
    }

    private void showAddListItem() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_list_item, null);

        EditText etName = dialogView.findViewById(R.id.editTextName);
        EditText etCount = dialogView.findViewById(R.id.editTextCount);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add new list item")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {

                    String name = etName.getText().toString();
                    Integer count = Integer.parseInt(etCount.getText().toString());

                    appendShoppingListItem(new ShoppingListItem(
                            shoppingListId,
                            0L,
                            name,
                            count
                    ));
                })
                .setNegativeButton("Cancel", null).create();

        dialog.show();
    }

    private void appendShoppingListItem(ShoppingListItem i) {

        i.setId(this.shoppingListItemMapper.save(i));
        this.items.add(i);

        Objects.requireNonNull(this.itemsRecyclerView.getAdapter()).notifyItemInserted(
                this.items.size() - 1
        );
    }
}