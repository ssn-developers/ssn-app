package in.edu.ssn.ssnapp.fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ClubAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class ClubFragment extends Fragment {

    public ClubFragment() {

    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference clubcolref = db.collection("club");
    ListView subs_club;
    ListView unsubs_club;
    ArrayList<Club> subs = new ArrayList<>();
    ArrayList<Club> unsubs = new ArrayList<>();
    ClubAdapter subs_ada;
    ClubAdapter unsubs_ada;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(),view);
        subs_club = view.findViewById(R.id.feedsRV_club);
        unsubs_club = view.findViewById(R.id.feedsRV_uns_club);

        clubcolref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            final String documentid = document.getId();
                            final String name = document.get("name").toString();
                            final String dp_url = document.get("dp_url").toString();
                            final String cover_url = document.get("cover_url").toString();
                            final int followers = Integer.valueOf(document.get("followers").toString());
                            final String Desc = document.get("description").toString();
                            final ArrayList<String> heads = (ArrayList<String>)document.get("head");
                            if(SharedPref.getBoolean(getContext(),"club_subscribed",documentid)) {
                                subs.add(new Club(documentid, Desc, heads, dp_url, name,followers,cover_url));
                            }
                            else{
                                unsubs.add(new Club(documentid, Desc, heads, dp_url, name,followers,cover_url));

                            }
                        }

                        subs_ada = new ClubAdapter(getContext(),subs);
                        unsubs_ada = new ClubAdapter(getContext(),unsubs);
                        subs_club.setAdapter(subs_ada);
                        unsubs_club.setAdapter(unsubs_ada);
                    }
                });


        return view;

    }

}