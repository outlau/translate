package com.production.outlau.translate;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class FirstFragment extends Fragment {
    static EditText input;
    static TextView output;
    TextView bottomBorder;
    int initScrollViewHeight;
    ImageButton langSwitch;
    ImageButton inputClearButton;
    public static ImageButton addPairButton;
    ImageButton inputCopy;
    ImageButton inputPaste;
    ImageButton navBarToggleButton;

    static TextView inputTextView;
    static TextView outputTextView;

    public static boolean checkAdded = false;

    boolean cannotAddPair = false;

    static Translator translator = new Translator();

    ArrayList<Integer> animations;

    AppDatabase db;

    InputMethodManager imm;

    View layout;

    Handler onTouchTimerHandler;

    ScrollView scrollView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.first_fragment, container, false);

        db = new AppDatabase(getActivity());

        onTouchTimerHandler = new Handler();

        animations = new ArrayList<>();
        animations.add(R.anim.add_rotate);
        animations.add(R.anim.add_scaledown);
        animations.add(R.anim.add_scaleup);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        input = (EditText)layout.findViewById(R.id.input_edit_text);
        output = (TextView)layout.findViewById(R.id.output_text);
        langSwitch = (ImageButton)layout.findViewById(R.id.lang_swap);
        inputClearButton= (ImageButton)layout.findViewById(R.id.input_clear);
        addPairButton = (ImageButton)layout.findViewById(R.id.addpair_button);
        inputCopy = (ImageButton)layout.findViewById(R.id.input_copy);
        inputPaste = (ImageButton)layout.findViewById(R.id.input_paste);
        inputTextView = (TextView)layout.findViewById(R.id.input_lang_text);
        outputTextView = (TextView)layout.findViewById(R.id.output_lang_text);
        navBarToggleButton = (ImageButton)layout.findViewById(R.id.nav_bar_toggle_button);
        bottomBorder = (TextView)layout.findViewById(R.id.bottom_border);
        scrollView = (ScrollView)layout.findViewById(R.id.scroll_view);

        initScrollViewHeight = (int)getResources().getDimension(R.dimen.scrollview_init_height);

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
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,initScrollViewHeight));
                if(Globals.expanded) {
                    input.setFocusableInTouchMode(true);
                    input.setFocusable(true);
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

        onTouchListen(inputPaste);
        inputPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = input.getText().toString();
                int inputStart = input.getSelectionStart();
                String startStr = inputStr.substring(0,inputStart);
                String pasteStr = MainFragmentActivity.pasteFromClipboard(getContext());
                String endStr = inputStr.substring(input.getSelectionEnd(),inputStr.length());
                String str = startStr +
                                pasteStr +
                                endStr;
                input.setText(str);
                input.setSelection(inputStart+pasteStr.length());
            }
        });

        onTouchListen(addPairButton);
        addPairButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String inputText = input.getText().toString();
                 String outputText = output.getText().toString();
                 if (!inputText.isEmpty() && inputText.trim().length() > 0 && !checkAdded && !translator.getIsMyAsyncTaskRunning() && !cannotAddPair) {
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

        onTouchExpand(bottomBorder);

        output.setMovementMethod(new ScrollingMovementMethod());
        output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.copyToClipboard(getContext(),output.getText().toString());
            }
        });

        return layout;
    }

    private void addWordPairAnim(ArrayList<Integer> anims){
        final int anim = anims.remove(0);
        final ArrayList<Integer> animList = anims;

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
        String outputText = output.getText().toString();
        inputTextView.setText(Globals.languages.get(MainFragmentActivity.defaultLang));
        outputTextView.setText(Globals.languages.get(MainFragmentActivity.secondLang));
        input.setText(outputText);
        input.setSelection(outputText.length());
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

    }

    private void actionUp(View viewToClick, View touched, ImageButton im){
        touched.setPressed(false);
        if(im != null)
            animate(im);

        viewToClick.performClick();
    }

    private void onTouchExpand(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            float pos;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        View focus = getActivity().getCurrentFocus();
                        if (focus != null)
                            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);

                        output.scrollTo(0,0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        pos = clamp(
                                event.getRawY()-(((View)view.getParent()).getY())-bottomBorder.getHeight(),
                                initScrollViewHeight,
                                (((View)view.getParent()).getHeight())-bottomBorder.getHeight()
                        );

                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)pos));

                        break;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);

                        if(scrollView.getLayoutParams().height < initScrollViewHeight + 200){
                            scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,initScrollViewHeight));
                            Globals.expanded = false;

                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                        else
                            Globals.expanded = true;

                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

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