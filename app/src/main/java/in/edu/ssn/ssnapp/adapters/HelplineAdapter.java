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
import in.edu.ssn.ssnapp.models.FuncHeadDetails;
import in.edu.ssn.ssnapp.utils.SharedPref;

//this Adapter is Used in FunctionalHeadsActivity to Show the info about Department HOD's and other office heads.
public class HelplineAdapter extends RecyclerView.Adapter<HelplineAdapter.ContributionViewHolder> {

    boolean darkMode = false;
    private ArrayList<FuncHeadDetails> funcHeadDetails;
    private Context context;
    private TextDrawable.IBuilder builder;

    public HelplineAdapter(Context context, ArrayList<FuncHeadDetails> funcHeadDetails) {
        this.context = context;
        this.funcHeadDetails = funcHeadDetails;

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
    public HelplineAdapter.ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;

        //check if the darkmode enabled and open respective Item layout.
        if (darkMode) {
            listItem = layoutInflater.inflate(R.layout.helpline_item_dark, parent, false);
        } else {
            listItem = layoutInflater.inflate(R.layout.helpline_item, parent, false);
        }
        return new HelplineAdapter.ContributionViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull HelplineAdapter.ContributionViewHolder holder, int position) {
        final FuncHeadDetails drawer = funcHeadDetails.get(position);

        holder.nameTV.setText(drawer.getName());
        holder.positionTV.setText(drawer.getPosition());
        holder.positionTV.setSelected(true);
        holder.extnTV.setText(drawer.getEmail());

        //Icon creator from the first letter of a word. To create DP's using a Letter.
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(drawer.getEmail());
        String textDrawable = String.valueOf(drawer.getEmail().charAt(0));
        TextDrawable ic1 = builder.build(textDrawable, color);
        holder.dpIV.setImageDrawable(ic1);

        //Redirect to Gmail.
        holder.mailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", drawer.getEmail(), null)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return funcHeadDetails.size();
    }

    public class ContributionViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, positionTV, extnTV;
        public ImageView dpIV, mailIV;

        public ContributionViewHolder(View convertView) {
            super(convertView);

            nameTV = convertView.findViewById(R.id.nameTV);
            positionTV = convertView.findViewById(R.id.positionTV);
            extnTV = convertView.findViewById(R.id.extnTV);
            dpIV = convertView.findViewById(R.id.dpIV);
            mailIV = convertView.findViewById(R.id.mailIV);
        }
    }
}
