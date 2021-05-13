package com.hcmus.photovideoviewer.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.hcmus.photovideoviewer.R;

import java.util.Map;

public class PreferenceTheme extends Preference {

    public PreferenceTheme(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.preference_theme);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false); // disable parent click
        View button = holder.findViewById(R.id.theme_dark);
        button.setClickable(true); // enable custom view click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(R.string.change_password);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText oldPass = new EditText(context);
                oldPass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                oldPass.setHint(R.string.oldpass);
                layout.addView(oldPass);
                final EditText newPass = new EditText(context);
                newPass.setHint(R.string.newpass);
                newPass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                layout.addView(newPass);
                dialog.setView(layout);
                SharedPreferences sharePassPrivate = context.getSharedPreferences("PasswordPrivate", Context.MODE_PRIVATE);
                Map<String, ?> mapPass = sharePassPrivate.getAll();
                try{
                    mapPass.get("Password").toString();
                }catch (Exception e){
                    Toast.makeText(context, "Chưa có mật khẩu", Toast.LENGTH_LONG).show();
                    return;
                }
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String getOldPass = oldPass.getText().toString();
                        String getNewPass = newPass.getText().toString();
                        if(getOldPass.equals(mapPass.get("Password"))){
                            SharedPreferences.Editor editor = sharePassPrivate.edit();
                            editor.putString("Password", getNewPass);
                            editor.apply();
                            Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(context, "Mật khẩu cũ không đúng", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        // the rest of the click binding
    }
}