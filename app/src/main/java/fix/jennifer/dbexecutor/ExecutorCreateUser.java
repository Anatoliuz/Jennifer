package fix.jennifer.dbexecutor;

import android.util.Log;
import fix.jennifer.config.HelperFactory;
import fix.jennifer.userdatadao.User;

import java.sql.SQLException;


public class ExecutorCreateUser implements Runnable {
    private String login;
    private String password;
    private String curve_1;


    public ExecutorCreateUser(String login, String password, String curve_1){
        this.login = login;
        this.password = password;
        this.curve_1 = curve_1;

    }

    @Override
    public void run(){

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
            HelperFactory.getHelper().getUserDAO().setNewUsers(newUser);
        }catch (SQLException e){
            Log.e("in create link", e.toString());
        }

    }
}
