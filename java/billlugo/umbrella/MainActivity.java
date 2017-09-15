package billlugo.umbrella;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.tvBanner);
        tv.setFocusable(false);

    }


    public void getWeather(View view) {

        //TODO: error-trap zip input

        TextView tv = (TextView) findViewById(R.id.tvBanner);
        EditText et = (EditText) findViewById(R.id.etZIP);
        ListView lv = (ListView) findViewById(R.id.lvOutput);

        tv.setText(et.getText());

//        String arr[] = new String[12];
//        arr[0] = "asdf";
//        listAdapter = new ArrayAdapter<String>(this,  , arr);

        // API call (TODO: refactor into data model area of code)
        String zip = et.getText().toString();
        final String strUrl = "http://api.wunderground.com/api/" + getString(R.string.api_key) + "/geolookup/q/" + zip + ".json";
        tv.setText(strUrl);
        Button btn = (Button) findViewById(R.id.button);
        btn.setText("getting weather...");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String strResult = "";
                Toast.makeText(MainActivity.this, "aaa", Toast.LENGTH_SHORT).show();
                try {

                    URL url = new URL(strUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();    // needs to run on a thread


                    connection.addRequestProperty("api-key", getString(R.string.api_key));

                    strResult = "error: before buffered reader";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); //???
                    strResult = "error: after buffered reader";

                    StringBuffer json = new StringBuffer(1024);
                    String tmp;

                    while ((tmp = reader.readLine()) != null) {
                        json.append(tmp).append("\n");
                        if(tmp.contains("NJ")) Toast.makeText(getApplication().getApplicationContext(), "NJ", Toast.LENGTH_SHORT).show();
                    }
                    reader.close();



                    JSONObject data = new JSONObject(json.toString());

                    if (data.length()==0) {
                        strResult = "error: zero-length data";
                        Toast.makeText(getApplication().getApplicationContext(), strResult, Toast.LENGTH_SHORT).show();
                    } else {
                        TextView tv = (TextView) findViewById(R.id.tvBanner);


                        tv.setText(data.length());
                        Toast.makeText(getApplication().getApplicationContext(), "asdf", Toast.LENGTH_SHORT).show();
                    }



                } catch (Exception e) {
                        TextView tv = (TextView) findViewById(R.id.tvBanner);
                        tv.setText(strResult + "\n" + e.toString());
                    Toast.makeText(MainActivity.this, strResult + "\n" + e.toString(), Toast.LENGTH_SHORT).show();
                }






            }
        } );    // end of thread

        btn.setText("Refresh");

    }
}