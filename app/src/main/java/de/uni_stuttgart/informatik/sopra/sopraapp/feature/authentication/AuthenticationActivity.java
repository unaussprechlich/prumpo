package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Optional;
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
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.exceptions.EditFieldValueException;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.AnimationHelper;

/**
 * The {@link AuthenticationActivity} provides a UI for the user to Login :3
 * The Activity is started by the {@link UserHandler} whenever a {@link de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException}
 * is thrown.
 */
public class AuthenticationActivity extends BaseActivity  implements AdapterView.OnItemSelectedListener {

    @Inject UserRepository userRepository;
    @Inject UserHandler userHandler;

    @BindView(R.id.signup_layout)   View signupView;
    @BindView(R.id.su_email)        EditText signUpEmail;
    @BindView(R.id.su_name_first)   EditText signUpFirstName;
    @BindView(R.id.su_name_last)    EditText signUpLastName;
    @BindView(R.id.su_password)     EditText signUpPassword;
    @BindView(R.id.su_password_confirm) EditText signUpPasswordConfirm;
    @BindView(R.id.signup_progress) View progressViewSignUp;

    @BindView(R.id.login_layout)   View loginView;
    @BindView(R.id.login_email)   EditText loginEmail;
    @BindView(R.id.login_password)EditText loginPassword;
    @BindView(R.id.login_animation) View loginAnimation;
    @BindView(R.id.login_progress) View progressViewLogin;

    @BindView(R.id.activity_authentication_create_new_account) TextView createNewAccount;
    @BindView(R.id.activity_authentication_back_to_login) TextView backToLogin;

    @BindView(R.id.logo_image) ImageView logoImage;
    @BindView(R.id.activity_authentication_demo_modus) View buttonDemoModus;

    @BindString(R.string.activity_authenticate_create_new_account) String createNewAccountString;
    @BindString(R.string.activity_authenticate_back_to_login) String backToLoginString;

    private User.EnumUserRoles userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);

        // toolbar elevation => 0
        Optional.ofNullable(getSupportActionBar())
                .ifPresent(i -> i.setElevation(0));

        //Set the Text from html, to display it in two different colors
        createNewAccount.setText(Html.fromHtml(createNewAccountString), TextView.BufferType.SPANNABLE);
        backToLogin.setText(Html.fromHtml(backToLoginString), TextView.BufferType.SPANNABLE);

        //Spinner
        Spinner spinner = findViewById(R.id.su_usergroup_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Insert the dummy object if it not exists
        userRepository.insertDummyIfNotExist();
    }

    /**
     * Starts the Demo modus with a default user!
     */
    @OnClick(R.id.activity_authentication_demo_modus)
    public void onDemoMode(){
        try {
            User user = userRepository.getByEmailAsync("dummy@dummy.net");
            if(user == null) return;
            userHandler.login(user);
            gotoMainActivity();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to sign in and show some fancy animations below the login button!
     *
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.login_button)
    void attemptLogin() {

            View view = this.getCurrentFocus();

            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // reset errors
            loginEmail.setError(null);
            loginPassword.setError(null);

            AnimationHelper.showProgress(progressViewLogin, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    try {

                        final String email = getFieldValueIfNotEmpty(loginEmail);
                        final String password = getFieldValueIfNotEmpty(loginPassword);

                        if (!Pattern.matches(Constants.EMAIL_REGEX, email))
                            throw new EditFieldValueException(loginEmail, "Invalid mail address!");

                        User user = userRepository.getByEmailAsync(email);
                        if (user == null) throw new EditFieldValueException(loginEmail, "User not found!");

                        if (!user.getPassword().equals(password))
                            throw new EditFieldValueException(loginPassword, "Password Incorrect!");

                        userHandler.login(user);
                        gotoMainActivity();

                    } catch (EditFieldValueException e) {
                        e.showError();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        progressViewLogin.setVisibility(View.GONE);
                    }
                }
            });

    }

    /**
     * Attempts to sign up and show some fancy animations below the signup button!
     *
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.su_signup_button)
    void attemptSignUp() {
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        signUpFirstName.setError(null);
        signUpLastName.setError(null);
        signUpEmail.setError(null);
        signUpPassword.setError(null);
        signUpPasswordConfirm.setError(null);

        AnimationHelper.showProgress(progressViewSignUp, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try{
                    final String nameFirst          = getFieldValueIfNotEmpty(signUpFirstName);
                    final String nameLast           = getFieldValueIfNotEmpty(signUpLastName);
                    final String email              = getFieldValueIfNotEmpty(signUpEmail);
                    final String password           = getFieldValueIfNotEmpty(signUpPassword);
                    final String passwordConfirm    = getFieldValueIfNotEmpty(signUpPasswordConfirm);

                    if(!password.equals(passwordConfirm))
                        throw new EditFieldValueException(signUpPasswordConfirm, "Die Passwörter sind nicht identisch!");

                    if(!Pattern.matches(Constants.EMAIL_REGEX, email))
                        throw new EditFieldValueException(signUpEmail, "Das ist keine valida Email-Adresse!");

                    if(userRole == User.EnumUserRoles.NULL)
                        throw new IllegalArgumentException("Keine Benutzerrolle ausgewählt!");


                    User userAsync = userRepository.getByEmailAsync(email);

                    if(userAsync != null && userAsync.getEmail().equals(email))
                        throw new EditFieldValueException(signUpEmail, "Ein Benutzer mit dieser Email-Adresse existiert bereits!");

                    User user = new User.Builder()
                            .setEmail(email)
                            .setPassword(password)
                            .setName(nameFirst + " " + nameLast)
                            .setRole(userRole)
                            .create();

                    userRepository.insert(user);
                    onClickToLogin();
                } catch(EditFieldValueException e) {
                    e.showError();
                } catch (IllegalArgumentException e){
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Something went wrong:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    progressViewSignUp.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    /**
     * Starts some fancy animations and finally leaves the {@link AuthenticationActivity}
     * to start the actual {@link MainActivity}
     */
    private void gotoMainActivity(){
        loginAnimation.setVisibility(View.VISIBLE);
        loginAnimation.animate().alpha(1).setDuration(100).setStartDelay(500).start();

        AnimationHelper.slideOfBottom(buttonDemoModus);
        AnimationHelper.slideOfTop(logoImage);
        AnimationHelper.slideOfBottom(loginView, () ->{
            //YEAH, FAKE LOADING TIME
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        });
    }

    /**
     * Switches from SignUp to Login
     */
    @OnClick(R.id.activity_authentication_back_to_login)
    public void onClickToLogin(){
        resetEditText(signUpFirstName);
        resetEditText(signUpLastName);
        resetEditText(signUpEmail);
        resetEditText(signUpPassword);
        resetEditText(signUpPasswordConfirm);

        AnimationHelper.viewVisibilityHide(true, logoImage);
        AnimationHelper.viewVisibilityHide(true, buttonDemoModus);
        AnimationHelper.viewVisibilityHide(false, signupView);
        AnimationHelper.viewVisibilityHide(true, loginView);
    }

    /**
     * Switches from Login to SignUp
     */
    @OnClick(R.id.activity_authentication_create_new_account)
    public void onClickToSignUp(){
        resetEditText(loginEmail);
        resetEditText(loginPassword);

        AnimationHelper.viewVisibilityHide(false, logoImage);
        AnimationHelper.viewVisibilityHide(false, buttonDemoModus);
        AnimationHelper.viewVisibilityHide(false, loginView);
        AnimationHelper.viewVisibilityHide(true, signupView);
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     *
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try{
            userRole = User.EnumUserRoles.valueOf(parent.getItemAtPosition(position).toString());
        } catch(Exception e){
            Log.e("SignUpActivity", "Could not find enum for given role", e);
            userRole = User.EnumUserRoles.NULL;
        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        this.userRole = User.EnumUserRoles.NULL;
    }

    /**
     * Lock the User in place ¯\_(ツ)_/¯
     */
    @Override
    public void onBackPressed() {}


}