package com.btitsolutions.contactmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Bereket on 5/12/2017.
 */

public class SMSContactAdapter extends BaseAdapter {

    private Activity context_1;

    private List<SMSContactModel> smsContactModels;

    public SMSContactAdapter(Activity context,
                          List<SMSContactModel> serviceModels) {
        context_1 = context;
        this.smsContactModels = serviceModels;
    }

    @Override
    public int getCount() {
        return smsContactModels.size();
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
                    R.layout.sms_contact_list, null);
            //convertView = new ViewHolder();
            lblDisplayName = (TextView) convertView.findViewById(R.id.lblDisplayName);
            lblPhoneNumber = (TextView) convertView.findViewById(R.id.lblPhoneNumber);
            btnUpdate = (Button) convertView.findViewById(R.id.btnUpdate);
            btnDelete = (Button)convertView.findViewById(R.id.btnDelete);

            lblDisplayName.setText(smsContactModels.get(position).getDisplayName());
            lblPhoneNumber.setText(smsContactModels.get(position).getPhoneNumber());
            btnUpdate.setTag(smsContactModels.get(position).getDisplayName());
            btnDelete.setTag(smsContactModels.get(position).getDisplayName());
        }

        btnUpdate = (Button)convertView.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    OpenSMSSettingEditDialog(context_1, view.getTag().toString());
                }
                catch(Exception ex){
                    Toast.makeText(context_1, "Cann't open", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDelete = (Button)convertView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenSMSSettingDeleteDialog(context_1, view.getTag().toString());
            }
        });

        return convertView;
    }

    public void OpenSMSSettingEditDialog(Context context, String selectedContact) {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.sms_setting_edit_dialog);
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

        CheckBox chkClearSMSLog = (CheckBox) dialog.findViewById(R.id.chkClearSMSLog);
        CheckBox chkClearSMSForHowLong = (CheckBox) dialog.findViewById(R.id.chkClearSMSForHowLong);
        final EditText txtClearSMSForHowLong = (EditText) dialog.findViewById(R.id.txtClearSMSForHowLong);
        chkClearSMSForHowLong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    txtClearSMSForHowLong.setEnabled(false);
                }
                else
                {
                    txtClearSMSForHowLong.setEnabled(true);
                }
            }
        });

        DBHelper dbHelper = new DBHelper(context_1);
        CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
        callSMSDetailModel = dbHelper.getAllCallSmsDetailByDisplayName(displayName, "SMS");

        chkBlockContent.setChecked(false);
        chkBlockContactForHowLong.setChecked(false);
        chkClearSMSLog.setChecked(false);
        chkClearSMSForHowLong.setChecked(false);
        txtBlockForHowLong.setText("");
        txtClearSMSForHowLong.setText("");

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
                chkClearSMSLog.setChecked(true);
            }
            else
            {
                chkClearSMSLog.setChecked(false);
            }

            if(callSMSDetailModel.getIsAlwaysCallCleared().equals("True"))
            {
                chkClearSMSForHowLong.setChecked(true);
            }
            else
            {
                chkClearSMSForHowLong.setChecked(false);
            }

            txtBlockForHowLong.setText(callSMSDetailModel.getBlockedForHowLong());
            txtClearSMSForHowLong.setText(callSMSDetailModel.getCallClearedForHowLong());
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

                    CheckBox chkClearSMSLog = (CheckBox) dialog.findViewById(R.id.chkClearSMSLog);
                    CheckBox chkClearSMSForHowLong = (CheckBox) dialog.findViewById(R.id.chkClearSMSForHowLong);
                    EditText txtClearSMSForHowLong = (EditText) dialog.findViewById(R.id.txtClearSMSForHowLong);

                    CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
                    callSMSDetailModel.setDisplayName(displayName);
                    callSMSDetailModel.setIsCallOrSms("SMS");

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

                    if(chkClearSMSLog.isChecked())
                    {
                        callSMSDetailModel.setIsCallCleared("True");
                    }
                    else
                    {
                        callSMSDetailModel.setIsCallCleared("False");
                    }

                    if(chkClearSMSForHowLong.isChecked())
                    {
                        callSMSDetailModel.setIsAlwaysCallCleared("True");
                    }
                    else
                    {
                        callSMSDetailModel.setIsAlwaysCallCleared("False");
                    }

                    callSMSDetailModel.setCallClearedForHowLong(txtClearSMSForHowLong.getText().toString());

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

    public void OpenSMSSettingDeleteDialog(Context context, String selectedContact) {
        final Dialog dialogDelete = new Dialog(context); // Context, this, etc.
        dialogDelete.setContentView(R.layout.sms_setting_delete_dialog);
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