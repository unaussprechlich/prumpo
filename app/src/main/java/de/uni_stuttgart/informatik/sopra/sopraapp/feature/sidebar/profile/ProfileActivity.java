package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.EventsAuthentication;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.controls.FixedDialog;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetriever;
import de.uni_stuttgart.informatik.sopra.sopraapp.util.InputRetrieverRegular;


public class ProfileActivity extends ProfileActivityBindings {

    private static final Pattern emailPattern = Pattern.compile(Constants.EMAIL_REGEX);

    private boolean isChanged = false;
    private MenuItem menuSaveItem;

    @Inject
    UserManager userManager;

    private int[] profilePictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setTitle(strProfileAppBarTitle);

        profilePictures = new int[]{
            R.drawable.zprofile_1,
            R.drawable.zprofile_2,
            R.drawable.zprofile_3,
            R.drawable.zprofile_4,
            R.drawable.zprofile_5,
            R.drawable.zprofile_6,
            R.drawable.zprofile_7,
            R.drawable.zprofile_8,
            R.drawable.zprofile_9,
            R.drawable.zprofile_10,
            R.drawable.zprofile_11,
            R.drawable.zprofile_12,
            R.drawable.zprofile_13,
            R.drawable.zprofile_14,
            R.drawable.zprofile_15,
            R.drawable.zprofile_16,
            R.drawable.zprofile_17,
            R.drawable.zprofile_18,
            R.drawable.zprofile_19,
            R.drawable.zprofile_20,
            R.drawable.zprofile_21,
            R.drawable.zprofile_22,
            R.drawable.zprofile_23,
            R.drawable.zprofile_24,
            R.drawable.zprofile_25,
            R.drawable.zprofile_26,
            R.drawable.zprofile_27,
            R.drawable.zprofile_28,
            R.drawable.zprofile_29
        };
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
        temporaryEditText.addTextChangedListener((RemoveErrorTextWatcher) s -> {
            if (isEmailValid(temporaryEditText))
                temporaryEditText.setError(null);
        });
    }

    @Subscribe(sticky = true)
    public void handleLogin(EventsAuthentication.Login event) {
        textViewUserName.setText(event.user.toString());
        textViewUserRole.setText(event.user.getRole().toString());
        imageViewProfilePicture.setImageResource(profilePictures[event.user.getProfilePicture()]);

        editTextEmailField.setText(event.user.getEmail());
        editTextEmailField.addTextChangedListener((RemoveErrorTextWatcher) s -> {
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
            User currentUser = userManager.getCurrentUser();
            return !currentUser.getEmail().equals(editTextEmailField.getText().toString()) || currentUser.isChanged();
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
            userManager.getCurrentUser().setEmail(editTextEmailField.getText().toString()).save();
            updateMenuSaveButton();
        } catch (UserManager.NoUserException | InterruptedException | ExecutionException e) {
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


    @OnClick(R.id.user_profile_photo)
    public void onProfileImagePressed(ImageButton imageButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();

        View inflate = layoutInflater.inflate(R.layout.activity_profile_input_img_dialog, null);
        inflate.findViewById(R.id.gridview);
        // Prepare grid view
        GridView gridView = new GridView(this);
        gridView.setAdapter(new ImageAdapter(profilePictures));
        gridView.setNumColumns(4);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                userManager.getCurrentUser().setProfilePicture(position);
                imageViewProfilePicture.setImageResource(profilePictures[position]);
                updateMenuSaveButton();
            } catch ( UserManager.NoUserException e) {
                e.printStackTrace();
            }
        });

        builder.setView(gridView);
        builder.setTitle("Goto");
        builder.show();

    }

    private class ImageAdapter extends BaseAdapter {

        List<Integer> imageList;

        public ImageAdapter(int[] intArray) {
            imageList = Arrays.stream(intArray).boxed().collect(Collectors.toList());
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(getApplication());
                imageView.setLayoutParams(new GridView.LayoutParams(160, 160));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(profilePictures[position]);
            return imageView;
        }
    }


}