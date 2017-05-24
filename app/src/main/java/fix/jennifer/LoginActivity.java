package fix.jennifer;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.hash.Hashing;
import fix.jennifer.algebra.Operations;
import fix.jennifer.config.HelperFactory;
import fix.jennifer.dbexecutor.ExecutorCreateUser;
import fix.jennifer.executor.DefaultExecutorSupplier;
import fix.jennifer.userdatadao.User;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoginActivity extends AppCompatActivity  {

    private ExecutorCreateUser ExecutorCreateUser;
    private final Executor executor = Executors.newCachedThreadPool();

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private boolean isAuthCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperFactory.setHelper(getApplicationContext());

        isAuthCompleted = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (EditText)findViewById(R.id.email);

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
        auth(email, password);
        onPostExecute(email,password);
    }


    public void onPostExecute(final String email, final String password){
        try {
            List<User> users = HelperFactory.getHelper().getUserDAO().getAllUsers();
            if (isAuthCompleted) {
                finish();
                Intent mainIntent = new Intent(LoginActivity.this, FileManagerActivity.class);
                LoginActivity.this.startActivity(mainIntent);
            } else if (getUserByLogin(users, email) == null) {
                {
                    mPasswordView.setError(getString(R.string.registered));
                    mPasswordView.requestFocus();
                }
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        } catch (SQLException e){
            Log.e("in login attempt link", e.toString());
        }
    }

        public void auth(final String email, final String password ) {

            Future future = DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                    .submit(new Runnable()  {
                        @Override
                        public void run() {
                            User user;
                            try {
                                List<User> users = HelperFactory.getHelper().getUserDAO().getAllUsers();
                                boolean isHere = isUserInDb(users, email);
                                if (isHere) {
                                     user = getUserByLogin(users, email);
                                    String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                            Settings.Secure.ANDROID_ID);
                                    String passToBeHashed = password +  android_id;
                                    final String hashed = Hashing.sha256()
                                            .hashString(passToBeHashed, StandardCharsets.UTF_8)
                                            .toString();
                                    if (user.getPassword().equals(hashed)) {
                                        HelperFactory.getHelper().setUserId(user.getmId());
                                        isAuthCompleted = true;
                                         HelperFactory.getHelper().generateCurve("id");
                                         HelperFactory.getHelper().setSecretKey(new BigInteger(user.getCurve_1() ) );

                                    }
                                } else {

                                    String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                            Settings.Secure.ANDROID_ID);
                                    String passToBeHashed = password +  android_id;
                                    final String hashed = Hashing.sha256()
                                            .hashString(passToBeHashed, StandardCharsets.UTF_8)
                                            .toString();
                                    HelperFactory.getHelper().generateCurve("id");
                                    BigInteger secretKey = Operations.getSecretKey();

                                    createUserInDb(email, hashed, secretKey.toString(), "b");
                                    user = getUserByLogin(users, email);
                                    HelperFactory.getHelper().setUserId(user.getmId());
                                    HelperFactory.getHelper().setSecretKey(secretKey );

                                    isAuthCompleted = true;

                                }
                            } catch (SQLException e) {
                                Log.e("in login attempt link", e.toString());
                            }
                        }
                    });
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            future.cancel(true);


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
        ExecutorCreateUser = new ExecutorCreateUser(login, password, curve_1, curve_2);
        executor.execute(ExecutorCreateUser);
    }
}

