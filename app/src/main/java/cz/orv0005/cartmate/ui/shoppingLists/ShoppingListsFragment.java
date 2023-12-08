package cz.orv0005.cartmate.ui.shoppingLists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import cz.orv0005.cartmate.MainActivity;
import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.adapters.ShoppingListAdapter;
import cz.orv0005.cartmate.databinding.FragmentShoppingListsBinding;
import cz.orv0005.cartmate.mappers.ShoppingListMapper;
import cz.orv0005.cartmate.models.ShoppingList;

public class ShoppingListsFragment extends Fragment {

    private FragmentShoppingListsBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentShoppingListsBinding.inflate(inflater, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();

        RecyclerView shoppingListRecyclerView = binding.shoppingListsRecyclerMenu;
        List<ShoppingList> lists = Objects.requireNonNull(mainActivity).getShoppingLists();

        ShoppingListAdapter adapter = new ShoppingListAdapter(lists, position -> {
            ShoppingList l = lists.get(position);

            NavController navController = Navigation.findNavController(
                    mainActivity,
                    R.id.nav_host_fragment_content_main
            );

            Bundle b = new Bundle();
            b.putLong("shoppingListId", l.getId());

            navController.navigate(R.id.shoppingListDetail, b);
        }, position -> mainActivity.showAddShoppingListDialog(
                lists.get(position),
                shoppingListRecyclerView)
        );

        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingListRecyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                List<ShoppingList> lists = Objects.requireNonNull(mainActivity).getShoppingLists();

                if (direction == ItemTouchHelper.LEFT) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage(R.string.do_you_want_to_remove_this_item)
                            .setPositiveButton(R.string.yes, (dialogInterface, i) -> {

                                ShoppingListMapper m = new ShoppingListMapper(
                                        new SQLiteHelper(requireContext()));
                                m.delete(lists.get(position).getId());

                                lists.remove(position);

                                Objects.requireNonNull(shoppingListRecyclerView.getAdapter())
                                        .notifyItemRemoved(position);
                            }).setNegativeButton(R.string.no, (dialogInterface, i) ->
                                    Objects.requireNonNull(shoppingListRecyclerView.getAdapter())
                                            .notifyItemChanged(position)).show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(shoppingListRecyclerView);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}