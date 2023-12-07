package cz.orv0005.cartmate.ui.shoppingLists;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.adapters.ShoppingListItemAdapter;
import cz.orv0005.cartmate.mappers.ShoppingListItemMapper;
import cz.orv0005.cartmate.mappers.ShoppingListMapper;
import cz.orv0005.cartmate.models.ShoppingList;
import cz.orv0005.cartmate.models.ShoppingListItem;
import cz.orv0005.cartmate.openfoodfacts.APIMapper;
import cz.orv0005.cartmate.openfoodfacts.FoodFactItem;

public class ShoppingListDetailFragment extends Fragment {

    private ShoppingListItemMapper shoppingListItemMapper;
    private List<ShoppingListItem> items = new ArrayList<>();
    private RecyclerView itemsRecyclerView;
    private ShoppingList shoppingList;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(requireContext(), R.string.code_scan_cancelled, Toast.LENGTH_LONG).show();
                } else {

                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    Future<FoodFactItem> future = executor.submit(() -> (new APIMapper()).getByEan(result.getContents()));

                    try {
                        FoodFactItem i = future.get();

                        if (i != null) {

                            this.actualizeItem(
                                    new ShoppingListItem(
                                            this.shoppingList.getId(),
                                            0L,
                                            i.getName(),
                                            0,
                                            1
                                    ), -1
                            );
                        } else {
                            Toast.makeText(getContext(), R.string.product_was_not_found, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.error_while_loading_product, Toast.LENGTH_LONG).show();
                    }
                }
            });


    public ShoppingListDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null && args.containsKey("shoppingListId")) {

            ShoppingListMapper m = new ShoppingListMapper(new SQLiteHelper(requireContext()));

            this.shoppingList = m.fetch(args.getLong("shoppingListId"));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping_list_detail, container, false);

        this.shoppingListItemMapper = new ShoppingListItemMapper(new SQLiteHelper(requireContext()));
        this.items = this.shoppingListItemMapper.fetchAllForList(this.shoppingList.getId());

        this.itemsRecyclerView = view.findViewById(R.id.itemsRecyclerView);
        this.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.itemsRecyclerView.setAdapter(new ShoppingListItemAdapter(this.items, position -> {
            ShoppingListItem i = items.get(position);

            showAddListItem(i);
        }));

        view.findViewById(R.id.addListItemFab).setOnClickListener(view1 -> showAddListItem(null));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage(R.string.do_you_want_to_remove_this_item).setPositiveButton(R.string.yes, (dialogInterface, i) -> {

                        ShoppingListItemMapper m = new ShoppingListItemMapper(new SQLiteHelper(requireContext()));
                        m.delete(items.get(position).getId());

                        items.remove(position);

                        Objects.requireNonNull(itemsRecyclerView.getAdapter()).notifyItemRemoved(position);
                    }).setNegativeButton(R.string.no, (dialogInterface, i) -> Objects.requireNonNull(itemsRecyclerView.getAdapter()).notifyItemChanged(position)).show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(itemsRecyclerView);

        return view;
    }

    private void showAddListItem(ShoppingListItem item) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_list_item, null);

        EditText etName = dialogView.findViewById(R.id.editTextName);
        EditText etCountToBuy = dialogView.findViewById(R.id.editTextCountToBuy);

        if (item != null) {
            etName.setText(item.getName());
            etCountToBuy.setText(item.getCountToBuy().toString());
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.edit_list_item)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog1, whichButton) -> {

                    String name = etName.getText().toString();

                    if (name.equals("")) {
                        Toast.makeText(getContext(), R.string.name_is_empty, Toast.LENGTH_LONG).show();
                        return;

                    }

                    String countToBuyString = etCountToBuy.getText().toString().trim();
                    if (countToBuyString.equals("")) {
                        Toast.makeText(getContext(), R.string.count_to_buy_is_empty, Toast.LENGTH_LONG).show();
                        return;

                    }

                    ShoppingListItem i = new ShoppingListItem(
                            this.shoppingList.getId(),
                            item == null ? 0L : item.getIdItem(),
                            name,
                            item == null ? 0 : item.getCount(),
                            Integer.parseInt(countToBuyString)
                    );

                    if (item != null) {
                        i.setId(item.getId());
                    }

                    actualizeItem(i, this.items.indexOf(item));
                })
                .setNegativeButton(R.string.cancel, null).create();

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.scan_code), (dialogInterface, i) -> barcodeLauncher.launch(new ScanOptions().setBeepEnabled(false)));

        dialog.show();
    }

    private void actualizeItem(ShoppingListItem i, Integer position) {
        i.setId(this.shoppingListItemMapper.save(i));

        if (position != -1) {
            this.items.set(position, i);
            Objects.requireNonNull(this.itemsRecyclerView.getAdapter()).notifyItemChanged(position);
        } else {
            this.items.add(i);

            Objects.requireNonNull(this.itemsRecyclerView.getAdapter()).notifyItemInserted(
                    this.items.size() - 1
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}