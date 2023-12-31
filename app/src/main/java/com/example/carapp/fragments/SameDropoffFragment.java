package com.example.carapp.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.R;
import com.example.carapp.databinding.FragmentSameDropoffBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SameDropoffFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SameDropoffFragment extends Fragment {
    private FragmentSameDropoffBinding sameDropoffBinding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private long fromTimestamp = 0;
    private long toTimestamp = 0;

    public SameDropoffFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SameDropoffFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SameDropoffFragment newInstance(String param1, String param2) {
        SameDropoffFragment fragment = new SameDropoffFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sameDropoffBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_same_dropoff, container, false);


        sameDropoffBinding.fromTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(true);
            }
        });

        sameDropoffBinding.toTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false);
            }
        });
        return sameDropoffBinding.getRoot();
    }

    private void showDateTimePicker(final boolean isFromDateTime) {
        MaterialDatePicker<Long> builder = MaterialDatePicker.Builder.datePicker().build();
        builder.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Handle the date selection
                if (isFromDateTime) {
                    fromTimestamp = selection;
                    sameDropoffBinding.fromTimeBtn.setText(builder.getHeaderText());
                } else {
                    toTimestamp = selection;
                    sameDropoffBinding.toTimeBtn.setText(builder.getHeaderText());
                }


                // Show the time picker
                showTimePicker(isFromDateTime);
            }
        });

        builder.show(getChildFragmentManager(), builder.toString());
    }

    private void showTimePicker(final boolean isFromDateTime) {
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
        builder.setTimeFormat(TimeFormat.CLOCK_24H);

        builder.setHour(12); // Set the initial hour (you can change this)
        builder.setMinute(0); // Set the initial minute (you can change this)

        MaterialTimePicker timePicker = builder.build();
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Handle the time selection
                // You can combine the date and time timestamps here

                long selectedTimestamp = combineDateAndTime(isFromDateTime, hour, minute);

                if (isFromDateTime) {
                    fromTimestamp = selectedTimestamp;
                    sameDropoffBinding.fromTimeBtn.setText(String.format("%s %02d:%02d", sameDropoffBinding.fromTimeBtn.getText(), hour, minute));
                } else {
                    toTimestamp = selectedTimestamp;
                    sameDropoffBinding.toTimeBtn.setText(String.format("%s %02d:%02d", sameDropoffBinding.toTimeBtn.getText(), hour, minute));
                }

                // You can now use the "fromTimestamp" and "toTimestamp" values

                timePicker.dismiss();
            }
        });

        timePicker.show(getChildFragmentManager(), timePicker.toString());
    }

    private long combineDateAndTime(boolean isFromDateTime, int hour, int minute) {
        // You can implement this method to combine the date and time as per your requirements
        // For example, you can use a Calendar instance to set the date and time and get the timestamp
        // Here, I'm assuming a date (year, month, day) of January 1, 1970 (Unix epoch) for simplicity
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(isFromDateTime ? fromTimestamp : toTimestamp);
        calendar.set(1970, Calendar.JANUARY, 1, hour, minute);
        return calendar.getTimeInMillis();
    }
}