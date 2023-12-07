package cz.orv0005.cartmate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cartmate.db";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static LocalDate str2LocalDate(String dateString) {

        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static String localDate2str(LocalDate date) {

        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.getDefault()));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createListsTable = "CREATE TABLE lists(" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT NOT NULL ," +
                "    store TEXT NULL ," +
                "    date TEXT NULL" +
                ");";

        String createListItemTable = "CREATE TABLE list_item(" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    id_list INT NOT NULL," +
                "    id_item INT NULL ," +
                "    name TEXT NOT NULL ," +
                "    count INT NOT NULL," +
                "    count_to_buy INT NOT NULL," +
                "    FOREIGN KEY (id_list) REFERENCES lists(id)," +
                "    FOREIGN KEY (id_item) REFERENCES item(id)" +
                ");";

        String createItemTable = "CREATE TABLE item(" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT," +
                "    ean INTEGER UNIQUE," +
                "    category TEXT," +
                "    unit TEXT" +
                ");";

        db.execSQL(createListsTable);
        db.execSQL(createListItemTable);
        db.execSQL(createItemTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema changes here
    }
}