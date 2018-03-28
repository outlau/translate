package com.production.outlau.translate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.*;
import java.lang.*;
import java.lang.reflect.Method;

public class FirstFragment extends Fragment {

    static EditText input;
    static TextView output;
    ImageButton langSwitch;
    ImageButton inputClearButton;
    ImageButton addPairButton;
    ImageButton inputCopy;
    ImageButton navBarToggleButton;

    static TextView inputTextView;
    static TextView outputTextView;

    boolean checkAdded = false;

    LinearLayout expandCont;

    static Translator translator = new Translator();

    ArrayList<Integer> animations;

    AppDatabase db;

    InputMethodManager imm;

    View layout;

    Handler onTouchTimerHandler;





    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.first_fragment, container, false);


        db = new AppDatabase(getActivity());

        onTouchTimerHandler = new Handler();


        animations = new ArrayList<>();
        animations.add(R.anim.add_rotate);
        animations.add(R.anim.add_scaledown);
        animations.add(R.anim.add_scaleup);
        System.out.println("add_rotate " +R.anim.add_rotate);
        System.out.println("add_scaledown " +R.anim.add_scaledown);
        System.out.println("add_scaleup" +R.anim.add_scaleup);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        input = (EditText)layout.findViewById(R.id.input_edittext);
        output = (TextView)layout.findViewById(R.id.output_text);
        langSwitch = (ImageButton)layout.findViewById(R.id.lang_swap);
        inputClearButton= (ImageButton)layout.findViewById(R.id.input_clear);
        addPairButton = (ImageButton)layout.findViewById(R.id.addpair_button);
        inputCopy = (ImageButton)layout.findViewById(R.id.input_copy);
        inputTextView = (TextView)layout.findViewById(R.id.input_lang_text);
        outputTextView = (TextView)layout.findViewById(R.id.output_lang_text);
        expandCont = (LinearLayout)layout.findViewById(R.id.expand_container);
        navBarToggleButton = (ImageButton)layout.findViewById(R.id.nav_bar_toggle_button);


        /*

        onTouchListen(addPairButton);
        onTouchListen(langSwitch);
        onTouchListen(inputClearButton);

        onTouchListen(inputCopy);

        onTouchListen(output);

*/




        updateTextUI();


        onTouchListen(langSwitch);
        langSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ON CLICK");
                MainFragmentActivity.switchLangs();
                updateTextUI();
                SecondFragment.updateTextUI();
            }
        });


        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Globals.expanded) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    input.setFocusableInTouchMode(true);
                    input.setFocusable(true);
                    resetExpandCont();
                    Globals.expanded = false;
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                //String inputText = input.getText().toString();
                //String[] splitInput = inputText.split(" ");
                translator.translate(MainFragmentActivity.defaultLang,MainFragmentActivity.secondLang,input.getText().toString(),getActivity(),output);
                if(checkAdded){
                    addPairButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_add,getActivity().getTheme()));
                    checkAdded = false;
                }
            }
        });

        onTouchListen(inputClearButton);
        inputClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
                output.setText("");
                output.scrollTo(0,0);
                Animation anim =
                        AnimationUtils.loadAnimation(getActivity(), R.anim.clear);
                inputClearButton.startAnimation(anim);
            }
        });

        output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.copyToClipboard(getActivity(),output.getText().toString());
            }
        });
        output.setMovementMethod(new ScrollingMovementMethod());

        onTouchListen(inputCopy);
        inputCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.copyToClipboard(getActivity(),input.getText().toString());
                Animation anim =
                        AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                inputCopy.startAnimation(anim);
            }
        });

        onTouchListen(addPairButton);
        addPairButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String inputText = input.getText().toString();
                 String outputText = output.getText().toString();
                 if (!inputText.isEmpty() && inputText.trim().length() > 0 && !checkAdded) {
                     addWordPair(inputText, outputText, MainFragmentActivity.defaultLang);
                     addWordPairAnim(new ArrayList<>(animations));
                 }
             }
        });

        onTouchListen(navBarToggleButton);
        navBarToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.drawer.openDrawer(Gravity.LEFT);
            }
        });
/*
                    An
                    addPairButton.
                            animate().
                            rotation(45).
                            setDuration(100).
                            scaleX(0.1f).
                            scaleY(0.1f).
                            setDuration(100).
                            scaleX(1f).
                            scaleY(1f).
                            rotation(0).
                            setDuration(100);
                            */
                            /*
                            setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation){
                                    addPairButton.
                                            animate().
                                            scaleX(0.1f).
                                            scaleY(0.1f).
                                            setDuration(100).
                                            setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    System.out.println("CHECH");
                                                    addPairButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_check,null));
                                                    addPairButton.
                                                            animate().
                                                            scaleX(1f).
                                                            scaleY(1f).
                                                            rotation(0).
                                                            setDuration(100);
                                                    checkAdded = true;
                                                }
                                            });

                                }
                            });
                            */

        return layout;
    }

    private void addWordPairAnim(ArrayList<Integer> anims){
        final int anim = anims.remove(0);
        final ArrayList<Integer> animList = anims;
        System.out.println("anim : "+anim);
        System.out.println("animList : "+animList);


        Animation animation =
                AnimationUtils.loadAnimation(getActivity(), anim);
        addPairButton.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                if(animList.size() == 1)
                    addPairButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_check,getActivity().getTheme()));
                if(!animList.isEmpty())
                    addWordPairAnim(animList);
                else
                    checkAdded = true;
            }
        });
    }


    public static void updateTextUI(){
        inputTextView.setText(Globals.languages.get(MainFragmentActivity.defaultLang));
        outputTextView.setText(Globals.languages.get(MainFragmentActivity.secondLang));
        input.setText(output.getText().toString());
    }


    private void onTouchListen(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {

            ImageButton im;
            boolean pressed;
            Runnable onTouchTimerRunnable;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        final View touched = v;

                        pressed = true;

                        System.out.println("ACTION DOWN");
                        try {
                            im = (ImageButton) view;
                            im.setColorFilter(R.color.anim_color);
                        }
                        catch (ClassCastException classCastException){

                        }
                        onTouchTimerRunnable= new Runnable() {
                            @Override
                            public void run() {
                                actionUpNoClick(touched, im);
                                pressed = false;
                            }
                        };
                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("ACTION MOVE");
                        onTouchTimerHandler.removeCallbacks(onTouchTimerRunnable);
                        onTouchTimerHandler.postDelayed(onTouchTimerRunnable,500);
                        break;

                    case MotionEvent.ACTION_UP:
                        if(pressed)
                            actionUp(view,v,im);

                        onTouchTimerHandler.removeCallbacks(onTouchTimerRunnable);

                        break;

                    default:
                        return false;
                }
                return true;
            }
        });
    }

    private void actionUpNoClick(View touched, ImageButton im){
        touched.setPressed(false);
        if(im != null)
            animate(im);

        System.out.println("ACTION UP NO CLICK");
    }

    private void actionUp(View viewToClick, View touched, ImageButton im){
        touched.setPressed(false);
        if(im != null)
            animate(im);

        viewToClick.performClick();
        System.out.println("ACTION UP");
    }



    /*
    private void onTouchExpand(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            float touchPoint;
            boolean moving;
            float x;
            float y;
            float dX;
            float dY;
            int outputHeight;
            int addPairHeight;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        moving = false;
                        touchPoint = event.getX();
                        addPairHeight = addPairButton.getHeight();
                        outputHeight = output.getHeight();
                        x = addPairButton.getX();
                        y = addPairButton.getY();
                        dX = addPairButton.getX() - event.getRawX();
                        dY = addPairButton.getY() - event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float changeInY = event.getRawY() + dY;

                        if (changeInY - y > 50 || moving) {
                            if (!moving) {
                                input.setFocusableInTouchMode(false);
                                input.setFocusable(false);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                            moving = true;
                            output.scrollTo(0,0);
                            addPairButton.animate()
                                    .y(event.getRawY() + dY)
                                    .setDuration(0)
                                    .start();

                            output.setHeight(outputHeight + (int) changeInY - addPairButton.getHeight());
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);

                        if(moving) {
                            if (addPairHeight > addPairButton.getHeight()) {
                                Globals.expanded = true;
                                output.setHeight(expandCont.getHeight());
                            } else {
                                resetExpandCont();
                                Globals.expanded = false;
                            }
                            if (!Globals.expanded) {
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                                input.setFocusableInTouchMode(true);
                                input.setFocusable(true);
                            }
                        }
                        else {
                            String inputText = input.getText().toString();
                            String outputText = output.getText().toString();
                            if(!inputText.isEmpty() && inputText.trim().length() > 0) {
                                addWordPair(inputText, outputText, MainFragmentActivity.defaultLang);
                                animate(addPairButton);
                            }
                        }
                        moving = false;

                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
    */

    private void animate(final ImageButton imageButton){
        int colorFrom = getResources().getColor(R.color.anim_color,null);


        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;


        final int colorTo = color;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(200); // milliseconds

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                imageButton.setColorFilter((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }



    private void resetExpandCont(){
        String outputText = output.getText().toString();
        expandCont.removeAllViews();
        View newCont = View.inflate(getActivity(),R.layout.expandtextview_inflater, null);

        addPairButton = (ImageButton) newCont.findViewById(R.id.addpair_button);
        output = (TextView) newCont.findViewById(R.id.output_text);

        output.setText(outputText);

        output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.copyToClipboard(getActivity(),output.getText().toString());
            }
        });
        output.setMovementMethod(new ScrollingMovementMethod());

        expandCont.addView(newCont);
    }

    private void addWordPair(String inputText, String outputText, String inputLanguage){
        if(inputLanguage == "en") {
            db.insertValueToTable(inputText,outputText);
        }
        else{
            db.insertValueToTable(outputText,inputText);
        }

        SecondFragment.createWordPairList();
        Widget.updateWidget(getActivity());
    }

    public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}