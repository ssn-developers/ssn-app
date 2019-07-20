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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

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
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
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



    /************************************************************************/

    // gets the key hash of the signing certificate

    public static String getHash(Context context) {

        // src: https://stackoverflow.com/questions/52041805/how-to-use-packageinfo-get-signing-certificates-in-api-28
        // used the code from the accepted answer

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
            final Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();
            final MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signatures[0].toByteArray());
            final String signatureBase64 = new String(Base64.encode(md.digest(), Base64.DEFAULT));
            return signatureBase64;

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        Toast.makeText(context, "Hash not found", Toast.LENGTH_SHORT).show();
        return "null";
    }
    /************************************************************************/

}