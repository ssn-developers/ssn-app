package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.AlumniDetails;
import in.edu.ssn.ssnapp.utils.SharedPref;

//this Adapter is Used in AppInfoActivity to Show the info about Alumni Contributors.
public class AboutAlumniAdapter extends RecyclerView.Adapter<AboutAlumniAdapter.ContributionViewHolder> {

    boolean darkMode = false;
    private ArrayList<AlumniDetails> alumniDetails;
    private Context context;
    private TextDrawable.IBuilder builder;

    public AboutAlumniAdapter(Context context, ArrayList<AlumniDetails> alumniDetails) {
        this.context = context;
        this.alumniDetails = alumniDetails;

        //Icon creator from the first letter of a word. To create DP's using a Letter.
        builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
        //get darkmode preference
        darkMode = SharedPref.getBoolean(context, "dark_mode");
    }

    @NonNull
    @Override
    public AboutAlumniAdapter.ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;

        //check if the darkmode enabled and open respective Item layout.
        if (darkMode) {
            listItem = layoutInflater.inflate(R.layout.alumni_item_dark, parent, false);
        } else {
            listItem = layoutInflater.inflate(R.layout.alumni_item, parent, false);
        }
        return new AboutAlumniAdapter.ContributionViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutAlumniAdapter.ContributionViewHolder holder, int position) {
        final AlumniDetails drawer = alumniDetails.get(position);

        holder.nameTV.setText(drawer.getName());
        holder.emailTV.setText(drawer.getEmail());

        //Icon creator from the first letter of a word. To create DP's using a Letter.
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(drawer.getEmail());
        TextDrawable ic1 = builder.build(String.valueOf(drawer.getName().charAt(0)), color);
        holder.dpIV.setImageDrawable(ic1);

        //Redirect to Gmail.
        holder.emailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", drawer.getEmail(), null)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return alumniDetails.size();
    }

    public class ContributionViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, emailTV;
        public ImageView dpIV;

        public ContributionViewHolder(View convertView) {
            super(convertView);

            nameTV = convertView.findViewById(R.id.nameTV);
            emailTV = convertView.findViewById(R.id.emailTV);
            dpIV = convertView.findViewById(R.id.dpIV);
        }
    }
}
