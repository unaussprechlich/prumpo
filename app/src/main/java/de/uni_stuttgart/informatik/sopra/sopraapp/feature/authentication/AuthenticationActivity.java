package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AuthenticationActivity extends BaseActivity  implements AdapterView.OnItemSelectedListener {

    @Inject UserRepository userRepository;
    @Inject UserManager userManager;

    @BindView(R.id.su_email)        EditText signUpEmail;
    @BindView(R.id.su_name_first)   EditText signUpFirstName;
    @BindView(R.id.su_name_last)    EditText signUpLastName;
    @BindView(R.id.su_password)     EditText signUpPassword;
    @BindView(R.id.su_password_confirm) EditText signUpPasswordConfirm;

    private User.EnumUserRoles userRole;

    @BindView(R.id.login_email)   EditText loginEmail;
    @BindView(R.id.login_password)EditText loginPassword;

    @BindView(R.id.activity_authentication_create_new_account) TextView createNewAccount;
    @BindView(R.id.activity_authentication_back_to_login) TextView backToLogin;

    @BindView(R.id.login_progress) View progressView;
    @BindView(R.id.login_layout)   View loginView;
    @BindView(R.id.signup_layout)   View signupView;

    @BindString(R.string.activity_authenticate_create_new_account) String createNewAccountString;
    @BindString(R.string.activity_authenticate_back_to_login) String backToLoginString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);

        createNewAccount.setText(Html.fromHtml(createNewAccountString), TextView.BufferType.SPANNABLE);
        backToLogin.setText(Html.fromHtml(backToLoginString), TextView.BufferType.SPANNABLE);

        //Spinner
        Spinner spinner = findViewById(R.id.su_usergroup_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        userRepository.insertDummyIfNotExist();
    }

    @OnClick(R.id.activity_authentication_demo_modus)
    public void onDemoModus(){
        LiveData<User> user = userRepository.getByEmail("dummy@dummy.net");
        user.observe(this, user1 -> {
            if(user1 == null) return;
            Log.e("TEST", user1.toString());
            userManager.login(user);
            gotoMainActivity();
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.login_button)
    void attemptLogin() {
        try {
            showProgress(true, loginView);
            View view = this.getCurrentFocus();

            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // reset errors
            loginEmail.setError(null);
            loginPassword.setError(null);

            final String email = getFieldValueIfNotEmpty(loginEmail);
            final String password = getFieldValueIfNotEmpty(loginPassword);

            if (!Pattern.matches(Constants.EMAIL_REGEX, email))
                throw new EditFieldValueException(loginEmail, "Invalid mail address!");

            LiveData<User> liveUser = userRepository.getByEmail(email);
            liveUser.observe(this, user -> {
                try {

                    if (user == null) throw new EditFieldValueException(loginEmail, "User not found!");

                    else {
                        if (user.getPassword().equals(password)) {
                            userManager.login(liveUser);
                            gotoMainActivity();

                        } else throw new EditFieldValueException(loginPassword, "Password Incorrect!");
                    }

                } catch (EditFieldValueException e) {
                    e.showError();
                    showProgress(false, loginView);
                }
            });
        } catch (EditFieldValueException e) {
            e.showError();
            showProgress(false, loginView);
        }
    }

    @OnClick(R.id.su_signup_button)
    void signUp() {
        try{
            showProgress(true, signupView);

            final String nameFirst          = getFieldValueIfNotEmpty(signUpFirstName);
            final String nameLast           = getFieldValueIfNotEmpty(signUpLastName);
            final String email              = getFieldValueIfNotEmpty(signUpEmail);
            final String password           = getFieldValueIfNotEmpty(signUpPassword);
            final String passwordConfirm    = getFieldValueIfNotEmpty(signUpPasswordConfirm);

            if(!password.equals(passwordConfirm))
                throw new EditFieldValueException(signUpPasswordConfirm, "Passwords do not match!");

            if(!Pattern.matches(Constants.EMAIL_REGEX, email))
                throw new EditFieldValueException(signUpEmail, "Invalid mail address!");

            if(userRole == User.EnumUserRoles.NULL)
                throw new IllegalArgumentException("Keine Benutzerrolle ausgewählt!");

            //TODO check if user exists

            User user = new User.Builder()
                    .setEmail(email)
                    .setPassword(password)
                    .setName(nameFirst + " " + nameLast)
                    .setRole(userRole)
                    .build();

            userRepository.insert(user);

            onClickToLogin();
        } catch(EditFieldValueException e) {
            e.showError();
        } catch (IllegalArgumentException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            showProgress(false, signupView);
        }
    }

    private void gotoMainActivity(){
        Intent myIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(myIntent);
    }

    private String getFieldValueIfNotEmpty(EditText editText) throws EditFieldValueIsEmptyException {
        editText.setError(null);
        String text = editText.getText().toString();
        if (text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
        return text;
    }



    @OnClick(R.id.activity_authentication_back_to_login)
    public void onClickToLogin(){
        animateViewVisibility(false, signupView);
        animateViewVisibility(true, loginView);

        resetEditText(signUpFirstName);
        resetEditText(signUpLastName);
        resetEditText(signUpEmail);
        resetEditText(signUpPassword);
        resetEditText(signUpPasswordConfirm);
    }

    @OnClick(R.id.activity_authentication_create_new_account)
    public void onClickToSignUp(){
        animateViewVisibility(false, loginView);
        animateViewVisibility(true, signupView);

        resetEditText(loginEmail);
        resetEditText(loginPassword);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try{
            userRole = User.EnumUserRoles.valueOf(parent.getItemAtPosition(position).toString());
        } catch(Exception e){
            Log.e("SignUpActivity", "Could not find enum for given role", e);
            userRole = User.EnumUserRoles.NULL;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        this.userRole = User.EnumUserRoles.NULL;
    }

    private void resetEditText(EditText editText){
        editText.setError(null);
        editText.setText("");
    }

    /**
     * Lock the User in place ¯\_(ツ)_/¯
     */
    @Override
    public void onBackPressed() {}

    private void animateViewVisibility(final boolean visibility, View view){
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);

        view.animate()
            .setDuration(300)
            .alpha(visibility ? 1 : 0)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(visibility ? View.VISIBLE : View.GONE);
                }
            });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show, View view) {
        animateViewVisibility(!show, view);
        animateViewVisibility(show, progressView);
    }
}
