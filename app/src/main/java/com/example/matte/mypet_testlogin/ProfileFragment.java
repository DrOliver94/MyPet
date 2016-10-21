package com.example.matte.mypet_testlogin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idUser";

    private String idUser;

    private SharedPreferences shPref;
    private MyPetDB dbHandler;

    private ListView itemsListView;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Id dell'utente da visualizzare.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUser = getArguments().getString(ARG_PARAM1);
        }
        //Recupero delle SharedPreferences
        shPref = getActivity().getSharedPreferences("MyPetPrefs", Context.MODE_PRIVATE);
        dbHandler = new MyPetDB(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        User currUser = dbHandler.getUser(idUser);

        TextView userUserNameProfileText = (TextView) view.findViewById(R.id.userUsernameTextView);
        TextView userNameText = (TextView) view.findViewById(R.id.userNameTextView);
        TextView userSurnameText = (TextView) view.findViewById(R.id.userSurnameTextView);
        TextView userGenderText = (TextView) view.findViewById(R.id.userGenderTextView);
        TextView userBirthDateText = (TextView) view.findViewById(R.id.userBirthDateTextView);

        userUserNameProfileText.setText(shPref.getString("Username", "No Username"));//TODO leggi da db
        userNameText.setText(currUser.name);
        userSurnameText.setText(currUser.surname);
        userGenderText.setText(currUser.gender);
        userBirthDateText.setText(currUser.birthdate);

        itemsListView = (ListView) view.findViewById(R.id.profile_postsListView);

        showPostsByAuthor(idUser);

        if (mListener != null) {
            mListener.onFragmentInteraction("Profilo");
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction("Profilo");
//        }
//    }

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String name);
    }

    /**
     * Mostra i post dell'utente richiesto
     *
     * @param idAuthor id dell'utente di cui recuperare i post
     */
    public void showPostsByAuthor(String idAuthor) {
        //recupero elenco dei post dal DB
        ArrayList<Post> posts = HomeActivity.dbManager.getPostsByAuthor(idAuthor);

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
