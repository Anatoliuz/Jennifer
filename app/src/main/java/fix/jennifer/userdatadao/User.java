package fix.jennifer.userdatadao;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "User")
public class User {

    @DatabaseField(generatedId = true)
    public int mId;

    @DatabaseField(dataType = DataType.STRING)
    private String login;

    @DatabaseField(dataType = DataType.STRING)
    private String password;


    public User(){}

    public int getmId() {
        return mId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
