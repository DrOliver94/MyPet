package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.StrictMode;
import android.util.Log;
import android.net.Uri;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView feedListView;
    private OnFragmentInteractionListener mListener;

    private SharedPreferences shPref;
    private MyPetDB dbHandler;

    private ServerComm srv;

    public FeedFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Recupero delle SharedPreferences
        shPref = getActivity().getSharedPreferences("MyPetPrefs", Context.MODE_PRIVATE);

        dbHandler = new MyPetDB(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedListView = (ListView) view.findViewById(R.id.feed_postsListView);
        getFeedPost(shPref.getString("IdUser", ""));

        if (mListener != null) {
            mListener.onFragmentInteraction("Feed");
        }

        //Test per controlli sessione.
//        srv = new ServerComm();
//        Button btnTest = (Button) view.findViewById(R.id.feed_button);
//        btnTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    //Lo strictmode evita che il thread principale vada troppo tempo in pausa con operazioni sulla rete
                      //Questo codice lo disabilita
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    JSONObject jObj = srv.makePostRequest("test=one");
//
//                    Log.d("MyPet", jObj.getString("test"));
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        //Scrittura token (DEBUG)
//        TextView usernameProfileText = (TextView) view.findViewById(R.id.feed_title);
//        usernameProfileText.setText(shPref.getString("Token", "No token"));

        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction("");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String name);
    }


    public void getFeedPost(String idUser) {
        //recupero elenco dei post dal DB
        ArrayList<Post> posts = HomeActivity.dbManager.getPostsByUser(idUser);

        Log.d("MyPet", feedListView.toString());

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
        //adapter.getCount();
        feedListView.setAdapter(adapter);
    }

}
