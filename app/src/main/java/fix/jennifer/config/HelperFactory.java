package fix.jennifer.config;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by fix on 16.04.17.
 */
public class HelperFactory {
    private static DataBaseHelper dataBaseHelper;

    public static DataBaseHelper getHelper(){
        return dataBaseHelper;
    }

    public static void setHelper(Context context){
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
    }

    public void releaseHelper(){
        OpenHelperManager.releaseHelper();
        dataBaseHelper = null;
    }
}
