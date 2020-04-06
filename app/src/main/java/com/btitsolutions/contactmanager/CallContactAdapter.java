package com.btitsolutions.contactmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Bereket on 5/12/2017.
 */

public class CallContactAdapter extends BaseAdapter {

    private Activity context_1;

    private List<CallContactModel> callContactModels;

    public CallContactAdapter(Activity context,
                          List<CallContactModel> callContactModels) {
        context_1 = context;
        this.callContactModels = callContactModels;
    }

    @Override
    public int getCount() {
        return callContactModels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView lblDisplayName, lblPhoneNumber;
        Button btnUpdate, btnDelete;

        if (convertView == null) {
            convertView = LayoutInflater.from(context_1).inflate(
                    R.layout.call_contact_list, null);

            lblDisplayName = (TextView) convertView.findViewById(R.id.lblDisplayName);
            lblPhoneNumber = (TextView) convertView.findViewById(R.id.lblPhoneNumber);
            btnUpdate = (Button) convertView.findViewById(R.id.btnUpdate);
            btnDelete = (Button)convertView.findViewById(R.id.btnDelete);

            lblDisplayName.setText(callContactModels.get(position).getDisplayName());
            lblPhoneNumber.setText(callContactModels.get(position).getPhoneNumber());
            btnUpdate.setTag(callContactModels.get(position).getDisplayName());
            btnDelete.setTag(callContactModels.get(position).getDisplayName());
        }

        btnUpdate = (Button)convertView.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCallSettingEditDialog(context_1, view.getTag().toString());
            }
        });

        btnDelete = (Button)convertView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCallSettingDeleteDialog(context_1, view.getTag().toString());
            }
        });

        return convertView;
    }

    public void OpenCallSettingEditDialog(Context context, String selectedContact) {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.call_setting_edit_dialog);
        dialog.setTitle(selectedContact);
        final String displayName = selectedContact;

        CheckBox chkBlockContent = (CheckBox) dialog.findViewById(R.id.chkBlockContent);
        CheckBox chkBlockContactForHowLong = (CheckBox) dialog.findViewById(R.id.chkBlockContactForHowLong);
        final EditText txtBlockForHowLong = (EditText) dialog.findViewById(R.id.txtBlockForHowLong);
        chkBlockContactForHowLong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    txtBlockForHowLong.setEnabled(false);
                }
                else
                {
                    txtBlockForHowLong.setEnabled(true);
                }
            }
        });

        CheckBox chkClearCallLog = (CheckBox) dialog.findViewById(R.id.chkClearCallLog);
        CheckBox chkClearCallForHowLong = (CheckBox) dialog.findViewById(R.id.chkClearCallForHowLong);
        final EditText txtClearCallForHowLong = (EditText) dialog.findViewById(R.id.txtClearCallForHowLong);
        chkClearCallForHowLong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    txtClearCallForHowLong.setEnabled(false);
                }
                else
                {
                    txtClearCallForHowLong.setEnabled(true);
                }
            }
        });

        DBHelper dbHelper = new DBHelper(context_1);
        CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
        callSMSDetailModel = dbHelper.getAllCallSmsDetailByDisplayName(displayName, "CALL");

        chkBlockContent.setChecked(false);
        chkBlockContactForHowLong.setChecked(false);
        chkClearCallLog.setChecked(false);
        chkClearCallForHowLong.setChecked(false);
        txtBlockForHowLong.setText("");
        txtClearCallForHowLong.setText("");

        if(callSMSDetailModel != null)
        {
            if(callSMSDetailModel.getIsBlocked().equals("True"))
            {
                chkBlockContent.setChecked(true);
            }
            else
            {
                chkBlockContent.setChecked(false);
            }

            if(callSMSDetailModel.getIsAlwaysBlocked().equals("True"))
            {
                chkBlockContactForHowLong.setChecked(true);
            }
            else
            {
                chkBlockContactForHowLong.setChecked(false);
            }

            if(callSMSDetailModel.getIsCallCleared().equals("True"))
            {
                chkClearCallLog.setChecked(true);
            }
            else
            {
                chkClearCallLog.setChecked(false);
            }

            if(callSMSDetailModel.getIsAlwaysCallCleared().equals("True"))
            {
                chkClearCallForHowLong.setChecked(true);
            }
            else
            {
                chkClearCallForHowLong.setChecked(false);
            }

            txtBlockForHowLong.setText(callSMSDetailModel.getBlockedForHowLong());
            txtClearCallForHowLong.setText(callSMSDetailModel.getCallClearedForHowLong());
        }

        Button btnDialogContinue = (Button) dialog.findViewById(R.id.btnDialogContinue);
        btnDialogContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(context_1);
                try{
                    CheckBox chkBlockContent = (CheckBox) dialog.findViewById(R.id.chkBlockContent);
                    CheckBox chkBlockContactForHowLong = (CheckBox) dialog.findViewById(R.id.chkBlockContactForHowLong);
                    EditText txtBlockForHowLong = (EditText) dialog.findViewById(R.id.txtBlockForHowLong);

                    CheckBox chkClearCallLog = (CheckBox) dialog.findViewById(R.id.chkClearCallLog);
                    CheckBox chkClearCallForHowLong = (CheckBox) dialog.findViewById(R.id.chkClearCallForHowLong);
                    EditText txtClearCallForHowLong = (EditText) dialog.findViewById(R.id.txtClearCallForHowLong);

                    CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
                    callSMSDetailModel.setDisplayName(displayName);
                    callSMSDetailModel.setIsCallOrSms("CALL");

                    try{
                        dbHelper.deleteCallSmsDetail(callSMSDetailModel);
                    }
                    catch(Exception ex){
                        Toast.makeText(context_1, "Failed to update", Toast.LENGTH_LONG).show();
                    }

                    if(chkBlockContent.isChecked())
                    {
                        callSMSDetailModel.setIsBlocked("True");
                    }
                    else
                    {
                        callSMSDetailModel.setIsBlocked("False");
                    }

                    if(chkBlockContactForHowLong.isChecked())
                    {
                        callSMSDetailModel.setIsAlwaysBlocked("True");
                    }
                    else
                    {
                        callSMSDetailModel.setIsAlwaysBlocked("False");
                    }

                    callSMSDetailModel.setBlockedForHowLong(txtBlockForHowLong.getText().toString());

                    if(chkClearCallLog.isChecked())
                    {
                        callSMSDetailModel.setIsCallCleared("True");
                    }
                    else
                    {
                        callSMSDetailModel.setIsCallCleared("False");
                    }

                    if(chkClearCallForHowLong.isChecked())
                    {
                        callSMSDetailModel.setIsAlwaysCallCleared("True");
                    }
                    else
                    {
                        callSMSDetailModel.setIsAlwaysCallCleared("False");
                    }

                    callSMSDetailModel.setCallClearedForHowLong(txtClearCallForHowLong.getText().toString());
                    Date currentDate = Calendar.getInstance().getTime();

                    callSMSDetailModel.setCreatedDate(currentDate.toString());
                    dbHelper.addCallSmsDetail(callSMSDetailModel);

                    dialog.dismiss();
                }
                catch(Exception ex){
                    Toast.makeText(context_1, "Error while updating record", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public void OpenCallSettingDeleteDialog(Context context, String selectedContact) {
        final Dialog dialogDelete = new Dialog(context); // Context, this, etc.
        dialogDelete.setContentView(R.layout.call_setting_delete_dialog);
        dialogDelete.setTitle(selectedContact);

        final String displayName = selectedContact;

        Button btnDialogContinue = (Button) dialogDelete.findViewById(R.id.btnDialogContinue);
        btnDialogContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(context_1);

                try{
                    CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
                    callSMSDetailModel.setDisplayName(displayName);
                    callSMSDetailModel.setIsCallOrSms("CALL");
                    dbHelper.deleteCallSmsDetail(callSMSDetailModel);

                    CallContactModel callContactModel = new CallContactModel();
                    callContactModel.setDisplayName(displayName);
                    dbHelper.deleteCallContact(callContactModel);

                    dialogDelete.dismiss();

                    try{
                        if (context_1 instanceof CallSettingActivity)
                        {
                            ((CallSettingActivity)context_1).RefreshContactList();
                        }
                    }
                    catch (Exception ex){
                        Toast.makeText(context_1, "Please refresh the page to see changes", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex){
                    Toast.makeText(context_1, "Error while deleting record", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnDialogCancel = (Button) dialogDelete.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDelete.dismiss();
            }
        });

        dialogDelete.setCancelable(false);
        dialogDelete.show();
    }
}
