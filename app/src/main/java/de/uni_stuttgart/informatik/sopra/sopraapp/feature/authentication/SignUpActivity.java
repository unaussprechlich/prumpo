package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueException;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueIsEmptyException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.su_email)        EditText mEmailView;
    @BindView(R.id.su_name_first)   EditText mNameFirstView;
    @BindView(R.id.su_name_last)    EditText mNameLastView;
    @BindView(R.id.su_password)     EditText mPasswordView;
    @BindView(R.id.su_password_confirm) EditText mPasswordConfirmView;

    private User.EnumUserRoles userRole;

    @BindView(R.id.su_progress)     View mProgressView;
    @BindView(R.id.su_form_layout)  View mSignupFormView;

    @Inject UserRepository userRepository;
    @Inject Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        //Spinner
        Spinner spinner = findViewById(R.id.su_usergroup_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Button
        findViewById(R.id.su_signup_button).setOnClickListener(this::signUp);
    }

    private boolean signUp(View v) {
        try{
            showProgress(true);

            mNameFirstView.setError(null);
            mNameLastView.setError(null);
            mEmailView.setError(null);
            mPasswordView.setError(null);
            mPasswordConfirmView.setError(null);

            final String nameFirst          = getFieldValueIfNotEmpty(mNameFirstView);
            final String nameLast           = getFieldValueIfNotEmpty(mNameLastView);
            final String email              = getFieldValueIfNotEmpty(mEmailView);
            final String password           = getFieldValueIfNotEmpty(mPasswordView);
            final String passwordConfirm    = getFieldValueIfNotEmpty(mPasswordConfirmView);

            if(!password.equals(passwordConfirm))
                throw new EditFieldValueException(mPasswordConfirmView, "Passwords do not match!");

            if(!Pattern.matches(Constants.EMAIL_REGEX, email))
                throw new EditFieldValueException(mEmailView, "Invalid mail address!");

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

        } catch(EditFieldValueException e){
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

    private String getFieldValueIfNotEmpty(EditText editText) throws EditFieldValueIsEmptyException {
        String text = editText.getText().toString();
        if(text.isEmpty()) throw new EditFieldValueIsEmptyException(editText);
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

