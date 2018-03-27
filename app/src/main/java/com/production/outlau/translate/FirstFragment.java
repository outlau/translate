package com.production.outlau.translate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.DragEvent;
import android.view.GestureDetector;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.TableRow;
import android.widget.TextView;

public class FirstFragment extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    static EditText input;
    static TextView output;
    Button langSwitch;
    Button inputClearButton;
    Button addPairButton;
    ImageButton inputCopy;

    static TextView inputTextView;
    static TextView outputTextView;

    LinearLayout expandCont;

    String defaultLangStr;

    static Translator translator = new Translator();

    AppDatabase db;

    static String inputLanguage = "sv";

    static String outputLanguage = "en";

    InputMethodManager imm;

    View layout;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.first_fragment, container, false);

        db = new AppDatabase(getActivity());
        sp = getActivity().getSharedPreferences("MyPref", 0);
        editor = sp.edit();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        input = (EditText)layout.findViewById(R.id.input_edittext);
        output = (TextView)layout.findViewById(R.id.output_text);
        langSwitch = (Button)layout.findViewById(R.id.lang_swap);
        inputClearButton= (Button)layout.findViewById(R.id.input_clear);
        addPairButton = (Button)layout.findViewById(R.id.addpair_button);
        inputCopy = (ImageButton)layout.findViewById(R.id.input_copy);
        inputTextView = (TextView)layout.findViewById(R.id.input_lang_text);
        outputTextView = (TextView)layout.findViewById(R.id.output_lang_text);
        expandCont = (LinearLayout)layout.findViewById(R.id.expand_container);


        System.out.println("TETEESST "+sp.getString("defaultLang", null));


        if(sp.getString("defaultLang", null).matches("sv")){
            inputLanguage = "sv";
            outputLanguage = "en";
        }else{
            inputLanguage = "en";
            outputLanguage = "sv";
        }

        updateTextUI();
        langSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTexts();
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
                String inputText = input.getText().toString();
                //String[] splitInput = inputText.split(" ");
                translator.translate(inputLanguage,outputLanguage,input.getText().toString(),getActivity(),output);
            }
        });

        inputClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
                output.setText("");
                output.scrollTo(0,0);
            }
        });

        onTouchExpand(addPairButton);

        output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.copyToClipboard(getActivity(),output.getText().toString());
            }
        });
        output.setMovementMethod(new ScrollingMovementMethod());

        inputCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentActivity.copyToClipboard(getActivity(),input.getText().toString());
                Animation anim =
                        AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                inputCopy.startAnimation(anim);
            }
        });

        return layout;
    }

    public static void switchTexts(){
        String inputLang = inputLanguage;
        inputLanguage = outputLanguage;
        outputLanguage = inputLang;
        updateTextUI();
    }

    private static void updateTextUI(){
        inputTextView.setText(Globals.languages.get(inputLanguage));
        outputTextView.setText(Globals.languages.get(outputLanguage));
        System.out.println("input " +inputTextView.getText());
        System.out.println("output " +outputTextView.getText());
        input.setText(output.getText().toString());
    }

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
                                addWordPair(inputText, outputText, inputLanguage);
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

    private void resetExpandCont(){
        String outputText = output.getText().toString();
        expandCont.removeAllViews();
        View newCont = View.inflate(getActivity(),R.layout.expandtextview_inflater, null);

        addPairButton = (Button) newCont.findViewById(R.id.addpair_button);
        output = (TextView) newCont.findViewById(R.id.output_text);

        output.setText(outputText);

        onTouchExpand(addPairButton);
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