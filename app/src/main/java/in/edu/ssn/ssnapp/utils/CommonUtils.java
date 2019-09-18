package in.edu.ssn.ssnapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.crashlytics.android.Crashlytics;
import com.google.common.io.BaseEncoding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import in.edu.ssn.ssnapp.R;

public class CommonUtils {

    public static Boolean is_blocked = false;
    //Change Font
    public static Typeface regular, bold, semi_bold;
    public static void initFonts(Context context, View view){
        regular = ResourcesCompat.getFont(context, R.font.open_sans);
        bold = ResourcesCompat.getFont(context, R.font.open_sans_bold);
        semi_bold = ResourcesCompat.getFont(context, R.font.open_sans_semi_bold);

        FontChanger fontChanger = new FontChanger(bold);
    }

    //Hides Keyboard
    public static void hideKeyboard(Activity activity){
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Checks for Internet Connectivity
    public static boolean alerter(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }
        return false;
    }

    /************************************************************************/
    // checks if wifi is connected to a particular network

    public static boolean checkWifiOnAndConnected(Context context,String ssid) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if( wifiInfo.getNetworkId() == -1 )
                return false;   // Not connected to an access point
            else{
                // connected to the required wifi network
                String temp=findSSIDForWifiInfo(wifiMgr,wifiInfo);

                if(temp.toLowerCase().equalsIgnoreCase("\""+ssid+"\""))
                    return true;
                else
                    return false;
            }
        }
        else
            return false; // Wi-Fi adapter is OFF
    }

    public static String findSSIDForWifiInfo(WifiManager manager, WifiInfo wifiInfo) {

        List<WifiConfiguration> listOfConfigurations = manager.getConfiguredNetworks();

        for (int index = 0; index < listOfConfigurations.size(); index++) {
            WifiConfiguration configuration = listOfConfigurations.get(index);
            if (configuration.networkId == wifiInfo.getNetworkId()) {
                return configuration.SSID;
            }
        }

        return null;
    }

    public static String getYear(String val){
        if(val.equals("4"))
            return Constants.fourth;
        else if(val.equals("3"))
            return Constants.third;
        else if(val.equals("2"))
            return Constants.second;
        else
            return Constants.first;
    }

    /************************************************************************/

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Boolean getIs_blocked() {
        return is_blocked;
    }

    public static void setIs_blocked(Boolean is_blocked) {
        CommonUtils.is_blocked = is_blocked;
    }


    public static String getNameFromEmail(String email){

        String name=" ";

        email = email.substring(0, email.indexOf("@"));
        for (int j = 0; j < email.length(); j++) {
            if (Character.isDigit(email.charAt(j))) {
                name = email.substring(0, j);
                break;
            }
        }
        if (name.isEmpty())
            name = email;

        Character.toUpperCase(name.charAt(0));

        return name;
    }
}