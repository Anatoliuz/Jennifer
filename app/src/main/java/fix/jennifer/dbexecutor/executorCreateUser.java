package fix.jennifer.dbexecutor;

import android.util.Log;
import fix.jennifer.config.HelperFactory;
import fix.jennifer.userdatadao.User;

import java.sql.SQLException;

/**
 * Created by fix on 16.04.17.
 */
public class executorCreateUser implements Runnable {
    private String login;
    private String password;
    private String curve_1;
    private String curve_2;

    public executorCreateUser(String login, String password, String curve_1, String curve_2){
        this.login = login;
        this.password = password;
        this.curve_1 = curve_1;
        this.curve_2 = curve_2;
    }

    @Override
    public void run(){
        // sleep is only to show gui not blocking

        try{
            Thread.sleep(1000);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
        try{
            User newUser = new User();
            newUser.setLogin(login);
            newUser.setPassword(password);
            newUser.setCurve_1(curve_1);
            newUser.setCurve_2(curve_2);
            HelperFactory.getHelper().getUserDAO().setNewUsers(newUser);
        }catch (SQLException e){
            Log.e("in create link", e.toString());
        }

    }
}
