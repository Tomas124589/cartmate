package cz.orv0005.cartmate.ui.shoppingLists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import cz.orv0005.cartmate.MainActivity;
import cz.orv0005.cartmate.R;
import cz.orv0005.cartmate.adapters.ShoppingListAdapter;
import cz.orv0005.cartmate.databinding.FragmentShoppingListsBinding;
import cz.orv0005.cartmate.models.ShoppingList;

public class ShoppingListsFragment extends Fragment {

    private FragmentShoppingListsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShoppingListsBinding.inflate(inflater, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            FloatingActionButton fab = mainActivity.findViewById(R.id.addShoppingListFab);
            if (fab != null)
                fab.show();
        }

        RecyclerView shoppingListRecyclerView = binding.shoppingListsRecyclerMenu;
        ShoppingListAdapter adapter = new ShoppingListAdapter(Objects.requireNonNull(mainActivity).getShoppingLists(), position -> {
            ShoppingList l = mainActivity.getShoppingLists().get(position);

            NavController navController = Navigation.findNavController(mainActivity, R.id.nav_host_fragment_content_main);

            Bundle b = new Bundle();
            b.putLong("shoppingListId", l.getId());

            navController.navigate(R.id.shoppingListDetail, b);
        });
        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingListRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}