package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    private static final String ARG_PARAM1 = "idUser";
    private String idUser;

    ListView friendsListView;

    public FriendsFragment() { }

    /**
     * Fragment showing the list of friends of the given user
     *
     * @param idUser id of the user
     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance(String idUser) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUser = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        getActivity().setTitle("Amici");

        friendsListView = (ListView) view.findViewById(R.id.friendsListView);

        getFriends(idUser);

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            User data = (User) adapterView.getItemAtPosition(i); //TODO si casta a Animal e funziona? Testare

//          Animal a = animals.get(i);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, ProfileFragment.newInstance(data.id))
                    .addToBackStack(null)
                    .commit();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    /**
     * Read from the DB the list of friends and inflate it into a listView
     *
     * @param idUser
     */
    public void getFriends(String idUser) {

        HomeActivity.dbManager.getFriendsByUser(idUser);
        ArrayList<User> users = HomeActivity.dbManager.getFriendsByUser(idUser);
        UserListAdapter ula = new UserListAdapter(getActivity(), users);
        friendsListView.setAdapter(ula);

    }

}

