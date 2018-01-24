package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

import android.widget.Button;
import android.widget.CheckBox;
import butterknife.BindString;
import butterknife.BindView;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;

public abstract class ContractShareHelperBindings {

    @BindString(R.string.app_name)
    String strAppName;

    @BindString(R.string.contract_export_util_format_1_long)
    String strFormatUtilsLong;

    @BindString(R.string.contract_export_util_format_1_short)
    String strFormatUtilsShort;

    @BindString(R.string.contract_export_header)
    String strContractHeader;

    @BindString(R.string.contract_export_name)
    String strContractName;

    @BindString(R.string.contract_export_location)
    String strContractLocation;

    @BindString(R.string.contract_export_date)
    String strContractDate;

    @BindString(R.string.map_frag_bottomsheet_date_pattern)
    String strSimpleDateFormatPattern;

    @BindString(R.string.contract_export_json_file_name_date_pattern)
    String strContractDateSharePattern;

    @BindString(R.string.contract_export_size)
    String strContractSize;

    @BindString(R.string.contract_export_damagetypes)
    String strContractDamagetypes;

    @BindString(R.string.contract_export_policyholder)
    String strContractPolicyholder;

    @BindString(R.string.contract_export_json_unsuccesful)
    String sirContractExportJsonUnsuccessful;

    @BindString(R.string.contract_export_json_succesful)
    String sirContractExportJsonSuccessful;



    @BindView(R.id.contract_share_abort)
    Button buttonAbort;

    @BindView(R.id.contract_share_export_json)
    Button buttonJson;

    @BindView(R.id.contract_share_export_text)
    Button buttonText;

    @BindView(R.id.contract_share_checkbox_plain_text)
    CheckBox plainTextCheckBox;
}
