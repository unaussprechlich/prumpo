package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ProfileActivity extends ProfileActivityBindings {

    private static final Pattern emailPattern = Pattern.compile(Constants.EMAIL_REGEX);

    private boolean isChanged = false;

    private void isChanged(boolean value) {
        isChanged = value;
        menuSaveItem.getIcon().setAlpha(255 / (isChanged ? 1 : 4));
        menuSaveItem.setEnabled(isChanged);
    }

    private MenuItem menuSaveItem;

    @Inject
    UserManager userManager;

    private AlertDialog userProfileSelectionDialog;
    private List<ImageView> imageList;
    private int lastSelectedImagePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setTitle(strProfileAppBarTitle);

        if (imageList == null)
            imageList = Arrays.stream(Constants.PROFILE_IMAGE_RESOURCES)
                    .mapToObj(this::mapToImageView)
                    .collect(Collectors.toList());

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

        lastSelectedImagePosition = event.user.getProfilePicture();
        imageViewProfilePicture.setImageResource(Constants.PROFILE_IMAGE_RESOURCES[lastSelectedImagePosition]);

        editTextEmailField.setText(event.user.getEmail());
        editTextEmailField.addTextChangedListener((RemoveErrorTextWatcher) s -> {
            if (isEmailValid(editTextEmailField)) {
                editTextEmailField.setError(null);
                isChanged(true);
            }
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

        if (isChanged)
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
            isChanged(false);
            userManager.getCurrentUser()
                    .setEmail(editTextEmailField.getText().toString())
                    .setProfilePicture(lastSelectedImagePosition)
                    .save();
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

        // Grid adapter
        ProfileImageGridViewAdapter adapter = new ProfileImageGridViewAdapter(imageList);
        adapter.setOnImageSelected((drawable, position) -> {
            imageViewProfilePicture.setImageDrawable(drawable);
            isChanged(userManager.getCurrentUser().getProfilePicture() != position);
            lastSelectedImagePosition = position;
            userProfileSelectionDialog.dismiss();
            userProfileSelectionDialog = null;
        });

        // Inflate Dialog layout & Grid view & adapter
        LayoutInflater layoutInflater = getLayoutInflater();
        View inflate = layoutInflater.inflate(R.layout.activity_profile_input_img_dialog, null);
        GridView gridView = inflate.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(adapter);

        // Set layout and build
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(strChangeProfileImageHeader);
        builder.setView(inflate);
        userProfileSelectionDialog = builder.create();
        userProfileSelectionDialog.show();
    }

    /**
     * Will transform a image layout to and imageView.
     *
     * @param layoutID the id
     * @return the imageView found by the given id
     */
    private ImageView mapToImageView(int layoutID) {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setPadding(15, 15, 15, 15);
        imageView.setImageResource(layoutID);
        return imageView;
    }
}