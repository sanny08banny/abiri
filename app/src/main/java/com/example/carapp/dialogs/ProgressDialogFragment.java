package com.example.carapp.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.carapp.R;

public class ProgressDialogFragment extends DialogFragment {

    private TextView dotsTextView;
    private Handler handler;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.layout_progress_dialog, null);
        dotsTextView = view.findViewById(R.id.dotsTextView);
        handler = new Handler(Looper.getMainLooper());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        startDotAnimation();
    }

    private void startDotAnimation() {
        final int DOT_ANIMATION_INTERVAL = 300; // milliseconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dotsTextView.append(".");
                if (dotsTextView.getText().length() > 3) {
                    dotsTextView.setText(".");
                }
                handler.postDelayed(this, DOT_ANIMATION_INTERVAL);
            }
        }, DOT_ANIMATION_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
}

