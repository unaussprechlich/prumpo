package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserRepository;

// TODO: deal with lags (and maybe introduce separate threads)
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Inject
    UserRepository userRepository;

    @Inject
    UserManager userManager;

    @Inject
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO remove dummy
        try {
            userRepository.insert(new User.Builder()
                    .setEmail("dummy@dummy.net")
                    .setPassword("dummy")
                    .setName("Mister Dummy")
                    .setRole(User.EnumUserRoles.ADMIN)
                    .build());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        findViewById(R.id.SKIP_LOGIN).setOnClickListener(v ->{

            LiveData<User> user = userRepository.getByEmail("dummy@dummy.net");
            user.observe(this, user1 ->
                    userManager.login(user)
            );
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(myIntent);
        });

        Button mEmailSignInButton = findViewById(R.id.sign_up_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        try{
            showProgress(true);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // reset errors
            mEmailView.setError(null);
            mPasswordView.setError(null);

            final String email = getFieldValueIfNotEmpty(mEmailView);
            final String password = getFieldValueIfNotEmpty(mPasswordView);

            if(!Pattern.matches(Constants.EMAIL_REGEX, email))
                throw new LogInValueException(mEmailView, "Invalid mail address!");


            LiveData<User> liveUser = userRepository.getByEmail(email);
            liveUser.observe(this, user -> {
                try{
                    if(user == null)
                        throw new LogInValueException(mEmailView, "User not found!");
                    else if(user.password.equals(password)){
                        userManager.login(liveUser);
                        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(myIntent);
                    } else if(!user.password.equals(password))
                        throw new LogInValueException(mPasswordView, "Password Incorrect!");
                     else
                        throw new Exception("Something went wrong!");

                } catch (LogInValueException e){
                    e.showError();
                    showProgress(false);
                } catch (Exception e){
                    Log.d("LoginActivity", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
        }catch(LogInValueException e){
            e.showError();
            showProgress(false);
        }
    }

    private class LogInValueIsEmptyException extends LogInValueException {
        public LogInValueIsEmptyException(EditText editText) {
            super(editText, "Field is empty!");
        }
    }

    private class LogInValueException extends Exception{

        public void showError(){
            editText.setError(getMessage());
            editText.requestFocus();
            vibrator.vibrate(1000);
        }

        final EditText editText;

        public LogInValueException(EditText editText, String message) {
            super(message);
            this.editText = editText;
        }
    }

    private String getFieldValueIfNotEmpty(EditText editText) throws LogInValueIsEmptyException {
        String text = editText.getText().toString();
        if(text.isEmpty()) throw new LogInValueIsEmptyException(editText);
        return text;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        // on Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);

        mLoginFormView
                .animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView
                .animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }
}