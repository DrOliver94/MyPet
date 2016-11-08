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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "IdAnim";

    // TODO: Rename and change types of parameters
    private String idAnim;

    private MyPetDB dbHandler2;

    private ListView itemsListView;
    private View headerAnimal;
//    private OnFragmentInteractionListener mListener;

    public AnimalProfileFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idAnim ID dell'animale da visualizzare.
     * @return A new instance of fragment AnimalProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if(currAnim.gender.equals("male")){
            gender = "Maschio";
        } else if(currAnim.gender.equals("female")) {
            gender = "Femmina";
        }
        animalGenderText.setText(gender);

        SimpleDateFormat format = new SimpleDateFormat("dd LLLL y");
        animalBirthDateText.setText(format.format(currAnim.birthdate));

        Picasso.with(view.getContext()).setIndicatorsEnabled(true);
        Picasso.with(view.getContext())
                .load(currAnim.profilepic)
                .transform(new CropCircleTransformation())
                .resize(512, 512)
                .centerCrop()
                .into((ImageView) view.findViewById(R.id.imageViewAnimalProfile));

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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(String name);
//    }

    public void showPostsByAnimal(String idAuthor) {
        //recupero elenco dei post dal DB
        ArrayList<Post> posts = HomeActivity.dbManager.getPostsByAnimal(idAnim);

//        Log.d("MyPet", itemsListView.toString());
//
//        //caricamento dei post in un array di HashMap
//        ArrayList<HashMap<String, String>> data = new ArrayList<>();
//        for(Post p : posts) {
//            HashMap<String, String> map = new HashMap<>();
//            map.put("id", p.id);
//            map.put("text", p.text);
//            data.add(map);
//            Log.d("MyPet", p.text);
//        }
//
//        //risorse
//        int res = R.layout.listview_post;
//        String [] from = {"id", "text"};
//        int[] to = {R.id.post_id, R.id.post_text};

        //caricamento dei dati nell'adapter
//        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, res, from, to);
//        adapter.getCount();
//        itemsListView.setAdapter(adapter);

        PostListAdapter adapter = new PostListAdapter(getActivity(), posts);
        itemsListView.setAdapter(adapter);
    }


}
