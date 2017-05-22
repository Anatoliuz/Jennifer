package fix.jennifer.config;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class HelperFactory {


    private static DataBaseHelper dataBaseHelper;

    public static DataBaseHelper getHelper(){
        return dataBaseHelper;
    }

    public static void setHelper(Context context){
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
    }

    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        dataBaseHelper = null;
    }


}
