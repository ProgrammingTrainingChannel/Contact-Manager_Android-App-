package com.btitsolutions.contactmanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Trinity Tuts on 10-01-2015.
 */
public class SelectContactAdapter extends BaseAdapter {

    public List<SelectContact> _data;
    private ArrayList<SelectContact> arraylist;
    public ArrayList<SelectContact> selectedUsers = new ArrayList<SelectContact>();
    public ArrayList<SelectContact> getSelectedUsers(){
        return selectedUsers;
    }

    Context _c;
    ViewHolder v;
    //RoundImage roundedImage;

    public SelectContactAdapter(List<SelectContact> selectUsers, Context context) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<SelectContact>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contacts_list_item, null);
        } else {
            view = convertView;
        }

        final View parentView = view;
        final CheckBox chkContact = (CheckBox) view.findViewById(R.id.chkContact);
        chkContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    TextView txtName = (TextView) parentView.findViewById(R.id.name);
                    TextView txtPhone = (TextView) parentView.findViewById(R.id.no);

                    txtName.setTag(selectedUsers.size());
                    selectedUsers.add(selectedUsers.size(), new SelectContact(txtName.getText().toString(), txtPhone.getText().toString(), "-"));
                }
                else{
                    TextView txtName = (TextView) parentView.findViewById(R.id.name);

                    selectedUsers.remove(Integer.parseInt(txtName.getTag().toString()));
                }
            }
        });


        v = new ViewHolder();

        v.title = (TextView) view.findViewById(R.id.name);
        //v.chkContact = (CheckBox) view.findViewById(R.id.chkContact);
        v.phone = (TextView) view.findViewById(R.id.no);
        //v.imageView = (ImageView) view.findViewById(R.id.pic);

        final SelectContact data = _data.get(i);
        v.title.setText(data.getName());
        //v.chkContact.setChecked(data.getCheckedBox());
        v.phone.setText(data.getPhone());

        view.setTag(data);
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (SelectContact wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        ImageView imageView;
        TextView title, phone;
        CheckBox check;
    }
}