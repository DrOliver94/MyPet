package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the/
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView feedListView;

    private SharedPreferences shPref;

    private GoogleApiHelper gApiHelper;

    public FeedFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
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

        setHasOptionsMenu(true);    //Per mostrare il menu specifico

        gApiHelper = new GoogleApiHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedListView = (ListView) view.findViewById(R.id.feed_postsListView);

//        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Post p = (Post) adapterView.getItemAtPosition(i);
//            }
//        });

        getFeedPost(shPref.getString("IdUser", ""), gApiHelper);

        getActivity().setTitle("Feed");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_feed_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAddPost:
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new PostDataFragment())
                        .addToBackStack(null)
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

    public void getFeedPost(String idUser, GoogleApiHelper gApiHelper) {
        //recupero elenco dei post dal DB
        ArrayList<Post> posts = HomeActivity.dbManager.getPostsByUser(idUser, gApiHelper);

        PostListAdapter adapter = new PostListAdapter(getActivity(), posts);
        feedListView.setAdapter(adapter);
    }

}
