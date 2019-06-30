package in.edu.ssn.ssnapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

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
    //Uploading an Image

    public static Intent getPickImageChooserIntent(Context context) {
        Uri outputFileUri =  getOutputUri(context);

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        Intent captureIntent = new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam =  packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new  Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new  Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery =  packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new  Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent =  allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if  (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity"))  {
                mainIntent = intent;
                break;
            }
        }

        allIntents.remove(mainIntent);
        Intent chooserIntent =  Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,  allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private static Uri getOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(),"pickImageResult.jpg"));
        }
        return outputFileUri;
    }

    public static Uri getPickImageResultUri(Intent data,Context context) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getOutputUri(context) : data.getData();
    }

    public static String getMimeType(Context context, Uri uri) {
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        }
        else
            return MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
    }

    /************************************************************************/
    //JSON Export, Import & Parse

    public static void saveData(String access, String dept, String dp_url, String email, String id, String name, String position, String docId, JSONArray fac_array) {
        try {
            JSONObject fac_details = new JSONObject();
            JSONObject fac_obj = new JSONObject();

            fac_details.put("access", access);
            fac_details.put("dept", dept);
            fac_details.put("dp_url", dp_url);
            fac_details.put("email", email);
            fac_details.put("id", id);
            fac_details.put("name", name);
            fac_details.put("position", position);

            fac_obj.put(docId, fac_details);
            fac_array.put(fac_obj);
        }
        catch (Exception e) {
            Log.d("test_set", e.getMessage());
        }
    }

    public static void exportFile(Context context,JSONArray fac_array){
        try {
            File storageDir = context.getExternalCacheDir();
            if (storageDir == null || !storageDir.exists()) {
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/.SSN");
                if (!storageDir.exists())
                    storageDir.mkdirs();
            }

            FileWriter file = new FileWriter(storageDir.getPath() + "/fac_profile.json");
            file.write(fac_array.toString(4));
            file.flush();
            file.close();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("test_set",e.getMessage());
        }
    }

    public static String getData(Context context) {
        try {
            File storageDir = context.getExternalCacheDir();
            if (storageDir == null || !storageDir.exists()) {
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/.SSN");
                if(!storageDir.exists())
                    storageDir.mkdirs();
            }

            File f = new File(storageDir.getPath() + "/fac_profile.json");
            FileInputStream is = new FileInputStream(f);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer);
        }
        catch (Exception e) {
            Log.e("test_set", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }
}