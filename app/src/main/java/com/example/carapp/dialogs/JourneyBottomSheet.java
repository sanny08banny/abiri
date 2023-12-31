package com.example.carapp.dialogs;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.carapp.R;
import com.example.carapp.entities.Ride;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class JourneyBottomSheet {
    private BottomSheetDialog bottomSheetDialog;
    private TextView clientNumberTextView, destinationTextView;
    private Button startButton;
    private boolean isJourneyStarted = false;

    public interface JourneyButtonClickListener {
        void onStartButtonClicked(boolean isJourneyStarted);
    }

    private JourneyButtonClickListener buttonClickListener;

    public JourneyBottomSheet(Context context, Ride ride, JourneyButtonClickListener listener) {
        this.buttonClickListener = listener;

        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);

        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);

        clientNumberTextView = view.findViewById(R.id.client_number_text_view);
        destinationTextView = view.findViewById(R.id.destination_text_view);
        startButton = view.findViewById(R.id.start_button);

        // Set ride details
        clientNumberTextView.setText(ride.getClientNumber());
//        destinationTextView.setText(ride.getDestination());

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    isJourneyStarted = !isJourneyStarted;
                    buttonClickListener.onStartButtonClicked(isJourneyStarted);
                    startButton.setText(isJourneyStarted ? "Stop" : "Start Journey");
                }
            }
        });
    }

    public void show() {
        bottomSheetDialog.show();
    }

    public void dismiss() {
        bottomSheetDialog.dismiss();
    }
}


