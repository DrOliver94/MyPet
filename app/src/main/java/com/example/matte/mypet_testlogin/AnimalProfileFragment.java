package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnimalProfileFragment.OnFragmentInteractionListener} interface
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

    private OnFragmentInteractionListener mListener;

    public AnimalProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 ID dell'animale da visualizzare.
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animal_profile, container, false);

        Animal currAnim = dbHandler2.getAnimal(idAnim);

        TextView animalNameText = (TextView) view.findViewById(R.id.animalNameTextView);
        TextView animalSpeciesText = (TextView) view.findViewById(R.id.animalSpeciesTextView);
        TextView animalGenderText = (TextView) view.findViewById(R.id.animalGenderTextView);
        TextView animalBirthDateText = (TextView) view.findViewById(R.id.animalBirthDateTextView);

        animalNameText.setText(currAnim.name);
        animalSpeciesText.setText(currAnim.species);
        animalGenderText.setText(currAnim.gender);
        animalBirthDateText.setText(currAnim.birthdate);

        itemsListView = (ListView) view.findViewById(R.id.profile_postsListView);

//        showPostsByAnimal(idAnim);

        if (mListener != null) {
            mListener.onFragmentInteraction("Profilo Animale");
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
/*    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction("Profilo Animale");
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String name);
    }

    public void showPostsByAnimal(String idAuthor) {
        //recupero elenco dei post dal DB
        ArrayList<Post> posts = HomeActivity.dbManager.getPostsByAnimal(idAnim);

        Log.d("MyPet", itemsListView.toString());

        //caricamento dei post in un array di HashMap
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for(Post p : posts) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", p.id);
            map.put("text", p.text);
            data.add(map);
            Log.d("MyPet", p.text);
        }

        //risorse
        int res = R.layout.listview_post;
        String [] from = {"id", "text"};
        int[] to = {R.id.post_id, R.id.post_text};

        //caricamento dei dati nell'adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, res, from, to);
        adapter.getCount();
        itemsListView.setAdapter(adapter);
    }


}



/*





 */