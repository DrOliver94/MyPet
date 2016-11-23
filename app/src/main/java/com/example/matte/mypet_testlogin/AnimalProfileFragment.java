package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AnimalProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimalProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "IdAnim";
    private String idAnim;

    private MyPetDB dbHandler2;

    private ListView itemsListView;
    private View headerAnimal;
//    private OnFragmentInteractionListener mListener;

    public GoogleApiHelper gApiHelper;

    public AnimalProfileFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idAnim ID dell'animale da visualizzare.
     * @return A new instance of fragment AnimalProfileFragment.
     */
    public static AnimalProfileFragment newInstance(String idAnim) {
        AnimalProfileFragment fragment = new AnimalProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idAnim);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idAnim = getArguments().getString(ARG_PARAM1);
        }
        dbHandler2 = new MyPetDB(getActivity());
        setHasOptionsMenu(true);
        gApiHelper = new GoogleApiHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animal_profile, container, false);

        itemsListView = (ListView) view.findViewById(R.id.animal_profile_listView);

        //Inflate header in listView
        headerAnimal = getActivity().getLayoutInflater().inflate(R.layout.fragment_animal_profile_header, null);
        itemsListView.addHeaderView(headerAnimal);

        Animal currAnim = dbHandler2.getAnimal(idAnim);

        TextView animalNameText = (TextView) headerAnimal.findViewById(R.id.animalNameTextView);
        TextView animalSpeciesText = (TextView) headerAnimal.findViewById(R.id.animalSpeciesTextView);
        TextView animalGenderText = (TextView) headerAnimal.findViewById(R.id.animalGenderTextView);
        TextView animalBirthDateText = (TextView) headerAnimal.findViewById(R.id.animalBirthDateTextView);

        animalNameText.setText(currAnim.name);
        animalSpeciesText.setText(currAnim.species);

        String gender = "";
        if(currAnim.gender != null && currAnim.gender.equals("male")){
            gender = "Maschio";
        } else if(currAnim.gender != null && currAnim.gender.equals("female")) {
            gender = "Femmina";
        }
        animalGenderText.setText(gender);

        if(currAnim.birthdate != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM y");
            animalBirthDateText.setText(format.format(currAnim.birthdate));
        }

//        Picasso.with(view.getContext()).setIndicatorsEnabled(true);
        if(currAnim.profilepic != null && !currAnim.profilepic.isEmpty()) {
            Picasso.with(view.getContext())
                    .load(currAnim.profilepic)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.imageViewAnimalProfile));
        } else {
            Picasso.with(view.getContext())
                    .load(R.drawable.defaultpet)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.imageViewAnimalProfile));
        }
        showPostsByAnimal(idAnim);

        getActivity().setTitle(currAnim.name);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEditProfile:
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, AnimalDataFragment.newInstance(idAnim, true))
                        .addToBackStack("")
                        .commit();
                return true;
            default: break;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showPostsByAnimal(String idAuthor) {
        //recupero elenco dei post dal DB
        ArrayList<Post> posts = HomeActivity.dbManager.getPostsByAnimal(idAnim, gApiHelper);

        PostListAdapter adapter = new PostListAdapter(getActivity(), posts);
        itemsListView.setAdapter(adapter);
    }
}
