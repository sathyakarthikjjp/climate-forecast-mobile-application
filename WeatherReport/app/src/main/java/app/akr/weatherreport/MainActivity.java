package app.akr.weatherreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    String json;
    JSONObject jsonObject;
    TextView textView;
    String city = null;
    TextView search = null;

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void search(View view) {


        closeKeyboard();
        city = search.getText().toString();
        JsonDownload jsonDownload = new JsonDownload();
        try {
            json = jsonDownload.execute("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=a3a57785cf3543fc3bf8c3571b305c5f").get();

        } catch (Exception u) {
            u.printStackTrace();
        }

    }


    public class JsonDownload extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            URL url = null;
            HttpURLConnection urlConnection = null;
            String result = "";

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data  = reader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                textView.setText("City not found!");
                return null;

            } catch (Exception i) {
                i.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                jsonObject = new JSONObject(s);
                Log.i("^^^^^^^^^^^^^^^^", s);
                String weatherInfo = null;
                weatherInfo = jsonObject.getString("weather");
                JSONArray array = new JSONArray(weatherInfo);
                JSONObject jsonObject = array.getJSONObject(0);

                Pattern pattern0 = Pattern.compile("temp_min\":(.*?),");
                Matcher matcher = pattern0.matcher(s);
                matcher.find();
                String tempMin = matcher.group(1);

                Pattern pattern1 = Pattern.compile("temp_max\":(.*?),");
                Matcher matcher1 = pattern1.matcher(s);
                matcher1.find();
                String tempMax = matcher1.group(1);

                Pattern pattern2 = Pattern.compile("pressure\":(.*?),");
                Matcher matcher2 = pattern2.matcher(s);
                matcher2.find();
                String pressure = matcher2.group(1);

                Pattern pattern3 = Pattern.compile("humidity\":(.*?),");
                Matcher matcher3 = pattern3.matcher(s);
                matcher3.find();
                String humidity = matcher3.group(1);

                textView.setText(jsonObject.getString("main") + "\n" +jsonObject.getString("description") + "\n" + "Minimum Temp: " + tempMin + "\n" + "Maximum Temp: " + tempMax + "\n" + "Pressure: " + pressure + "\n" + "Humidity: " + humidity);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.resultDisplay);
        search = findViewById(R.id.editText);


    }
}
