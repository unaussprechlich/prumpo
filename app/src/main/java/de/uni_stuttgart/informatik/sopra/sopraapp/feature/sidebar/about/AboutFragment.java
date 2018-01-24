package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.about;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.FragmentBackPressed;

public class AboutFragment extends AboutBindings implements FragmentBackPressed {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main_fragment_about,
                container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(strAppbarTitle);
    }

    @OnClick(R.id.about_frag_container_contacts)
    public void onContainerContactsPressed() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(MAIL_TO + strContactEmailAddress));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getEmailSubject());

        startActivity(Intent.createChooser(emailIntent, strContactIntentChooserTitle));
    }

    @OnClick(R.id.about_frag_container_licence)
    public void onContainerLicencePressed() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strLicenceUrl));
        startActivity(browserIntent);
    }



}
