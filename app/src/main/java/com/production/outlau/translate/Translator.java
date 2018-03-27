package com.production.outlau.translate;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;

public class Translator {
    Activity thisActivity;
    TextView outputTextView;

    public void translate(String inputLang, String outputLang, String inputText, Activity activity, TextView tv) {
        thisActivity = activity;
        outputTextView = tv;
        String[] inputs = {inputLang, outputLang, inputText};
        new MyAsyncTask(inputs).execute();
    }

    private void updateTextView(final String translation) {
        thisActivity.runOnUiThread(new Runnable() {
            public void run () {
                outputTextView.setText(translation);
            }
        });
    }

    private class MyAsyncTask extends AsyncTask<Void, Integer, String> {

        String[] inputs; // Language from, Language to, Phrase
        public MyAsyncTask(String[] inputs){
            this.inputs = inputs;
        }


        @Override
        protected String doInBackground(Void... params) {

            String translation = "";
            try{
                translation = callUrlAndParseResult(inputs[0],inputs[1],inputs[2]); // Language from, Language to, Phrase

            }
            catch (Exception e){
                translation = e.toString();
            }
            updateTextView(translation);
            return translation;
        }
    }


    public String callUrlAndParseResult(String langFrom, String langTo,
                                        String word) throws Exception
    {

        String url = "https://translate.googleapis.com/translate_a/single?"+
                "client=gtx&"+
                "sl=" + langFrom +
                "&tl=" + langTo +
                "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseResult(response.toString());
    }

    private String parseResult(String inputJson) throws Exception
    {
        JSONArray jsonArray = new JSONArray(inputJson);
        JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
        JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);

        return jsonArray3.get(0).toString();
    }
}