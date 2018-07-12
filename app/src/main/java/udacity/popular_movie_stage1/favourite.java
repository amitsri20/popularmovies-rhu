package udacity.popular_movie_stage1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class favourite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLiteDatabase.db";

    public static final String TABLE_NAME = "Favourite";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_FIRST_NAME = "Title";
    public static final String COLUMN_LAST_NAME = "IsFavourite";


    public favourite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY ," + COLUMN_FIRST_NAME + " VARCHAR, " + COLUMN_LAST_NAME + " Boolean);";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


    // Adding new userModel
    void addUser(FavouriteModel userModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, userModel.getTitle()); // UserModel Name
        values.put(COLUMN_LAST_NAME, userModel.getIsFavourite()); // UserModel Phone

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting All users
    public List<FavouriteModel> getAllUsers() {
        List<FavouriteModel> userModelList = new ArrayList<FavouriteModel>();
        // Select All Query
        String selectQuery = " SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_LAST_NAME + " = '" + String.valueOf(true) +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FavouriteModel userModel = new FavouriteModel();
                userModel.setId(cursor.getString(0));
                userModel.setTitle(cursor.getString(1));
                userModel.setIsFavourite(cursor.getString(2));
                // Adding userModel to list
                userModelList.add(userModel);
            } while (cursor.moveToNext());
        }

        // return contact list
        return userModelList;
    }

}
