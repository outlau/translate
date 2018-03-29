package com.production.outlau.translate;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONObject;

public class Translator {
	Activity thisActivity;
	TextView outputTextView;

	static MyAsyncTask currentAsyncTask;

	public void translate(String inputLang, String outputLang, String inputText, Activity activity, TextView tv) {
		thisActivity = activity;
		outputTextView = tv;
		String[] inputs = {inputLang, outputLang, inputText};
		try {
			currentAsyncTask.cancel(true);
		}
		catch (NullPointerException e){

		}
		currentAsyncTask = new MyAsyncTask(inputs);
		currentAsyncTask.execute();
	}

	public boolean getIsMyAsyncTaskRunning(){
		System.out.println("running? : "+currentAsyncTask.getStatus());
		return currentAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING);
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

			String translation;
			try{
				translation = callYandexUrlAndParseResult(inputs[0],inputs[1],inputs[2]); // Language from, Language to, Phrase

			}
			catch (Exception e){
				translation = e.toString();
			}
			updateTextView(translation);
			return translation;
		}

		@Override
		protected void onPostExecute(String result) {
			for (WordPair wordpair:SecondFragment.list) {
				if(inputs[2].toLowerCase().matches(wordpair.leftWord.toLowerCase())){
					FirstFragment.addPairButton.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.icon_check, thisActivity.getTheme()));
					FirstFragment.checkAdded = true;
				}
			}
		}
	}

	public String callYandexUrlAndParseResult(String langFrom, String langTo,
											  String word) throws Exception
	{
		String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?"+
				"key=trnsl.1.1.20180328T235619Z.cf5ef7f3c7cab192.6e133666e3a3c0c661c6653616c47225d81906f6"+
				"&text="+word+
				"&lang="+langFrom+"-"+langTo;

		if(langFrom.isEmpty() || langTo.isEmpty() || word.isEmpty())
			return null;

		HttpURLConnection connection = null;
		BufferedReader reader = null;

		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			InputStream stream = connection.getInputStream();

			reader = new BufferedReader(new InputStreamReader(stream));

			StringBuffer buffer = new StringBuffer();
			String line;

			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}

			return parseYandexResult(buffer.toString());
		}

		 catch (MalformedURLException e) {
			e.printStackTrace();
		 } catch (InterruptedIOException e) {
			e.printStackTrace();
			return outputTextView.getText().toString()+"...";
		}catch (UnknownHostException e){
			e.printStackTrace();
			return "Cannot connect to server";
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			return "Unknown error. Use google translate instead.";
	}

	private String parseYandexResult(String inputJson) throws Exception
	{

		JSONObject result = new JSONObject(inputJson);
		int code = result.getInt("code");
		System.out.println("code : "+code);

		System.out.println("getTEXT : "+result.getJSONArray("text").getString(0));
		/*
		401
		Invalid API key

		402
		Blocked API key

		404
		Exceeded the daily limit on the amount of translated text
		*/
		if(code == 401){
			return "Invalid API key. The developer fucked up";
		}
		else if(code == 402){
			return "API Key has been blocked. Contact the developer PLEASE";
		}
		else if(code == 404){
			return "Exceeded the daily limit. Go use Google translate instead";
		}
		else if(code == 200){
			return result.getJSONArray("text").getString(0);
		}
		else{
			return "Unknown response code, GG :(";
		}

	}



	public String callGoogleUrlAndParseResult(String langFrom, String langTo,
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

		return parseGoogleResult(response.toString());
	}

	private String parseGoogleResult(String inputJson) throws Exception
	{
		JSONArray jsonArray = new JSONArray(inputJson);
		JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
		JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);

		System.out.println("json ARRAY : "+jsonArray);

		return jsonArray3.get(0).toString();
	}
}