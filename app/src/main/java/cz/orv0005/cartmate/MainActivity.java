package cz.orv0005.cartmate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cz.orv0005.cartmate.adapters.ShoppingListAdapter;
import cz.orv0005.cartmate.databinding.ActivityMainBinding;
import cz.orv0005.cartmate.mappers.ShoppingListMapper;
import cz.orv0005.cartmate.models.ShoppingList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ShoppingListMapper shoppingListMapper;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private RecyclerView shoppingListRecyclerView;

    private FloatingActionButton fab;

    public static String getLocalDatePattern(Context context) {

        SimpleDateFormat f = (SimpleDateFormat) DateFormat.getDateFormat(context);
        return f.toLocalizedPattern();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.shoppingListMapper = new ShoppingListMapper(new SQLiteHelper(this));

        cz.orv0005.cartmate.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        this.fab = binding.appBarMain.addShoppingListFab;
        this.fab.setOnClickListener(view -> showAddShoppingListDialog(null, shoppingListRecyclerView));

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_shopping_lists, R.id.nav_products).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() != R.id.nav_shopping_lists) {
                this.fab.hide();
            } else {
                this.fab.show();
            }
        });

        try {

            this.shoppingLists = this.shoppingListMapper.fetchAll();
        } catch (ParseException e) {

            Log.e("ParseException", e.toString());
            Toast.makeText(this, R.string.error_when_loading_shopping_lists, Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e) {

            Log.e("SQLException", e.toString());
            Toast.makeText(this, "Error when loading shopping lists.", Toast.LENGTH_SHORT).show();
        }

        this.shoppingListRecyclerView = findViewById(R.id.shoppingListsRecyclerMenu);
        ShoppingListAdapter adapter = new ShoppingListAdapter(this.shoppingLists, position -> {
            ShoppingList l = shoppingLists.get(position);

            Bundle b = new Bundle();
            b.putLong("shoppingListId", l.getId());

            navController.navigate(R.id.shoppingListDetail, b);
        }, position -> showAddShoppingListDialog(shoppingLists.get(position), shoppingListRecyclerView));

        if (this.shoppingListRecyclerView != null)
            this.shoppingListRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void showAddShoppingListDialog(ShoppingList list, RecyclerView recyclerView) {

        Calendar calendar = Calendar.getInstance();

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_list, null);

        EditText etName = dialogView.findViewById(R.id.editTextName);
        EditText etShopName = dialogView.findViewById(R.id.editTextEan);
        EditText etDate = dialogView.findViewById(R.id.editTextDate);

        if (list != null) {

            etName.setText(list.getName());
            etShopName.setText(list.getShopName());
            etDate.setText(list.getDate().format(DateTimeFormatter.ofPattern(MainActivity.getLocalDatePattern(MainActivity.this), Locale.getDefault())));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_shopping_list)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog1, whichButton) -> {

                    String name = etName.getText().toString();
                    String shopName = etShopName.getText().toString();
                    String date = etDate.getText().toString();

                    if (name.equals("")) {
                        Toast.makeText(getBaseContext(), R.string.name_is_empty, Toast.LENGTH_LONG).show();
                        return;

                    } else if (shopName.equals("")) {
                        Toast.makeText(getBaseContext(), R.string.shop_name_is_empty, Toast.LENGTH_LONG).show();
                        return;

                    } else if (date.equals("")) {
                        Toast.makeText(getBaseContext(), R.string.date_is_empty, Toast.LENGTH_LONG).show();
                        return;

                    }

                    ShoppingList l = new ShoppingList(
                            name,
                            shopName,
                            LocalDate.parse(date, DateTimeFormatter.ofPattern(MainActivity.getLocalDatePattern(MainActivity.this), Locale.getDefault()))
                    );

                    if (list != null)
                        l.setId(list.getId());

                    actualizeList(l, shoppingLists.indexOf(list), recyclerView);
                })
                .setNegativeButton(R.string.cancel, null).create();

        DatePickerDialog.OnDateSetListener dateListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat dateFormat = new SimpleDateFormat(MainActivity.getLocalDatePattern(MainActivity.this), Locale.getDefault());
            etDate.setText(dateFormat.format(calendar.getTime()));
        };

        etDate.setOnClickListener(view -> new DatePickerDialog(MainActivity.this, dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        dialog.show();
    }

    public void actualizeList(ShoppingList l, Integer position, RecyclerView recyclerView) {
        l.setId(this.shoppingListMapper.save(l));

        if (position != -1) {
            this.shoppingLists.set(position, l);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(position);
        } else {
            this.shoppingLists.add(l);

            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(
                    this.shoppingLists.size() - 1
            );
        }
    }

    public List<ShoppingList> getShoppingLists() {

        return this.shoppingLists;
    }
}