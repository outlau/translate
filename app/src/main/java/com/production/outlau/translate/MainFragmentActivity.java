package com.production.outlau.translate;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainFragmentActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static String defaultLang = "en";
    public static String secondLang = "sv";

    boolean navBarOpen = false;
    boolean deleteDataDialogOpen = false;
    public static DrawerLayout drawer;


    InputMethodManager imm;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = sp.edit();

        db = new AppDatabase(getApplicationContext());

        setTheme(getStyle(sp.getString("theme", "dark")));
        setContentView(R.layout.activity_slide);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
                if(!Globals.expanded) {
                    if (position == 1) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    } else if (position == 0) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.openNavBar, R.string.closeNavBar);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(!navBarOpen) {
                    navBarOpen = true;
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                if(!deleteDataDialogOpen)
                    closeNavBar();

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void closeNavBar(){

        navBarOpen = false;
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        System.out.println("CLOSE");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change_theme) {
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.themeName, outValue, true);
            if ("dark".equals(outValue.string)) {
                //super.getTheme().applyStyle(R.style.AppTheme_Light,true);
                System.out.println("DARK");
                editor.putString("theme", "light");
                editor.commit();
            }
            else{
                //super.getTheme().applyStyle(R.style.AppTheme_Dark,true);
                editor.putString("theme", "dark");
                editor.commit();
                System.out.println("LIGHT");

            }

            Intent intent = getIntent();
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
            //super.getTheme().applyStyle(R.style.AppTheme_Light,true);

        }
        else if(id == R.id.nav_delete_table){
            deleteDataDialogOpen = true;
            final Dialog customDialog = new Dialog(MainFragmentActivity.this);
            customDialog.setContentView(R.layout.dialog_confirm_inflater);
            Window window = customDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,      WindowManager.LayoutParams.WRAP_CONTENT);    window.setGravity(Gravity.CENTER);

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            @ColorInt int color = typedValue.data;

            window.setBackgroundDrawable(new ColorDrawable(color));

            TextView msg = (TextView)customDialog.findViewById(R.id.dialog_msg);
            msg.setText("Are you sure you want to delete everything?");
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
                    db.deleteAllInTable();
                    customDialog.dismiss();
                    SecondFragment.updateTextUI();
                }
            });
            customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    deleteDataDialogOpen = false;
                    closeNavBar();
                }
            });
            customDialog.show();

        }
        /*else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getStyle(String theme){
        if(theme.matches("dark")){
            System.out.println("THEME DARK");
            return R.style.AppTheme_Dark;
        }
        else{
            System.out.println("THEME LIGHT");
            return R.style.AppTheme_Light;
        }
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