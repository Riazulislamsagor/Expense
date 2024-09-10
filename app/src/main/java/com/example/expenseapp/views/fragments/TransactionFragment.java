
package com.example.expenseapp.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expenseapp.R;
import com.example.expenseapp.Adapters.TransactionAdapter;
import com.example.expenseapp.Viewmodel.MainViewModel;
import com.example.expenseapp.databinding.FragmentTransactionBinding;
import com.example.expenseapp.Models.Transaction;
import com.example.expenseapp.utils.Constants;
import com.example.expenseapp.utils.Helper;

import com.example.expenseapp.views.activities.MainActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import io.realm.RealmResults;


public class TransactionFragment extends Fragment {

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentTransactionBinding binding;

    Calendar calendar;

    public MainViewModel viewModel;

    /*
    0 = Daily
    1 = Monthly
    2 = Calendar
    3 = Summary
    4 = Notes
     */





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater);
        viewModel  = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        calendar = Calendar.getInstance();
        UpdateDate();
        binding.nextDateBtn.setOnClickListener(c->{
            if (Constants.SELECTED_TAB==Constants.DAILY){
                calendar.add(Calendar.DATE,1);

            }else if(Constants.SELECTED_TAB==Constants.MONTHLY){
                calendar.add(Calendar.MONTH,1);

            }
            UpdateDate();
        });
        binding.previousDateBtn.setOnClickListener(c->{
            if (Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE,-1);
            }else if(Constants.SELECTED_TAB==Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1);
            }
            UpdateDate();
        });





        binding.floatingActionButton.setOnClickListener(c->{
            new AddTransactionFragment().show(getParentFragmentManager(),null);
        });
        binding.transactionsList.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.transactions.observe(getViewLifecycleOwner(), new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {
                TransactionAdapter transactionAdapter=new TransactionAdapter(getContext(),transactions);
                binding.transactionsList.setAdapter(transactionAdapter);

                if (transactions.size()>0){
                    binding.emptyState.setVisibility(View.GONE);

                }else {
                    binding.emptyState.setVisibility(View.VISIBLE);
                }

            }
        });
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Monthly")){
                    Constants.SELECTED_TAB=1;
                    UpdateDate();
                }else if(tab.getText().equals("Daily")){
                    Constants.SELECTED_TAB=0;
                    UpdateDate();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewModel.totalIncome.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.incomeLbl.setText(String.valueOf(aDouble));
            }
        });
        viewModel.totalExpense.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.expenseLbl.setText(String.valueOf(aDouble));
            }
        });
        viewModel.totalAmount.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.totalLbl.setText(String.valueOf(aDouble));
            }
        });
        viewModel.getTransactions(calendar);








        return binding.getRoot();
    }
    void UpdateDate() {
        if(Constants.SELECTED_TAB == Constants.DAILY) {
            binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        }
        viewModel.getTransactions(calendar);
    }

    }

