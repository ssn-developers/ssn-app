package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import java.io.File;

import in.edu.ssn.ssnapp.utils.CommonUtils;
import spencerstudios.com.bungeelib.Bungee;

public class OpenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        ImageView iv_image=findViewById(R.id.iv_image);
        ImageView backIV=findViewById(R.id.backIV);
        ImageView iv_download=findViewById(R.id.iv_download);

        String url = getIntent().getStringExtra("url");
        final Uri downloadUri = Uri.parse(url);
        final File f= new File(downloadUri.getPath());

        Picasso.get().load(url).into(iv_image);

        iv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!CommonUtils.hasPermissions(OpenImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(OpenImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else{

                    try {
                        Toast toast = Toast.makeText(OpenImageActivity.this, "Downloading...", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/SSN App/", f.getName())
                                .setTitle(downloadUri.getLastPathSegment()).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        dm.enqueue(request);
                    }
                    catch (Exception ex) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Download failed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        ex.printStackTrace();
                        Crashlytics.log("stackTrace: "+ex.getStackTrace()+" \n Error: "+ex.getMessage());
                    }

                }

            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImageActivity.super.onBackPressed();
                Bungee.slideRight(OpenImageActivity.this);
            }
        });
    }
}