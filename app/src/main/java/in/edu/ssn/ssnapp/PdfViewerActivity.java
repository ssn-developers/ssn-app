package in.edu.ssn.ssnapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import in.edu.ssn.ssnapp.utils.Constants;

public class PdfViewerActivity extends BaseActivity implements DownloadFile.Listener {

    private static final String TAG ="PdfViewerActivity" ;
    String url;



    //View
    ProgressBar pb_pdf;
    TextView pageNumberTV;
    RemotePDFViewPager remotePDFViewPager;
    PDFViewPager pdfViewPager;
    PDFPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);


        initUI();

        Intent intent=getIntent();
        url = intent.getStringExtra(Constants.PDF_URL);

        setupPdf();




    }

    void initUI(){
        pb_pdf = findViewById(R.id.pb_pdf);
        pb_pdf.setIndeterminate(false);
        pageNumberTV = findViewById(R.id.pageNumberTV);
        pdfViewPager = findViewById(R.id.pdfViewPager);
        changeFont(bold,(ViewGroup)this.findViewById(android.R.id.content));
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
        pb_pdf.setVisibility(View.GONE);
        Log.d(TAG,"url "+url+" path "+destinationPath);
        adapter = new PDFPagerAdapter(this, destinationPath);
        pdfViewPager.setAdapter(adapter);
    }

    @Override
    public void onFailure(Exception e) {
        pb_pdf.setVisibility(View.GONE);
        showAlertDialog();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        pb_pdf.setMax(total);
        pb_pdf.setProgress(progress);
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
