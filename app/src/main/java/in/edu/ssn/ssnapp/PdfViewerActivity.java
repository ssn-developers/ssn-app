package in.edu.ssn.ssnapp;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import in.edu.ssn.ssnapp.utils.Constants;

public class PdfViewerActivity extends BaseActivity implements DownloadFile.Listener {

    private static final String TAG ="PdfViewerActivity" ;
    String url;



    //View
    LottieAnimationView loadingView;
    TextView pageNumberTV;
    RemotePDFViewPager remotePDFViewPager;
    PDFViewPager pdfViewPager;
    ImageView backIV;

    PDFPagerAdapter adapter;


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
        loadingView = findViewById(R.id.animation_view);
        loadingView.setSpeed(2);
        pageNumberTV = findViewById(R.id.pageNumberTV);
        pdfViewPager = findViewById(R.id.pdfViewPager);
        backIV = findViewById(R.id.backIV);
        //changeFont(bold,(ViewGroup)this.findViewById(android.R.id.content));
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
        loadingView.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        Log.d(TAG,"url "+url+" path "+destinationPath);
        adapter = new PDFPagerAdapter(this, destinationPath);
        pdfViewPager.setAdapter(adapter);
    }

    @Override
    public void onFailure(Exception e) {
        loadingView.setVisibility(View.GONE);
        showAlertDialog();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        /*pb_pdf.setMax(total);
        pb_pdf.setProgress(progress);*/
    }


    void showAlertDialog(){

        new AlertDialog.Builder(PdfViewerActivity.this)
                .setTitle("Oops")
                .setMessage("Download Failed. Try Again Later")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.close();
    }
}
