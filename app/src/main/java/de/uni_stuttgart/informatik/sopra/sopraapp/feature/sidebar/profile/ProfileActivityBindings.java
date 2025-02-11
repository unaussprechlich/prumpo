package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseActivity;

public abstract class ProfileActivityBindings extends BaseActivity {

    @BindString(R.string.profile_title_app_bar)
    String strProfileAppBarTitle;

    @BindString(R.string.profile_logout_dialog_title)
    String strLogoutDialogTitle;

    @BindString(R.string.profile_logout_dialog_message)
    String strLogoutDialogMessage;
    @BindString(R.string.profile_logout_dialog_message_onchange)
    String strLogoutDialogMessageOnChange;

    @BindString(R.string.map_frag_botsheet_dialog_yes)
    String strLogoutDialogConfirmYes;

    @BindString(R.string.map_frag_botsheet_dialog_no)
    String strLogoutDialogConfirmNo;

    @BindString(R.string.prompt_email)
    String strEmailDialogTitle;

    @BindString(R.string.profile_leave_dialog_title)
    String strLeaveDialogTitle;

    @BindString(R.string.profile_leave_dialog_message)
    String strLeaveDialogMessage;

    @BindString(R.string.profile_image_change)
    String strChangeProfileImageHeader;

    @BindString(R.string.profile_input_password_title)
    String strPasswordDialogHeader;

    @BindString(R.string.profile_input_password_title_confirm)
    String strPasswordDialogConfirmHeader;

    @BindString(R.string.profile_onsave_successfully)
    String strSavedSuccessfully;

    @BindString(R.string.profile_onsave_notsuccessfully)
    String strSavedNotSuccessfully;

    @BindView(R.id.profile_input_password)
    EditText editTextPassword;

    @BindView(R.id.profile_input_password_confirm)
    EditText editTextPasswordConfirm;

    @BindView(R.id.profile_input_email)
    EditText editTextEmailField;

    @BindView(R.id.user_name_text)
    TextView textViewUserName;

    @BindView(R.id.user_role_text)
    TextView textViewUserRole;

    @BindView(R.id.user_profile_photo)
    ImageView imageViewProfilePicture;
}
