package com.a2015.sf32.aukcije.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.db.DatabaseHelper;
import com.a2015.sf32.aukcije.model.DataSingleton;
import com.a2015.sf32.aukcije.model.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<User, Integer> usersDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        User user = DataSingleton.getInstance().getUser();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String newName = sp.getString("name_pref", user.getName());
        String newEmail = sp.getString("email_pref", user.getEmail());
        String newAddress = sp.getString("address_pref", user.getAddress());
        String newPhone = sp.getString("phone_pref", user.getPhone());
        String newPassword = sp.getString("password_pref", user.getPassword());

        user.setName(newName);
        user.setEmail(newEmail);
        user.setAddress(newAddress);
        user.setPhone(newPhone);
        user.setPassword(newPassword);
        usersDAO = getHelper().getUsersRuntimeDAO();
        usersDAO.update(user);

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
