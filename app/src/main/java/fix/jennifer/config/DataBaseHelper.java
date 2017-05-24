package fix.jennifer.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fix.jennifer.algebra.Operations;
import fix.jennifer.ellipticcurves.EllipticCurve;
import fix.jennifer.userdatadao.User;
import fix.jennifer.userdatadao.UserImpl;

import java.math.BigInteger;
import java.sql.SQLException;

/**
 * Created by fix on 16.04.17.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DataBaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "db.db";

    private static final int DATABASE_VERSION = 8;

    private static int userId;

    private  EllipticCurve curve;

    private  BigInteger secretKey;

    private UserImpl userDAO =  null;

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try{
            TableUtils.createTable(connectionSource, User.class);
            Log.d(TAG, "onCreate call");
        }
        catch(SQLException e){
            Log.e(TAG, "onCreate error");

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer){
        try{
            TableUtils.dropTable(connectionSource, User.class, true);
            onCreate(db, connectionSource);
        }catch (SQLException e){
            Log.e(TAG, "onCreate error");
        }
    }
    public UserImpl getUserDAO() throws SQLException {
        if (userDAO == null){
            userDAO = new UserImpl(getConnectionSource(), User.class);
        }
        return userDAO;
    }

    @Override
    public void close(){
        super.close();
        userDAO = null;
    }
    public  void setUserId(int id){
        userId = id;
    }

    public  int getUserId(){
        return userId;
    }

    public  void generateCurve(String id){
        curve = new EllipticCurve(id);
    }


    public  EllipticCurve getCurve(){
        return curve;
    }

    public  void setSecretKey(BigInteger s){
        secretKey = s;
    }

    public  BigInteger getSecretKey(){
        return secretKey;
    }


}
