package cz.orv0005.cartmate.ui.shoppingLists;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cz.orv0005.cartmate.MainActivity;
import cz.orv0005.cartmate.adapters.ShoppingListAdapter;
import cz.orv0005.cartmate.databinding.FragmentShoppingListsBinding;
import cz.orv0005.cartmate.models.ShoppingList;

public class ShoppingListsFragment extends Fragment {

    private FragmentShoppingListsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShoppingListsBinding.inflate(inflater, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();

        RecyclerView shoppingListRecyclerView = binding.shoppingListsRecyclerMenu;
        ShoppingListAdapter adapter = new ShoppingListAdapter(mainActivity.getShoppingLists(), new ShoppingListListener() {
            @Override
            public void onClick(int position) {

                ShoppingList l = mainActivity.getShoppingLists().get(position);

                //Intent i = new Intent(this, )
                Log.d("TEST", Long.toString(l.getId()));
            }
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