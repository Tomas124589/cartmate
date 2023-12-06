package cz.orv0005.cartmate.mappers;

import android.database.sqlite.SQLiteDatabase;

import cz.orv0005.cartmate.SQLiteHelper;

abstract class DatabaseMapper {

    protected final SQLiteHelper helper;

    public DatabaseMapper(SQLiteHelper helper) {
        this.helper = helper;
    }

    public void delete(Long id) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.delete(getTableName(), "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public abstract String getTableName();
}