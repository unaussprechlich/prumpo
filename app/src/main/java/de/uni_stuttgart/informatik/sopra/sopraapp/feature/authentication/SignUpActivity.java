package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserRepository;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private EditText mEmailView;
    private EditText mNameFirstView;
    private EditText mNameLastView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private User.EnumUserRoles userRole;

    private View mProgressView;
    private View mSignupFormView;

    @Inject
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the login form.
        mNameFirstView = findViewById(R.id.su_name_first);
        mNameLastView = findViewById(R.id.su_name_last);
        mEmailView = findViewById(R.id.su_email);
        mPasswordView = findViewById(R.id.su_password);
        mPasswordConfirmView = findViewById(R.id.su_password_confirm);

        //Spinner
        Spinner spinner = findViewById(R.id.su_usergroup_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Button
        findViewById(R.id.su_signup_button).setOnClickListener(this::signUp);

        mSignupFormView = findViewById(R.id.su_form_layout);
        mProgressView = findViewById(R.id.su_progress);
    }



    private boolean signUp(View v) {
        try{
            showProgress(true);
            final String nameFirst = getFieldValueIfNotEmpty(mNameFirstView);
            final String nameLast = getFieldValueIfNotEmpty(mNameLastView);
            final String email = getFieldValueIfNotEmpty(mEmailView);
            final String password = getFieldValueIfNotEmpty(mPasswordView);
            final String passwordConfirm = getFieldValueIfNotEmpty(mPasswordConfirmView);

            if(!password.equals(passwordConfirm))
                throw new SignUpValueException(mPasswordConfirmView, "Passwords do not match!");

            if(!Pattern.matches(EMAIL_REGEX, email))
                throw new SignUpValueException(mEmailView, "Invalid mail address!");

            //TODO check if user exists

            User user = new User.Builder()
                    .setEmail(email)
                    .setPassword(password)
                    .setName(nameFirst + " " + nameLast)
                    .setRole(userRole)
                    .build();

            userRepository.insert(user);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } catch(SignUpValueException e){
            e.showError();
            showProgress(false);
            return false;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            showProgress(false);
            return false;
        }

        return true;
    }

    private class SignUpValueIsEmptyEsception extends SignUpValueException{
        public SignUpValueIsEmptyEsception(EditText editText) {
            super(editText, "Field is empty!");
        }
    }

    private class SignUpValueException extends Exception{

        public void showError(){
            editText.setError(getMessage());
            editText.requestFocus();
        }

        final EditText editText;

        public SignUpValueException(EditText editText, String message) {
            super(message);
            this.editText = editText;
        }
    }

    private String getFieldValueIfNotEmpty(EditText editText) throws SignUpValueIsEmptyEsception{
        String text = editText.getText().toString();
        if(text.isEmpty()) throw new SignUpValueIsEmptyEsception(editText);
        return text;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mSignupFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
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
}

