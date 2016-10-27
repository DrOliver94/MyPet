package com.example.matte.mypet_testlogin;

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
 * {@link ReminderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReminderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView remindersListView;
    private OnFragmentInteractionListener mListener;

    private SharedPreferences shPref;
    private MyPetDB dbHandler;

    public ReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReminderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderFragment newInstance(String param1, String param2) {
        ReminderFragment fragment = new ReminderFragment();
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
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

//        TextView reminderText = (TextView) view.findViewById(R.id.text_reminder);
//        reminderText.setText(shPref.getString("IdUser", "No User") + " "
//                            + shPref.getString("Name", "No Name") + " "
//                            + shPref.getString("Surname", "No surname"));

        remindersListView = (ListView) view.findViewById(R.id.remindersListView);
        getReminders(shPref.getString("IdUser", ""));

        getActivity().setTitle("Promemoria");

        return view;

    }

    private void getReminders(String idUser) {
        //recupero elenco dei post dal DB
        ArrayList<Reminder> reminders = HomeActivity.dbManager.getRemindersByUser(idUser);

//        Log.d("MyPet", remindersListView.toString());

        //caricamento dei post in un array di HashMap
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for(Reminder r : reminders) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", r.id);
            map.put("eventname", r.eventname);
            data.add(map);
            Log.d("Event in map ", r.eventname);
        }

        //risorse
        int res = R.layout.listview_reminder;
        String [] from = {"id", "eventname"};
        int[] to = {R.id.reminder_id, R.id.reminder_eventname};

        //caricamento dei dati nell'adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, res, from, to);
        //adapter.getCount();
        remindersListView.setAdapter(adapter);

    }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(String name);
    }
}
