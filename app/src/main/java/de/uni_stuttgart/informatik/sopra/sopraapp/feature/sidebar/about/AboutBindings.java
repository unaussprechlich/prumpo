package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.about;

import android.support.v7.widget.CardView;
import butterknife.BindString;
import butterknife.BindView;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

import java.text.MessageFormat;

public abstract class AboutBindings extends DaggerFragment {


    final static String MAIL_TO = "mailto:";

    @BindView(R.id.about_frag_container_contacts)
    CardView cardViewContacts;

    @BindView(R.id.about_frag_container_licence)
    CardView cardViewLicence;


    @BindString(R.string.app_name)
    String strAppName;

    @BindString(R.string.nav_appbar_about)
    String strAppbarTitle;

    @BindString(R.string.about_fragment_contact_chooser_title)
    String strContactIntentChooserTitle;

    @BindString(R.string.about_fragment_contact_address)
    String strContactEmailAddress;

    @BindString(R.string.about_fragment_contact_email_subject)
    String strContactEmailSubject;

    @BindString(R.string.about_fragment_licence_url)
    String strLicenceUrl;

    @BindString(R.string.about_fragment_version)
    String strVersionNumber;


    /**
     * Retrieve a subject for the email
     * @return the subject for the email
     */
    String getEmailSubject(){
        return MessageFormat.format("{0} v{1}: {2}", strAppName, strVersionNumber, strContactEmailSubject);
    }
}
