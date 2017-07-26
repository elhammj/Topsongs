package example.org.topsongs_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    // singleton reference for this class
    // restricts the instantiation of the DatabaseManager class to one object.
    private static DatabaseManager self = null;

    private static String DATABASE_NAME = "topsongdb";

    private static int DATABASE_VERSION = 1;

    /**
    * @param context -  allows access to application-specific resources db,assets
    * @param name - name of the database
    * @param factory - provides random read-write access to the result set returned by a database query.
    * @param version - version of the databse
    * Make the constructor private so that can not be called from outside of this class
    */
    private DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    /**
     * @return a single instance of the DatabaseManager Class
    */
    public static DatabaseManager getInstance()
    {
        if(self == null)
        {
            self = new DatabaseManager(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }
        return self;
    }

    private String TBL_SONG_LIST = "CREATE TABLE top_songs (" + "id INTEGER PRIMARY KEY," +
            "title TEXT," +
            "image_url TEXT," +
            "image_data blob);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_SONG_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * @param topSong - details of songs need to be stored in database (update data of 10 songs)
     * @return - true if insert operation is successfully else false.
     */
    public synchronized boolean saveImageData(TopSong topSong){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        try {
            cv.put("id", topSong.getId());
            cv.put("title", topSong.getSong());
            cv.put("image_url", topSong.getImage());
            // Convert the image into byte array
            if(topSong.getImageData() != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                topSong.getImageData().compress(Bitmap.CompressFormat.PNG, 100, out);
                byte[] buffer = out.toByteArray();
                cv.put("image_data", buffer);
            }
            db.update("top_songs", cv, "id="+topSong.getId(), null);
            } catch (Exception e){
            return false;
        } finally {
            cv.clear();
            if(db != null) {
                db.close();
            }
        }
        return true;
    }


    public synchronized boolean saveSongList(List<TopSong> songsList){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        boolean insert = true;
        Cursor cursor =  null;
        try {
            for(TopSong topSong : songsList) {
                cursor = db.rawQuery("SELECT * FROM top_songs WHERE id=" + topSong.getId(), null);
                while (cursor.moveToNext()) {
                    insert = false;
                }
                if (insert) {
                    cv.put("id", topSong.getId());
                    cv.put("title", topSong.getSong());
                    cv.put("image_url", topSong.getImage());
                    // Convert the image into byte array
                    if (topSong.getImageData() != null) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        topSong.getImageData().compress(Bitmap.CompressFormat.PNG, 100, out);
                        byte[] buffer = out.toByteArray();
                        cv.put("image_data", buffer);
                    }
                    db.insert("top_songs", null, cv);
                }
            }
        } catch (Exception e){
            return false;
        } finally {
            cv.clear();
            if(db != null) {
                db.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return true;
    }


    /**
     * List of songs from database
     * @return list of songs from database
     */
    public synchronized List<TopSong> getTopSongsList(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<TopSong> songList = new ArrayList<TopSong>();
        TopSong song = null;
        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery("SELECT * from top_songs", null);
            while(cursor.moveToNext())
            {
                song = new TopSong();
                song.setId(cursor.getInt(cursor.getColumnIndex("id")));
                song.setSong(cursor.getString(cursor.getColumnIndex("title")));
                song.setImage(cursor.getString(cursor.getColumnIndex("image_url")));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex("image_data"));
                if(blob != null) {
                    song.setImageData(BitmapFactory.decodeByteArray(blob, 0, blob.length));
                }
                songList.add(song);
            }
        }
        catch(Exception e)
        {

        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
            db.close();
        }
        return songList;
    }
}
