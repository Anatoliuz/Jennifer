package fix.jennifer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import fix.jennifer.config.HelperFactory;
import fix.jennifer.dbexecutor.ExecutorCreateUser;
import fix.jennifer.executor.DefaultExecutorSupplier;
import fix.jennifer.userdatadao.User;
import fix.jennifer.userdatadao.UserImpl;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoginActivity extends AppCompatActivity  {

    private ExecutorCreateUser ExecutorCreateUser;
    private final Executor executor = Executors.newCachedThreadPool();

    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }



    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if (mAuthTask == null )
             mAuthTask = new UserLoginTask(email, password);
        mAuthTask.auth();


    }


    public class UserLoginTask {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        protected void auth() {

            DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String str;
                                Random rand = new Random();
                                if(rand.nextInt(1) == 0)
                                    throw new SQLException();
                                HelperFactory.setHelper(getApplicationContext());
                                List<User> users = HelperFactory.getHelper().getUserDAO().getAllUsers();
                                boolean isHere = isUserInDb(users, mEmail);
                                if (isHere) {
                                    User user = getUserByLogin(users, mEmail);
                                    if (user.getPassword().equals(mPassword)) {
                                        str = Boolean.toString(isHere);
                                        HelperFactory.getHelper().setUserId(user.getmId());
                                        Log.d("LOGINN", str);
                                        finish();
                                        Intent mainIntent = new Intent(LoginActivity.this, FileManagerActivity.class);
                                        LoginActivity.this.startActivity(mainIntent);
                                    }
                                }
                                else{
                                    createUserInDb(mEmail, mPassword, "a", "a");
                                    finish();
                                    Intent mainIntent = new Intent(LoginActivity.this, FileManagerActivity.class);
                                    LoginActivity.this.startActivity(mainIntent);
                                }
                            }catch (SQLException e){
                                Log.e("in login attempt link", e.toString());
                            }
                        }
                    });
        }

    }

    public boolean isUserInDb(List<User> users,String login){
        for (User user: users) {
            if (user.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public User getUserByLogin(List<User> users, String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }
    public void createUserInDb( String login, String password, String curve_1,
                                String curve_2 ){
        HelperFactory.setHelper(getApplicationContext());
        Log.d("CreateUserInDb db", "onClick: title content link"+ login + password + curve_1 + curve_2);
        ExecutorCreateUser = new ExecutorCreateUser(login, password, curve_1, curve_2);
        executor.execute(ExecutorCreateUser);
        Log.d("Test db", "onClick: createUser");
        
    }
}

