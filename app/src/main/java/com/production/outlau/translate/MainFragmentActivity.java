package com.production.outlau.translate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.inputmethod.InputMethodManager;

public class MainFragmentActivity extends FragmentActivity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static String defaultLang = "en";
    public static String secondLang = "sv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        sp = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = sp.edit();


        String defaultLangStr = sp.getString("defaultLang", "en");

        if(defaultLangStr.matches("sv"))
            switchLangs();

        System.out.println("DEFAULT : "+defaultLangStr);

        final ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("PAGE SELECT: " + position);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(!Globals.expanded) {
                    if (position == 1) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    } else if (position == 0) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            System.out.println("POSITION: "+pos);
            switch (pos) {
                case 0:
                    return FirstFragment.newInstance("FirstFragment, Instance 1");
                case 1:
                    return SecondFragment.newInstance("SecondFragment, Instance 1");
                default:
                    return SecondFragment.newInstance("SecondFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


    }

    public static void switchLangs(){
        String tempLang = defaultLang;
        defaultLang = secondLang;
        secondLang = tempLang;
        editor.putString("defaultLang", defaultLang);
        editor.commit();
    }

    public static void copyToClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

}