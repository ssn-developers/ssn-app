package in.edu.ssn.ssnapp;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.InputStream;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import in.edu.ssn.ssnapp.utils.Constants;
import pl.droidsonroids.gif.GifImageView;

public class PdfViewerActivity extends BaseActivity implements DownloadFile.Listener {

    private static final String TAG ="PdfViewerActivity" ;
    String url;

    TextView pageNumberTV;
    RemotePDFViewPager remotePDFViewPager;
    PDFViewPager pdfViewPager;
    PDFPagerAdapter adapter;

    ImageView backIV;
    GifImageView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        initUI();

        Intent intent=getIntent();
        url = intent.getStringExtra(Constants.PDF_URL);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupPdf();
    }

    void initUI(){
        pageNumberTV = findViewById(R.id.pageNumberTV);
        pdfViewPager = findViewById(R.id.pdfViewPager);
        backIV = findViewById(R.id.backIV);
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
                //Toast.makeText(PdfViewerActivity.this, "current page "+i, Toast.LENGTH_SHORT).show();
                pageNumberTV.setText(String.valueOf(i+1));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        pageNumberTV.setText("1");
        adapter = new PDFPagerAdapter(this, destinationPath);
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
        /*pb_pdf.setMax(total);
        pb_pdf.setProgress(progress);*/
    }


    void showAlertDialog(){
        final Dialog dialog = new Dialog(PdfViewerActivity.this);
        dialog.setContentView(R.layout.custom_alert_dialog2);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_message = dialog.findViewById(R.id.tv_message);
        TextView tv_ok = dialog.findViewById(R.id.tv_ok);

        tv_title.setText("Can't open the file!");
        tv_message.setText("Sorry, we were unable to open the file at the moment. So, please try again later.");
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }
}
