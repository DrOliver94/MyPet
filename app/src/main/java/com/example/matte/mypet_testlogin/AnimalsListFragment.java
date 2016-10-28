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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String idUser;

    private ArrayList<Animal> animals;

    private ListView animalsListView;

//    private OnFragmentInteractionListener mListener;

    public AnimalsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AnimalsListFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        //recupero elenco dei post dal DB
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
                //adapterView.getItemAtPosition(i); //TODO si casta a Animal e funziona? Testare

                Animal a = animals.get(i);

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

    public void showAnimalsByUser(String idUser) {
        Log.d("MyPet", animalsListView.toString());

        //caricamento degli animali in un array di HashMap
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for(Animal a : animals) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", a.id);
            map.put("name", a.name);
            map.put("profilepic", a.profilepic);
            data.add(map);
            Log.d("MyPet", a.name + "in arrayList");
        }

        //risorse
        int res = R.layout.listview_animal;
        String [] from = {"id", "name"};
        int[] to = {R.id.animal_id, R.id.animal_name};

        //caricamento dei dati nell'adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, res, from, to);
        //adapter.getCount();
        animalsListView.setAdapter(adapter);
    }

    public void debugUsersAnim() {
        //recupero elenco dei post dal DB
//        ArrayList<Animal> animals = HomeActivity.dbManager.getAnimalsByOwner(idUser);
        ArrayList<String> strs = HomeActivity.dbManager.debugUsersAnimals();

//        Log.d("MyPet", animalsListView.toString());

        //caricamento degli animali in un array di HashMap
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for(String s: strs) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", s);
//            map.put("name", a.name);
            //TODO complete
            data.add(map);
//            Log.d("MyPet", a.name + "in arrayList");
        }

        //risorse
        int res = R.layout.listview_animal;
        String [] from = {"id"};
        int[] to = {R.id.animal_id};

        //caricamento dei dati nell'adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, res, from, to);
        adapter.getCount();
        animalsListView.setAdapter(adapter);
    }

}
