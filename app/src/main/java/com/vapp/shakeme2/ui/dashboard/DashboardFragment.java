package com.vapp.shakeme2.ui.dashboard;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vapp.shakeme2.R;

public class DashboardFragment extends Fragment
    {
        public TextView l1;
        public TextView l2;
        private DashboardViewModel dashboardViewModel;

        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
                View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
                l1 = root.findViewById(R.id.hl1);
                l1.setMovementMethod(LinkMovementMethod.getInstance());
                l2 = root.findViewById(R.id.hl1);
                l2.setMovementMethod(LinkMovementMethod.getInstance());
                return root;
            }
    }
