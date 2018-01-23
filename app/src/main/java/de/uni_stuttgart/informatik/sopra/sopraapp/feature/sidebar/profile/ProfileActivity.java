package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.*;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserHandler;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetrieverRegular;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Profile activity. The user is able to change email, password and profile image.
 * Logout also is supported.
 */
public class ProfileActivity extends ProfileActivityBindings {

    /** User handler to obtain the current logged in user */
    @Inject UserHandler userHandler;

    /** The menu item for saving the changed profile */
    private MenuItem menuSaveItem;

    /** Email pattern compiled only once for performance reasons */
    private static final Pattern PATTERN_EMAIL = Pattern.compile(Constants.EMAIL_REGEX);

    /** List to keep ImageViews to improve performance on reentry of this activity */
    private static List<ImageView> imageList;

    /** Text watcher for each input text field to invoke actions on change */
    private CustomEditTextWatcher textWatcher = s -> onUserChangedSomething();

    /** Index of the selected image of the image dialog */
    private int lastSelectedImagePosition = 0;

    /** Variable used as a blocker. */
    private boolean loggedIn = false;


    // ### Lifecycle ##################################################################################################

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setTitle(strProfileAppBarTitle);

        new LoadProfileImagesTask(new WeakReference<>(getApplicationContext()))
                .execute(Constants.PROFILE_IMAGE_RESOURCES);
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
    protected void onPause() {
        super.onPause();

        editTextEmailField.removeTextChangedListener(textWatcher);
        editTextPassword.removeTextChangedListener(textWatcher);
        editTextPasswordConfirm.removeTextChangedListener(textWatcher);
    }

    @Override
    public void onBackPressed() {
        onUserWantsToLeave();
    }

    @Subscribe(sticky = true)
    public void handleLogin(EventsAuthentication.Login event) {
        if (loggedIn) return;

        loggedIn = true;

        try {
            User currentUser = userHandler.getCurrentUser();
            textViewUserName.setText(currentUser.toString());
            textViewUserRole.setText(currentUser.getRole().toString());

            lastSelectedImagePosition = currentUser.getProfilePicture();
            imageViewProfilePicture.setImageResource(Constants.PROFILE_IMAGE_RESOURCES[lastSelectedImagePosition]);
            editTextEmailField.setText(currentUser.getEmail());

            editTextEmailField.addTextChangedListener(textWatcher);
            editTextPassword.addTextChangedListener(textWatcher);
            editTextPasswordConfirm.addTextChangedListener(textWatcher);
        } catch (NoUserException e) {
            e.printStackTrace();
        }

    }


    // ### Onclick listener ###########################################################################################

    @OnClick(R.id.user_profile_photo)
    public void onProfileImagePressed(ImageButton imageButton) {
        if (imageList == null) return;

        /* To dodge lambda final issue. This variable is used to keep a reference of the dialog */
        final AlertDialog[] imageListDialog = {null};

        // Grid adapter
        ProfileImageGridViewAdapter adapter = new ProfileImageGridViewAdapter(imageList);
        adapter.setOnImageSelected((drawable, position) -> {
            imageViewProfilePicture.setImageDrawable(drawable);
            lastSelectedImagePosition = position;
            onUserChangedSomething();

            if (imageListDialog[0] != null)
                imageListDialog[0].dismiss();
        });

        // Inflate Dialog: layout, view, adapter & listener
        LayoutInflater layoutInflater = getLayoutInflater();
        View inflate = layoutInflater.inflate(R.layout.activity_profile_input_img_dialog, null);
        GridView gridView = inflate.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(adapter);

        // Set layout and create
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(strChangeProfileImageHeader);
        builder.setView(inflate);
        imageListDialog[0] = builder.create();
        imageListDialog[0].show();
    }

    @OnClick(R.id.profile_input_email)
    public void onProfileInputEmailFieldPressed(EditText editText) {
        InputRetrieverRegular retrieverRegular = InputRetriever
                .newRegularInputRetrieverFrom(editText)
                .withTitle(strEmailDialogTitle)
                .build();

        retrieverRegular.show();

        EditText temporaryEditText = retrieverRegular.getTemporaryEditText();
        temporaryEditText.addTextChangedListener((CustomEditTextWatcher) s -> {
            if (isEmailValid(temporaryEditText))
                temporaryEditText.setError(null);
        });
    }

    @OnClick(R.id.profile_input_password)
    public void onPasswordInputFieldPressed(EditText editText) {
        InputRetriever.newPasswordRetrieverFrom(editText)
                .withTitle(strPasswordDialogHeader)
                .build()
                .show();
    }

    @OnClick(R.id.profile_input_password_confirm)
    public void onPasswordConfirmInputFieldPressed(EditText editText) {
        InputRetriever.newPasswordRetrieverFrom(editText)
                .withTitle(strPasswordDialogConfirmHeader)
                .build()
                .show();
    }

    // ### Helper #####################################################################################################

    /**
     * Invoked when user hits the save button in the menu bar
     * (This method should only be invoked when the save button is enabled).
     *
     * @param __ the menu item
     * @return true if action event consumed in this activity, false else
     */
    private boolean onSaveButtonPressed(MenuItem __) {
        saveUserNow();
        onUserChangedSomething();
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
     * Checks whether the value of the given editText is valid
     *
     * @param editText the field to check
     * @return true if valid, false else
     */
    private boolean isEmailValid(EditText editText) {

        String emailText = editText.getText().toString();

        if (emailText.isEmpty())
            editText.setError("Field is empty!");
        else if (!PATTERN_EMAIL.matcher(emailText).matches())
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

        boolean successful = true;

        try {

            User currentUser = userHandler.getCurrentUser();

            currentUser.setEmail(editTextEmailField.getText().toString());
            currentUser.setProfilePicture(lastSelectedImagePosition);

            String newPassword = editTextPassword.getText().toString();
            if (!newPassword.isEmpty())
                currentUser.setPassword(newPassword);

            currentUser.save();

            editTextPassword.getText().clear();
            editTextPasswordConfirm.getText().clear();

        } catch (NoUserException | InterruptedException | ExecutionException e) {
            successful = false;
            e.printStackTrace();
        }

        String message = successful ? strSavedSuccessfully : strSavedNotSuccessfully;
        Snackbar.make(imageViewProfilePicture, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Will display the logout confirm dialog.
     */
    private void showLogoutConfirmationDialog() {
        new FixedDialog(ProfileActivity.this)
                .setTitle(strLogoutDialogTitle)
                .setMessage(hasUserChangedSomething() ? strLogoutDialogMessageOnChange : strLogoutDialogMessage)
                .setPositiveButton(strLogoutDialogConfirmYes, (dialog, id) -> userHandler.logout())
                .setNegativeButton(strLogoutDialogConfirmNo, (dialog, id) -> { /* Ignore */ })
                .create()
                .show();
    }

    /**
     * Will display the dialog hinting the user that his changes will be abandoned.
     */
    private void showLeaveWithoutSaveDialog() {
        new FixedDialog(ProfileActivity.this)
                .setTitle(strLeaveDialogTitle)
                .setMessage(strLeaveDialogMessage)
                .setPositiveButton(strLogoutDialogConfirmYes, (dialog, id) -> super.onBackPressed())
                .setNegativeButton(strLogoutDialogConfirmNo, (dialog, id) -> { /* Ignore */ })
                .create()
                .show();
    }


    // ### Validation logic for the menu save button ##################################################################

    /**
     * Always call this method if relevant user information is changed in the UI.
     * <p>
     * This method will turn on the save button in the menu bar, if
     * <ul>
     * <li>
     * any user info in the ui does not match the user info in the data base
     * </li>
     * <li>
     * all user info (in the input fields) is valid
     * </li>
     * </ul>
     */
    private void onUserChangedSomething() {
        editTextEmailField.setError(null);
        editTextPasswordConfirm.setError(null);

        boolean enableSaveButton =
                hasUserChangedSomething() && isCurrentStateAllowed();

        menuSaveItem.getIcon().setAlpha(255 / (enableSaveButton ? 1 : 4));
        menuSaveItem.setEnabled(enableSaveButton);

    }

    /**
     * Checks whether user has changed something.
     *
     * @return true, if user changed something
     */
    private boolean hasUserChangedSomething() {

        try {
            User currentUser = userHandler.getCurrentUser();

            return !currentUser.getEmail().equals(editTextEmailField.getText().toString())
                    || currentUser.getProfilePicture() != lastSelectedImagePosition
                    || !editTextPassword.getText().toString().isEmpty()
                    || !editTextPasswordConfirm.getText().toString().isEmpty();

        } catch (NoUserException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks whether the current state of the input fields is valid to save.
     * Will show errors on the fields which are not valid.
     *
     * @return true if all data from the input fields can be saved into the data base.
     */
    private boolean isCurrentStateAllowed() {
        boolean isConfirmPwValid = editTextPassword.getText().toString()
                .equals(editTextPasswordConfirm.getText().toString());

        if (!isConfirmPwValid) editTextPasswordConfirm.setError("");

        return isEmailValid(editTextEmailField) && isConfirmPwValid;
    }

    /**
     * Task for loading profile images asynchronously.
     * Static class in order to prevent memory leaks.
     */
    private static class LoadProfileImagesTask extends AsyncTask<Integer, Void, List<ImageView>> {

        /** Using a weak reference to prevent memory leaks. */
        private WeakReference<Context> context;

        LoadProfileImagesTask(WeakReference<Context> context) {
            this.context = context;
        }

        @Override
        protected List<ImageView> doInBackground(Integer... integers) {
            if (imageList != null) return imageList;

            return Arrays.stream(integers)
                    .map(this::mapToImageView)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        @Override
        protected void onPostExecute(List<ImageView> imageViews) {
            imageList = imageViews;
            context = null;
        }

        /**
         * Will transform a image layout to an imageView.
         *
         * @param layoutID the id
         * @return the imageView found by the given id
         */
        private ImageView mapToImageView(int layoutID) {
            Context context = this.context.get();

            if (context == null)
                return null;

            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(15, 15, 15, 15);
            imageView.setImageResource(layoutID);
            return imageView;
        }
    }
}