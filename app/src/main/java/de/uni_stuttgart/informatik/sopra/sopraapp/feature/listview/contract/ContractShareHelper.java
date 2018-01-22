package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.GsonBuilder;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.Constants;
import de.uni_stuttgart.informatik.sopra.sopraapp.app.MainActivity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet.AbstractBottomSheetBase;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/**
 * If we really would do it correctly we should separate the sharing logic from the ui stuff.
 * But I don't want to do this, because Java.
 */
public class ContractShareHelper extends ContractShareHelperBindings {

    private FragmentActivity fragmentActivity;
    private List<Contract> contracts;

    private ContractShareCallback onShareAbort = () -> { /* Ignore */ };
    private ContractShareCallback onShareDone = () -> { /* Ignore */ };

    // Sharing options
    private boolean plainText = false;


    // Constructor
    ContractShareHelper(View shareView,
                        List<Contract> contracts,
                        FragmentActivity fragmentActivity) {

        this.contracts = contracts;
        this.fragmentActivity = fragmentActivity;

        ButterKnife.bind(this, shareView);

        registerListener();
    }

    /**
     * Method for register listeners on the ui components.
     * This way all the corresponding booleans will always hold the exact state.
     */
    private void registerListener() {
        plainTextCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> plainText = isChecked);
    }

    @OnClick(R.id.contract_share_export_text)
    public void onShareAsText() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ShareContracts");

        String delimiter = String.format("%s%n", strFormatUtilsShort);
        String prefix = String.format("%s%n", strFormatUtilsLong);
        String suffix = String.format("%s", strFormatUtilsLong);
        String shareString = contracts.stream()
                .map(this::contractToString)
                .collect(Collectors.joining(delimiter, prefix, suffix));

        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareString);
        fragmentActivity.startActivity(Intent.createChooser(shareIntent, "Share"));

        onShareDone.invoke();
    }

    @OnClick(R.id.contract_share_export_json)
    public void onShareAsJson() {

        if (isWritePermissionNeeded())
            requestWritePermission();
        else
            saveAsJsonFile();

    }

    @OnClick(R.id.contract_share_abort)
    public void onShareAbort() {
        onShareAbort.invoke();
    }


    // ### Helper for saving as text #################################################### Helper for saving as text ###

    /**
     * Using https://dzone.com/articles/java-string-format-examples
     * todo check whether all necessary info is shared
     *
     * @param c the Contract to format
     * @return the preformatted String to share
     */
    private String contractToString(Contract c) {

        StringBuilder stringBuilder = new StringBuilder();

        String policyholder = c.getHolder().getValue() != null
                ? String.format(Locale.GERMAN, ": %s", c.getHolder().getValue().getName())
                : String.format(Locale.GERMAN, "-ID: %d", c.getHolderID());

        Formatter formatter = new Formatter(stringBuilder);
        formatter.format("%s %d:%n", strContractHeader, c.getID());
        formatter.format("%s: %s%n", strContractName, c.getName());
        formatter.format("%s%s%n", strContractPolicyholder, policyholder);
        formatter.format("%s: %s%n", strContractDamagetypes, c.getDamageType());
        formatter.format("%s: %s%n", strContractLocation, c.getAreaCode());
        formatter.format("%s: %s%n", strContractDate, c.getDate()
                .toString(strSimpleDateFormatPattern, Locale.GERMAN));
        formatter.format("%s: %s%n", strContractSize, AbstractBottomSheetBase.calculateAreaValue(c.getAreaSize()));

        return plainText
                ? stringBuilder.toString()
                : String.valueOf(encrypt(3, stringBuilder.toString().toCharArray()));
    }

    // ### Helper for saving to file #################################################### Helper for saving to file ###

    /**
     * Will invoke all necessary actions to save the list to file.
     */
    public void saveAsJsonFile() {
        if (isReadyToSaveAsFile())
            try {
                invokeWriteAction(contracts);
                showSharingResult(true);

            } catch (IOException e) {
                Log.e("ERROR", e.getLocalizedMessage());
                showSharingResult(false);
            }
        else
            showSharingResult(false);

        onShareDone.invoke();
    }

    /**
     * Will request write permission.
     * <p>
     * Callback will be invoked in {@link MainActivity}.
     */
    public void requestWritePermission() {
        ActivityCompat.requestPermissions(fragmentActivity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Constants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
    }

    /**
     * Will write the given contract list to file.
     * Each contract will be written into a separate file.
     *
     * @param contracts the list of contracts to write
     * @throws IOException if something goes wrong while writing
     */
    private void invokeWriteAction(List<Contract> contracts) throws IOException {
        for (Contract c : contracts)
            try (Writer writer =
                         new FileWriter(String.format(Locale.GERMAN, "%s/Contract_%d_%s.json",
                                 getExportDirectoryPath(),
                                 c.getID(),
                                 getDateStringReadyForExport()))) {

                String json = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .excludeFieldsWithoutExposeAnnotation()
                        .serializeNulls()
                        .setPrettyPrinting()
                        .create()
                        .toJson(c);

                json = plainText ? json : String.valueOf(encrypt(2, json.toCharArray()));

                writer.write(json);
            }
    }

    /**
     * Returns true if write permission was granted already.
     *
     * @return true if write permission was granted already
     */
    private boolean isWritePermissionNeeded() {
        return ContextCompat.checkSelfPermission(fragmentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PERMISSION_GRANTED;
    }

    /**
     * Will create a folder if necessary and return whether to continue to save as file.
     * <p>
     * Will return true if ...
     * <ul>
     * <li>
     * ...folder already exists <b>or</b>
     * </li>
     * <li>
     * ...folder creation was successfully
     * </li>
     * </ul>
     * <p>
     * Will return false if ...
     * <ul>
     * <li>
     * ...folder does not exist <b>and</b>
     * </li>
     * <li>
     * ...folder creation was not successful
     * </li>
     * </ul>
     */
    private boolean isReadyToSaveAsFile() {
        File folder = new File(getExportDirectoryPath());
        return folder.exists() || folder.mkdirs();
    }

    /**
     * Get the full path of the directory to export.
     *
     * @return the full path of the directory to export
     */
    private String getExportDirectoryPath() {
        return String.format("%s%s%s%s%s", Environment.getExternalStorageDirectory(),
                File.separator, strAppName,
                File.separator, "export");
    }

    /**
     * Returns the date part of the json file name.
     *
     * @return the date part of the json file name
     */
    private String getDateStringReadyForExport() {
        return DateTime.now().toString(strContractDateSharePattern, Locale.GERMAN);
    }

    /**
     * Will create a toast showing information whether sharing was successful.
     *
     * @param successful true if sharing was successful, false else
     */
    private void showSharingResult(boolean successful) {
        Toast.makeText(fragmentActivity.getBaseContext(),
                successful
                        ? sirContractExportJsonSuccessful + getExportDirectoryPath()
                        : sirContractExportJsonUnsuccessful,
                Toast.LENGTH_LONG)
                .show();
    }

    // ### Helper for saving both ########################################################## Helper for saving both ###

    /**
     * Encrypt using the famous caesar encryption.
     * <p>
     * Taken from: https://www.kleingebloggt.de/2014/01/caesar-verschluesselung-in-java-ein-einfaches-beispiel/
     */
    @SuppressWarnings("SameParameterValue")
    private static char[] encrypt(int offset, char[] charArray) {

        char[] cryptArray = new char[charArray.length];
        int bound = charArray.length;

        for (int i = 0; i < bound; i++) {
            int difference = (charArray[i] + offset) % 128;
            cryptArray[i] = (char) (difference);
        }
        return cryptArray;
    }

    // ### Setter ########################################################################################## Setter ###

    public void setOnShareAbort(ContractShareCallback onShareAbort) {
        this.onShareAbort = onShareAbort;
    }

    public void setOnShareDone(ContractShareCallback onShareDone) {
        this.onShareDone = onShareDone;
    }
}