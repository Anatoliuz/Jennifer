package fix.jennifer.userdatadao;

import android.content.Intent;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by fix on 16.04.17.
 */
public class UserImpl extends BaseDaoImpl<User, Integer> {

    public UserImpl(ConnectionSource connectionSource, Class<User> user) throws SQLException{
        super(connectionSource, user);
    }

    public List<User> getAllUsers() throws SQLException{
        return this.queryForAll();
    }

    public void setNewUsers(User user) throws SQLException{
        this.create(user);
    }

}
