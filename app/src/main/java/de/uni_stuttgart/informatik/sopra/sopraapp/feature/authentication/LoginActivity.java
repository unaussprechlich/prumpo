package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    @Inject UserRepository userRepository;
    @Inject UserManager userManager;
    @Inject Vibrator vibrator;

    @BindView(R.id.email)       EditText mEmailView;
    @BindView(R.id.password)    EditText mPasswordView;
    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.login_form)  View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    @OnClick(R.id.SKIP_LOGIN)
    public void onClickSkipLogin(){
        LiveData<User> user = userRepository.getByEmail("dummy@dummy.net");
        user.observe(this, user1 -> userManager.login(user));
        gotoMainActivity();
    }

    @OnClick(R.id.sign_up_button)
    public void onClickSignUpButton(){
        attemptLogin();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        try {
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

            if (!Pattern.matches(Constants.EMAIL_REGEX, email))
                throw new EditFieldValueException(mEmailView, "Invalid mail address!");

            LiveData<User> liveUser = userRepository.getByEmail(email);
            liveUser.observe(this, user -> {
                try {

                    if (user == null) throw new EditFieldValueException(mEmailView, "User not found!");
                    else {
                        if (user.getPassword().equals(password)) {
                            userManager.login(liveUser);
                            gotoMainActivity();
                        } else throw new EditFieldValueException(mPasswordView, "Password Incorrect!");
                    }

                } catch (EditFieldValueException e) {
                    e.showError();
                    showProgress(false);
                }
            });
        } catch (EditFieldValueException e) {
            e.showError();
            showProgress(false);
        }
    }

    private void gotoMainActivity(){
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(myIntent);
    }

    private String getFieldValueIfNotEmpty(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

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