package cz.orv0005.cartmate.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import cz.orv0005.cartmate.SQLiteHelper;
import cz.orv0005.cartmate.models.ShoppingListItem;

public class ShoppingListItemMapper extends DatabaseMapper {

    public ShoppingListItemMapper(SQLiteHelper helper) {
        super(helper);
    }

    @Override
    public String getTableName() {
        return "list_item";
    }

    public long save(ShoppingListItem item) throws SQLiteException {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id_list", item.getIdList());
        values.put("id_item", item.getIdItem());
        values.put("name", item.getName());
        values.put("count", item.getCount());
        values.put("count_to_buy", item.getCountToBuy());

        long id = item.getId();
        if (id == 0) {
            id = db.insertOrThrow(this.getTableName(), null, values);

        } else {
            db.update(
                    this.getTableName(),
                    values,
                    "id=?",
                    new String[]{String.valueOf(item.getId())}
            );
        }

        db.close();

        return id;
    }

    public List<ShoppingListItem> fetchAllForList(Long idList) throws SQLiteException {

        List<ShoppingListItem> result = new ArrayList<>();

        SQLiteDatabase db = this.helper.getReadableDatabase();

        Cursor c = db.query(
                this.getTableName(),
                null,
                "id_list = ?",
                new String[]{idList.toString()},
                null,
                null,
                null
        );

        while (c.moveToNext()) {

            ShoppingListItem i = new ShoppingListItem(
                    c.getLong(c.getColumnIndexOrThrow("id_list")),
                    c.getLong(c.getColumnIndexOrThrow("id_item")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getInt(c.getColumnIndexOrThrow("count")),
                    c.getInt(c.getColumnIndexOrThrow("count_to_buy"))
            );

            i.setId(c.getLong(c.getColumnIndexOrThrow("id")));

            result.add(i);
        }

        c.close();
        db.close();

        return result;
    }

}
