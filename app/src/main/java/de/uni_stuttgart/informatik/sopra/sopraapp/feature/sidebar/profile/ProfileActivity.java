package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetrieverRegular;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.regex.Pattern;


public class ProfileActivity extends ProfileActivityBindings {

    private static final Pattern emailPattern = Pattern.compile(Constants.EMAIL_REGEX);

    private boolean isChanged = false;
    private MenuItem menuSaveItem;

    @Inject
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setTitle(strProfileAppBarTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_profile_menu, menu);

        menuSaveItem = menu.findItem(R.id.action_save_profile);
        menuSaveItem.getIcon().setAlpha(255 / 4);
        menuSaveItem.setOnMenuItemClickListener(this::onSaveButtonPressed);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                showLogoutConfirmationDialog();
                return true;
            case android.R.id.home:
                onUserWantsToLeave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        onUserWantsToLeave();
    }

    @OnClick(R.id.profile_input_email)
    public void onProfileInputEmailFieldPressed(EditText editText) {
        InputRetrieverRegular retrieverRegular = InputRetriever
                .newRegularInputRetrieverFrom(editText)
                .withTitle(strEmailDialogTitle)
                .build();

        retrieverRegular.show();

        EditText temporaryEditText = retrieverRegular.getTemporaryEditText();
        temporaryEditText.addTextChangedListener((CleanUpTextWatcher) s -> {
            if (isEmailValid(temporaryEditText))
                temporaryEditText.setError(null);
        });
    }

    @Subscribe(sticky = true)
    public void handleLogin(EventsAuthentication.Login event) {
        textViewUserName.setText(event.user.getName());
        textViewUserRole.setText(event.user.getRole().toString());

        editTextEmailField.setText(event.user.getEmail());
        editTextEmailField.addTextChangedListener((CleanUpTextWatcher) s -> {
            if (isEmailValid(editTextEmailField))
                editTextEmailField.setError(null);
            updateMenuSaveButton();
        });
    }

    /**
     * Invoked when user hits the enabled save button in the menu bar.
     *
     * @param __ the menu item
     * @return true if action event consumed in this activity, false else
     */
    private boolean onSaveButtonPressed(MenuItem __) {

        if (isChanged)
            saveUserNow();

        return true;
    }

    /**
     * Invoked when user hits back button or when user hits the back button in the toolbar.
     */
    private void onUserWantsToLeave() {

        if (hasUserChangedSomething())
            showLeaveWithoutSaveDialog();
        else
            super.onBackPressed();
    }

    /**
     * Returns whether the current logged in user has changed
     *
     * @return true if changed, false else
     */
    private boolean hasUserChangedSomething() {
        try {
            return !userManager.getCurrentUser().getEmail().equals(editTextEmailField.getText().toString());
        } catch (UserManager.NoUserException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Will update the state of the menu save button.
     * Will be enabled if user has changed and if changed value is valid.
     */
    private void updateMenuSaveButton() {
        isChanged = hasUserChangedSomething() && isEmailValid(editTextEmailField);
        menuSaveItem.getIcon().setAlpha(255 / (isChanged ? 1 : 4));
        menuSaveItem.setEnabled(isChanged);
    }

    /**
     * Checks whether the value of the given editText is valid
     *
     * @param editText the field to check
     * @return true if valid, false else
     */
    private boolean isEmailValid(EditText editText) {

        String emailText = editText.getText().toString();

        if (emailText.isEmpty())
            editText.setError("Field is empty!");
        else if (!emailPattern.matcher(emailText).matches())
            editText.setError("No valid Email address!");
        else
            return true;

        editText.requestFocus();
        return false;
    }

    /**
     * Will save the actual changed user.
     */
    private void saveUserNow() {
        try {
            userManager.getCurrentUser().setEmail(editTextEmailField.getText().toString());
            updateMenuSaveButton();
        } catch (UserManager.NoUserException e) {
            e.printStackTrace();
        }
    }

    private void showLogoutConfirmationDialog() {
        new FixedDialog(ProfileActivity.this)
                .setTitle(strLogoutDialogTitle)
                .setMessage(isChanged ? strLogoutDialogMessageOnChange : strLogoutDialogMessage)
                .setPositiveButton(strLogoutDialogConfirmYes, (dialog, id) -> userManager.logout())
                .setNegativeButton(strLogoutDialogConfirmNo, (dialog, id) -> { /* Ignore */ })
                .create()
                .show();
    }

    private void showLeaveWithoutSaveDialog() {
        new FixedDialog(ProfileActivity.this)
                .setTitle(strLeaveDialogTitle)
                .setMessage(strLeaveDialogMessage)
                .setPositiveButton(strLogoutDialogConfirmYes, (dialog, id) -> super.onBackPressed())
                .setNegativeButton(strLogoutDialogConfirmNo, (dialog, id) -> { /* Ignore */ })
                .create()
                .show();
    }

}