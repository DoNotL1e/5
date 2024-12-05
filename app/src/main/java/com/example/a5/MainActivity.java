package com.example.a5;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    TextView textView;
    ListView listView;
    ArrayList<String> currencies = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);
        Button fetchButton = findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(v -> new FetchCurrencyDataTask().execute());
    }
    private class FetchCurrencyDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://www.floatrates.com/daily/usd.xml");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(reader);
                String currencyCode = "", rate = "";
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();
                    if (eventType == XmlPullParser.START_TAG) {
                        if ("code".equalsIgnoreCase(tagName)) {
                            currencyCode = parser.nextText();
                        } else if ("rate".equalsIgnoreCase(tagName)) {
                            rate = parser.nextText();
                        }
                    } else if (eventType == XmlPullParser.END_TAG && "item".equalsIgnoreCase(tagName)) {
                        currencies.add(currencyCode + " - " + rate);
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            textView.setText("Data:");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, currencies);
            listView.setAdapter(adapter);
        }
    }
}




