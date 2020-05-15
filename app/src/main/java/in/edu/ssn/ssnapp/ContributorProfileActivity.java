package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import in.edu.ssn.ssnapp.models.TeamDetails;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import spencerstudios.com.bungeelib.Bungee;

public class ContributorProfileActivity extends BaseActivity {

    ImageView backIV, userImageIV, iconIV1, iconIV2, iconIV3;
    TextView nameTV, workTV, linkTV1, linkTV2, linkTV3, linkTitleTV1, linkTitleTV2, linkTitleTV3;
    CardView linkCV1, linkCV2, linkCV3;
    TeamDetails teamDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (darkModeEnabled) {
            setContentView(R.layout.activity_contributor_profile_dark);
            clearLightStatusBar(this);
        } else {
            setContentView(R.layout.activity_contributor_profile);
        }

        initUI();
        updateUI();
    }

    void initUI() {
        backIV = findViewById(R.id.backIV);
        userImageIV = findViewById(R.id.userImageIV);
        nameTV = findViewById(R.id.nameTV);
        workTV = findViewById(R.id.workTV);
        linkCV1 = findViewById(R.id.linkCV1);
        linkCV2 = findViewById(R.id.linkCV2);
        linkCV3 = findViewById(R.id.linkCV3);
        iconIV1 = findViewById(R.id.iconIV1);
        iconIV2 = findViewById(R.id.iconIV2);
        iconIV3 = findViewById(R.id.iconIV3);
        linkTV1 = findViewById(R.id.linkTV1);
        linkTV2 = findViewById(R.id.linkTV2);
        linkTV3 = findViewById(R.id.linkTV3);
        linkTitleTV1 = findViewById(R.id.linkTitleTV1);
        linkTitleTV2 = findViewById(R.id.linkTitleTV2);
        linkTitleTV3 = findViewById(R.id.linkTitleTV3);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void updateUI() {
        teamDetails = (TeamDetails) getIntent().getSerializableExtra("Contributor");
        nameTV.setText(teamDetails.getName());
        workTV.setText(teamDetails.getPosition());
        userImageIV.setImageResource(teamDetails.getDp());
        updateLinks(teamDetails.getUrl().get(0), linkCV1, iconIV1, linkTitleTV1, linkTV1);
        updateLinks(teamDetails.getUrl().get(1), linkCV2, iconIV2, linkTitleTV2, linkTV2);
        updateLinks(teamDetails.getUrl().get(2), linkCV3, iconIV3, linkTitleTV3, linkTV3);
    }

    void updateLinks(final String link, CardView cardView, ImageView imageView, TextView textView1, TextView textView2) {
        textView2.setText(link);
        if (link.contains("@")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.googleRedColor));
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.mail_icon));
            textView1.setText("Mail");
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", link, null)));
                }
            });
        } else if (link.contains("github")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.githubColor));
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.github_icon));
            textView1.setText("Github");
            openLink(link, cardView);
        } else if (link.contains("facebook")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.facebookColor));
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon));
            textView1.setText("Facebook");
            openLink(link, cardView);
        } else if (link.contains("instagram")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.instagramColor));
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.instagram_icon));
            textView1.setText("Instagram");
            openLink(link, cardView);
        } else if (link.contains("linkedin")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.linkedinColor));
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.linkedin_icon));
            textView1.setText("Linkedin");
            openLink(link, cardView);
        } else if (link.contains("behance")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.behanceColor));
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.behance_icon));
            textView1.setText("Behance");
            openLink(link, cardView);
        } else if (link.contains("dribbble")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.dribbbleColor));
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.dribbble_icon));
            textView1.setText("Dribbble");
            openLink(link, cardView);
        }

    }

    void openLink(final String link, CardView cardView) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtils.alerter(getApplicationContext())) {
                    CommonUtils.openCustomBrowser(getApplicationContext(), link);
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "contributor_profile");
                    startActivity(intent);
                    Bungee.fade(ContributorProfileActivity.this);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
