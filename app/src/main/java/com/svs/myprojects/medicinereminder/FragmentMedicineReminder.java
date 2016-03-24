package com.svs.myprojects.medicinereminder;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by snehalsutar on 2/15/16.
 */
public class FragmentMedicineReminder extends Fragment implements MedicineViewAdapterInterface, View.OnClickListener {

    public String LOG_TAG = "svs_me";
    public String FRAGMENT_ADD_MED_TAG = "add_med";
    public String mJsonArray;
    public ArrayList<MedicineViewItems> listData;
    private View mRootView;
    private Context mContext;
    private FloatingActionButton fab;
    PatientRecordToAddMedInterface patientRecordToAddMedInterface;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medicine_reminder, container, false);
        mRootView = rootView;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        patientRecordToAddMedInterface = (PatientRecordToAddMedInterface) getActivity();
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_pill);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);
        final RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        // init layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        listData = new ArrayList<>();
        setListData();
        final MedicineViewAdapter adapter = new MedicineViewAdapter(listData, getActivity(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // callback for swipe to dismiss, removing item from data and adapter

                // Remove the alarm from the system.
                Utility.removeAlarmFromSystem(mContext,listData.get(viewHolder.getAdapterPosition()));
                // Delete the alarm from list view.
                listData.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
                // Save the new list of alarms with removed alarm to Shared Preferences.
                Utility.saveAlarmListToSharedPref(mContext, listData);
            }

        });

        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView);
    }


    public void setListData() {
        ArrayList<MedicineViewItems> medicineViewItemsTemp = Utility.getAlarmListArrayList(mContext);
        if (medicineViewItemsTemp != null) {
            listData.addAll(medicineViewItemsTemp);
        }
    }


    @Override
    public void modifyButtonClick(int position) {
        mJsonArray = Utility.getAlarmListArrayListStringFormat(mContext);
        patientRecordToAddMedInterface.modifyExistingAlarm(mJsonArray, position);
    }

    public void makeToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_pill:
                mJsonArray = Utility.getAlarmListArrayListStringFormat(mContext);
                patientRecordToAddMedInterface.patientRecToAddMedCommunication(mJsonArray);
                break;
        }
    }
}
