package com.example.carapp.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.example.carapp.R;
import com.example.carapp.databinding.FragmentBottomSheetBinding;
import com.example.carapp.databinding.FragmentDifferentDropoffBinding;
import com.example.carapp.databinding.FragmentSameDropoffBinding;
import com.example.carapp.fragments.SameDropoffFragment;
import com.example.carapp.utils.MyPagerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyBottomSheetDialog extends BottomSheetDialogFragment {

    private FragmentSameDropoffBinding sameDropoffBinding;
    private FragmentDifferentDropoffBinding differentDropoffBinding;
    private MyPagerAdapter pagerAdapter;
    private FragmentBottomSheetBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet, container, false);

        pagerAdapter = new MyPagerAdapter(this);

        // Add fragments to the pager adapter
        pagerAdapter.addFragment(new SameDropoffFragment());
        pagerAdapter.addFragment(new DifferentDropoffFragment());

        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Same Drop-off");
            } else if (position == 1) {
                tab.setText("Different Drop-off");
            }
        }).attach();

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return binding.getRoot();
    }
}

