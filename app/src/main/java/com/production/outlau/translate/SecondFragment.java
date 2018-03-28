package com.production.outlau.translate;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.zip.Inflater;

public class SecondFragment extends ListFragment {

    static AppDatabase db;

    static ListViewAdapter adapter;

    static ArrayList<WordPair> list;

    private TextView listLangSwap;

    private static TextView leftLang;
    private static TextView rightLang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = View.inflate(getActivity(), R.layout.list_head_inflater, null);

        db = new AppDatabase(getActivity());

        leftLang = (TextView)v.findViewById(R.id.lang_left);
        rightLang = (TextView)v.findViewById(R.id.lang_right);
        listLangSwap = (TextView)v.findViewById(R.id.list_lang_swap);

        listLangSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.switchLangs();
                updateTextUI();
                FirstFragment.updateTextUI();
            }
        });

        return v;
    }

    public static void updateTextUI(){
        leftLang.setText(Globals.languages.get(MainFragmentActivity.defaultLang).toString().toUpperCase());
        rightLang.setText(Globals.languages.get(MainFragmentActivity.secondLang).toString().toUpperCase());

        if(list != null)
            createWordPairList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        final Context context = getContext();
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                final int pos = position;

                final Dialog customDialog = new Dialog(getActivity());
                customDialog.setContentView(R.layout.dialog_confirm_inflater);
                Window window = customDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,      WindowManager.LayoutParams.WRAP_CONTENT);    window.setGravity(Gravity.CENTER);

                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getActivity().getTheme();
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                @ColorInt int color = typedValue.data;

                window.setBackgroundDrawable(new ColorDrawable(color));

                TextView cancel = (TextView)customDialog.findViewById(R.id.dialog_cancel);
                TextView accept = (TextView)customDialog.findViewById(R.id.dialog_accept);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.deleteValue(list.get(pos).leftWord, MainFragmentActivity.defaultLang);
                        list.remove(pos);
                        adapter.notifyDataSetChanged();
                        Widget.updateWidget(getActivity());
                        customDialog.dismiss();
                    }
                });

                customDialog.show();

                return true;
            }

        });

        list = new ArrayList<>();
        adapter = new ListViewAdapter(getActivity(),
                list);
        setListAdapter(adapter);
        updateTextUI();
    }

    public static void createWordPairList() {
        list.clear();

        Hashtable<String,String> dbWords = db.getWords(MainFragmentActivity.defaultLang);

        ArrayList<String> tempList = new ArrayList<>();

        for (String key : dbWords.keySet()) {
            tempList.add(key);
        }

        Collections.sort(tempList,String.CASE_INSENSITIVE_ORDER);

        for (String key : tempList) {
            WordPair wordPair = new WordPair(key, dbWords.get(key));
            list.add(wordPair);
        }
        adapter.notifyDataSetChanged();
    }

    public static SecondFragment newInstance(String text) {
        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MainFragmentActivity.copyToClipboard(getActivity(),list.get(position).leftWord);
    }


    @Override
    public void onResume(){
        super.onResume();
    }

}
