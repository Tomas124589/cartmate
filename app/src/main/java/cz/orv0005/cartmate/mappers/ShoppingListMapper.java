package cz.orv0005.cartmate.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.models.ShoppingList;

public class ShoppingListMapper {

    private final SQLiteHelper helper;
    private static final String TABLE = "lists";

    public ShoppingListMapper(SQLiteHelper helper) {

        this.helper = helper;
    }

    public long insert(ShoppingList list) throws SQLiteException {

        SQLiteDatabase db = helper.getWritableDatabase();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());

        ContentValues values = new ContentValues();
        values.put("name", list.getName());
        values.put("store", list.getShopName());
        values.put("date", list.getDate().format(f));

        long id = db.insertOrThrow(TABLE, null, values);
        db.close();

        return id;
    }

    public List<ShoppingList> fetchAll() throws SQLiteException, ParseException {

        List<ShoppingList> result = new ArrayList<>();

        SQLiteDatabase db = this.helper.getReadableDatabase();

        Cursor c = db.query(TABLE, null, null, null, null, null, null);

        while (c.moveToNext()) {

            ShoppingList l = new ShoppingList(
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("store")),
                    c.getString(c.getColumnIndexOrThrow("date"))
            );

            l.setId(c.getLong(c.getColumnIndexOrThrow("id")));

            result.add(l);
        }

        c.close();
        db.close();

        return result;
    }
}
