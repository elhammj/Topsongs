package example.org.topsongs_db;

import android.app.Application;
import android.content.Context;

/**
 * Created by Hem on 3/29/2016.
 */
public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getAppContext(){
        return mContext;
    }

}
