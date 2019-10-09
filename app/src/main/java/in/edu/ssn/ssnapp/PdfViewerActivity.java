package in.edu.ssn.ssnapp;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.crashlytics.android.Crashlytics;

import java.io.InputStream;
import java.util.Arrays;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import pl.droidsonroids.gif.GifImageView;
import spencerstudios.com.bungeelib.Bungee;

public class PdfViewerActivity extends BaseActivity implements DownloadFile.Listener {

    private static final String TAG ="PdfViewerActivity" ;
    String url;

    TextView pageNumberTV;
    RemotePDFViewPager remotePDFViewPager;
    PDFViewPager pdfViewPager;
    PDFPagerAdapter adapter;

    ImageView backIV, iv_download;
    GifImageView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(darkModeEnabled){
            setContentView(R.layout.activity_pdf_viewer_dark);
            clearLightStatusBar(this);
        }else{
            setContentView(R.layout.activity_pdf_viewer);
        }


        initUI();

        Intent intent=getIntent();
        url = intent.getStringExtra(Constants.PDF_URL);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Uri downloadUri = Uri.parse(url);
        iv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!CommonUtils.hasPermissions(PdfViewerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(PdfViewerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else {
                    try {
                        Toast toast = Toast.makeText(PdfViewerActivity.this, "Downloading...", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                        String names = downloadUri.getLastPathSegment();
                        if (names != null && names.startsWith("post_bus"))
                            names = names.substring(9);

                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/SSN App/", names)
                                .setTitle(names).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        dm.enqueue(request);
                    } catch (Exception ex) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Download failed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        ex.printStackTrace();
                        Crashlytics.log("stackTrace: " + Arrays.toString(ex.getStackTrace()) + " \n Error: " + ex.getMessage());
                    }
                }
            }
        });

        setupPdf();
    }

    void initUI(){
        pageNumberTV = findViewById(R.id.pageNumberTV);
        pdfViewPager = findViewById(R.id.pdfViewPager);
        backIV = findViewById(R.id.backIV);
        iv_download = findViewById(R.id.iv_download);
        progress = findViewById(R.id.progress);
    }

    private void setupPdf() {
        remotePDFViewPager = new RemotePDFViewPager(PdfViewerActivity.this, url,this);
        pdfViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pageNumberTV.setText(i+1+"/"+adapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapter = new PDFPagerAdapter(this, destinationPath);
        pageNumberTV.setText("1/"+adapter.getCount());
        pdfViewPager.setAdapter(adapter);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Exception e) {
        progress.setVisibility(View.GONE);
        Log.d(TAG,e.getMessage());
        showAlertDialog();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
    }

    void showAlertDialog(){
        final Dialog dialog = new Dialog(PdfViewerActivity.this);
        dialog.setContentView(R.layout.custom_alert_dialog2);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_message = dialog.findViewById(R.id.tv_message);
        TextView tv_ok = dialog.findViewById(R.id.tv_ok);

        if(CommonUtils.alerter(getApplicationContext())) {
            tv_title.setText("Network error!");
            tv_message.setText("Please check your mobile data or Wi-Fi connection.");
        }
        else{
            tv_title.setText("Can't open the file!");
            tv_message.setText("Sorry, we were unable to open the file at the moment. So, please try again later.");
        }
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(PdfViewerActivity.this);
        finish();
    }
}
