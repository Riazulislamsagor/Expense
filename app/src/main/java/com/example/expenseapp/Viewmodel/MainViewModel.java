package com.example.expenseapp.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.expenseapp.Models.Transaction;
import com.example.expenseapp.utils.Constants;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<RealmResults<Transaction>> transactions = new MutableLiveData<>();
    public MutableLiveData<RealmResults<Transaction>> categoriesTransactions = new MutableLiveData<>();

    public MutableLiveData<Double> totalIncome = new MutableLiveData<>();
    public MutableLiveData<Double> totalExpense = new MutableLiveData<>();
    public MutableLiveData<Double> totalAmount = new MutableLiveData<>();

    Realm realm;
    Calendar calendar;
    public MainViewModel(@NonNull Application application) {
        super(application);
        Realm.init(application);
        setupDatabase();
    }
    public void getTransactions(Calendar calendar,String type){
        this.calendar=calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        RealmResults<Transaction> newtransactions=null;
        if (Constants.SELECTED_TAB_STATS==Constants.DAILY){



            newtransactions=realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date",calendar.getTime())
                    .lessThan("date",new Date(calendar.getTime().getTime()+(24*60*60*1000)))
                    .equalTo("type",type)
                    .findAll();



        }
        else if (Constants.SELECTED_TAB_STATS==Constants.MONTHLY){

            calendar.set(Calendar.DAY_OF_MONTH,0);
            Date starttime=calendar.getTime();
            calendar.add(Calendar.MONTH,1);
            Date endtime=calendar.getTime();
            newtransactions=realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date",starttime)
                    .equalTo("type",type)
                    .lessThan("date",endtime)
                    .findAll();


        }


        categoriesTransactions.setValue(newtransactions);

    }

    public void getTransactions(Calendar calendar){
        this.calendar=calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        double income=0;
        double expense=0;
        double total=0;
        RealmResults<Transaction> newtransactions=null;
        if (Constants.SELECTED_TAB==Constants.DAILY){



         newtransactions=realm.where(Transaction.class)
                .greaterThanOrEqualTo("date",calendar.getTime())
                .lessThan("date",new Date(calendar.getTime().getTime()+(24*60*60*1000)))
                .findAll();
         income=realm.where(Transaction.class)
                .greaterThanOrEqualTo("date",calendar.getTime())
                .lessThan("date",new Date(calendar.getTime().getTime()+(24*60*60*1000)))
                .equalTo("type",Constants.INCOME)
                .sum("amount")
                .doubleValue();
         expense=realm.where(Transaction.class)
                .greaterThanOrEqualTo("date",calendar.getTime())
                .lessThan("date",new Date(calendar.getTime().getTime()+(24*60*60*1000)))
                .equalTo("type",Constants.EXPENSE)
                .sum("amount")
                .doubleValue();
         total=realm.where(Transaction.class)
                .greaterThanOrEqualTo("date",calendar.getTime())
                .lessThan("date",new Date(calendar.getTime().getTime()+(24*60*60*1000)))
                .sum("amount")
                .doubleValue();


        }
        else if (Constants.SELECTED_TAB==Constants.MONTHLY){

            calendar.set(Calendar.DAY_OF_MONTH,0);
            Date starttime=calendar.getTime();
            calendar.add(Calendar.MONTH,1);
            Date endtime=calendar.getTime();
            newtransactions=realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date",starttime)
                    .lessThan("date",endtime)
                    .findAll();
            income=realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date",starttime)
                    .lessThan("date",endtime)
                    .equalTo("type",Constants.INCOME)
                    .sum("amount")
                    .doubleValue();
            expense=realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date",starttime)
                    .lessThan("date",endtime)
                    .equalTo("type",Constants.EXPENSE)
                    .sum("amount")
                    .doubleValue();
            total=realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date",starttime)
                    .lessThan("date",endtime)
                    .sum("amount")
                    .doubleValue();

        }

        totalIncome.setValue(income);
        totalExpense.setValue(expense);
        totalAmount.setValue(total);
        transactions.setValue(newtransactions);

    }

    public void deleteTransaction(Transaction transaction) {
        realm.beginTransaction();
        transaction.deleteFromRealm();
        realm.commitTransaction();
        getTransactions(calendar);
    }


    public void addTransactions(Transaction transaction){
        realm.beginTransaction();

        realm.copyToRealmOrUpdate(transaction);

        realm.commitTransaction();
    }
    void setupDatabase(){

        realm=Realm.getDefaultInstance();
    }

}