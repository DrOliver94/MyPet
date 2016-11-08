package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
  * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "idUser";

    private String idUser;

    private SharedPreferences shPref;

    private ListView itemsListView;
    private View header;

    public ProfileFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idUser Id dell'utente da visualizzare.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String idUser) {
        ProfileFragment fragment = new ProfileFragment();
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

        //Recupero delle SharedPreferences
        shPref = getActivity().getSharedPreferences("MyPetPrefs", Context.MODE_PRIVATE);

        //Mostra il menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        itemsListView = (ListView) view.findViewById(R.id.profile_postsListView);

        //Inflate header in listView
        header = getActivity().getLayoutInflater().inflate(R.layout.fragment_user_profile_header, null);
        itemsListView.addHeaderView(header);

        User currUser = HomeActivity.dbManager.getUser(idUser);     //Read user data from DB

        TextView userUserNameProfileText = (TextView) header.findViewById(R.id.userUsernameTextView);
        TextView userNameText = (TextView) header.findViewById(R.id.userNameTextView);
        TextView userSurnameText = (TextView) header.findViewById(R.id.userSurnameTextView);
        TextView userGenderText = (TextView) header.findViewById(R.id.userGenderTextView);
        TextView userBirthDateText = (TextView) header.findViewById(R.id.userBirthDateTextView);

        userUserNameProfileText.setText(currUser.username);
        userNameText.setText(currUser.name);
        userSurnameText.setText(currUser.surname);
        userGenderText.setText(currUser.gender);

        SimpleDateFormat format = new SimpleDateFormat("dd LLLL y");
        userBirthDateText.setText(format.format(currUser.birthdate));

        //Immagine
        Picasso.with(view.getContext()).setIndicatorsEnabled(true);
        Picasso.with(view.getContext())
                .load(currUser.profilepic)
                .transform(new CropCircleTransformation())
                .resize(512, 512)
                .centerCrop()
                .into((ImageView) view.findViewById(R.id.imageViewUserProfile));

        showPostsByAuthor(idUser);

        getActivity().setTitle(currUser.username);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_profile, menu);

        //Check se si può modificare il profilo
        String idLoggedUser = shPref.getString("IdUser", "");
        if(!idUser.equals(idLoggedUser)){ //Se l'user non visualizza il proprio profilo
            menu.findItem(R.id.menuEditProfile).setVisible(false).setEnabled(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEditProfile:
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, UserDataFragment.newInstance(idUser, true))
                        .addToBackStack(null)
                        .commit();
                return true;
            default: break;
        }
        return false;
    }

//    public static void justifyListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter adapter = listView.getAdapter();
//
//        if (adapter == null) {
//            return;
//        }
//        ViewGroup vg = listView;
//        int totalHeight = 0;
//        int measure =0;
//        for (int i = 0; i < adapter.getCount(); i++) {
//            View listItem = adapter.getView(i, null, vg);
//            listItem.measure(0, 0);
//            measure = listItem.getMeasuredHeight();
//            totalHeight += measure;
//        }
//        //totalHeight+=measure/2;
//        ViewGroup.LayoutParams par = listView.getLayoutParams();
//        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
//        listView.setLayoutParams(par);
//        listView.requestLayout();
//    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction("Profilo");
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

        PostListAdapter adapter = new PostListAdapter(getActivity(), posts);
        itemsListView.setAdapter(adapter);
    }

}
