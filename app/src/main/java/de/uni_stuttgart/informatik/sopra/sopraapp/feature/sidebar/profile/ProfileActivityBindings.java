package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.BaseEventBusActivity;

public abstract class ProfileActivityBindings extends BaseEventBusActivity {

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


    @BindView(R.id.profile_input_email)
    EditText editTextEmailField;

    @BindView(R.id.user_name_text)
    TextView textViewUserName;

    @BindView(R.id.user_role_text)
    TextView textViewUserRole;

    @BindView(R.id.user_profile_photo)
    ImageView imageViewProfilePicture;
}
