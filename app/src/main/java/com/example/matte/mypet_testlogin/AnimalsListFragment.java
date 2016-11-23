package com.example.matte.mypet_testlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link AnimalsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimalsListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String idUser;

    private ArrayList<Animal> animals;

    private ListView animalsListView;

    public AnimalsListFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AnimalsListFragment.
     */
    public static AnimalsListFragment newInstance(String param1) {
        AnimalsListFragment fragment = new AnimalsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recupero idUser passato dal chiamante
        if (getArguments() != null) {
            idUser = getArguments().getString(ARG_PARAM1);
        }

        //recupero elenco degli animali dal DB
        animals = HomeActivity.dbManager.getAnimalsByOwner(idUser);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animals_list, container, false);

        animalsListView = (ListView) view.findViewById(R.id.animalsListView);

        animalsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Animal a = (Animal) adapterView.getItemAtPosition(i);
//                Animal a = animals.get(i);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, AnimalProfileFragment.newInstance(a.id))
                        .addToBackStack(null)
                        .commit();
            }
        });

        showAnimalsByUser(idUser);
//        debugUsersAnim();

        getActivity().setTitle("Elenco Animali");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_animal_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAddAnimal:
                getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, AnimalDataFragment.newInstance("-1", false))
                    .addToBackStack(null)
                    .commit();
                return true;
            default: break;
        }
        return false;
    }

    public void showAnimalsByUser(String idUser) {
        AnimalListAdapter ala = new AnimalListAdapter(getActivity(), animals);
        animalsListView.setAdapter(ala);
    }

}
