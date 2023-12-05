package cz.orv0005.cartmate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.orv0005.cartmate.adapters.ShoppingListAdapter;
import cz.orv0005.cartmate.databinding.ActivityMainBinding;
import cz.orv0005.cartmate.mappers.ShoppingListMapper;
import cz.orv0005.cartmate.models.ShoppingList;
import cz.orv0005.cartmate.ui.shoppingLists.ShoppingListListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private ShoppingListMapper shoppingListMapper;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private RecyclerView shoppingListRecyclerView;

    public static String getLocalDatePattern(Context context) {

        SimpleDateFormat f = (SimpleDateFormat) DateFormat.getDateFormat(context);
        return f.toLocalizedPattern();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.shoppingListMapper = new ShoppingListMapper(new SQLiteHelper(this));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddShoppingListDialog();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_shopping_lists, R.id.nav_products).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        try {

            this.shoppingLists = this.shoppingListMapper.fetchAll();
        } catch (ParseException e) {

            Log.e("ParseException", e.toString());
            Toast.makeText(this, "Error when loading shopping lists.", Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e) {

            Log.e("SQLException", e.toString());
            Toast.makeText(this, "Error when loading shopping lists.", Toast.LENGTH_SHORT).show();
        }

        this.shoppingListRecyclerView = findViewById(R.id.shoppingListsRecyclerMenu);
        ShoppingListAdapter adapter = new ShoppingListAdapter(this.shoppingLists, new ShoppingListListener() {
            @Override
            public void onClick(int position) {

                ShoppingList l = shoppingLists.get(position);

                Log.d("TEST", Long.toString(l.getId()));
            }
        });
        this.shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void showAddShoppingListDialog() {

        Calendar calendar = Calendar.getInstance();

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_list, null);

        EditText etName = dialogView.findViewById(R.id.editTextName);
        EditText etShopName = dialogView.findViewById(R.id.editTextShopName);
        EditText etDate = dialogView.findViewById(R.id.editTextDate);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add new shopping list")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String name = etName.getText().toString();
                        String shopName = etShopName.getText().toString();
                        String date = etDate.getText().toString();

                        appendShoppingList(new ShoppingList(
                                name,
                                shopName,
                                LocalDate.parse(date, DateTimeFormatter.ofPattern(MainActivity.getLocalDatePattern(MainActivity.this), Locale.getDefault()))
                        ));
                    }
                })
                .setNegativeButton("Cancel", null).create();

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                SimpleDateFormat dateFormat = new SimpleDateFormat(MainActivity.getLocalDatePattern(MainActivity.this), Locale.getDefault());
                etDate.setText(dateFormat.format(calendar.getTime()));
            }
        };

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(MainActivity.this, dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dialog.show();
    }

    private void appendShoppingList(ShoppingList l) {

        l.setId(this.shoppingListMapper.insert(l));
        this.shoppingLists.add(l);

        this.shoppingListRecyclerView.getAdapter().notifyItemInserted(
                this.shoppingLists.size() - 1
        );
    }

    public List<ShoppingList> getShoppingLists() {

        return this.shoppingLists;
    }
}