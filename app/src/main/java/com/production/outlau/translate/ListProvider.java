package com.production.outlau.translate;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class ListProvider implements RemoteViewsFactory {

    public static ArrayList<WordPair> list = new ArrayList<>();
    private Context context = null;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        createWordPairList(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_inflater);
        WordPair listItem = list.get(position);
        remoteView.setTextViewText(R.id.list_left, listItem.leftWord);
        remoteView.setTextViewText(R.id.list_right, listItem.rightWord);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

    public static void createWordPairList(Context context) {
        list.clear();

        AppDatabase db = new AppDatabase(context);
        Hashtable<String,String> dbWords = db.getWords("en");

        ArrayList<String> tempList = new ArrayList<>();

        for (String key : dbWords.keySet()) {
            tempList.add(key);
        }

        Collections.sort(tempList,String.CASE_INSENSITIVE_ORDER);

        for (String key : tempList) {
            WordPair wordPair = new WordPair(key, dbWords.get(key));
            list.add(wordPair);
        }
    }
    

}
