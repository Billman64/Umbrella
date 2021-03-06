package billlugo.umbrella;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
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

        final TextView tv = (TextView) findViewById(R.id.tvBanner);
        EditText et = (EditText) findViewById(R.id.etZIP);
        ListView lv = (ListView) findViewById(R.id.lvOutput);

        tv.setText(et.getText());

        final Handler handler = new Handler(getApplicationContext().getMainLooper());

//        String arr[] = new String[12];
//        arr[0] = "asdf";
//        listAdapter = new ArrayAdapter<String>(this,  , arr);

        // API call (TODO: refactor into data model area of code)
        String zip = et.getText().toString();
        final String strUrl = "http://api.wunderground.com/api/" + getString(R.string.api_key) + "/geolookup/q/" + zip + ".json";
        tv.setText(strUrl);
        Button btn = (Button) findViewById(R.id.button);
        btn.setText("getting weather...");
        final String strData;

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                String strResult = "";
                try {

                    URL url = new URL(strUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();    // needs to run on a thread
                    strResult = "error: before InputStreamReader";

                    // Error - MainThread Exception somehow

                    InputStreamReader isr = new InputStreamReader(connection.getInputStream()) {
                        @Override
                        public int read() throws IOException {
//                            Toast.makeText(MainActivity.this, "InputStreamReader error", Toast.LENGTH_SHORT).show();
                            Log.d ("myError","inputStreamReader error");
                            return 0;
                        }
                    };
                    Log.d("connection",connection.toString());
//                    Reader rdr = new InputStreamReader(isr);

                    strResult = "error: before buffered reader";

                    BufferedReader reader = new BufferedReader(isr); //???
                    strResult = "error: after buffered reader";

                    StringBuffer json = new StringBuffer(1024);
                    String tmp;

                    while ((tmp = reader.readLine()) != null) {
                        json.append(tmp).append("\n");
                    }
                    reader.close();
//                    Log.d("jsonData",json.toString() + " |tmp: " + tmp);
                    strResult = "error: after readLine loop";

                    final JSONObject data = new JSONObject(json.toString());
                    strResult = "error: after JSONObject initialization";

                    if (data.length()==0) {
                        strResult = "error: zero-length data";
//                        Toast.makeText(getApplication().getApplicationContext(), strResult, Toast.LENGTH_SHORT).show();
                    } else {
                        strResult = "error after data length>0";
                        Log.d("dataSample",data.toString().substring(0,50));
                    }

                    // send JSON data back to main thread via a bundled message object
//                    Message msgObj = handler.obtainMessage();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("message", data.toString());
//                    msgObj.setData(bundle);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // main thread communication
//                            strData = data.toString();
                            tv.setText(data.toString());
                        }
                    });
//                    handler.sendMessage(msgObj);

                    // create a handler to communicate JSON data back to main thread
//            private final Handler handler = new Handler() { //TODO: handler potential memory leak, since it's nested (clear on onDestroy())
//                public void handleMessage(Message msg){
//                    String response = msg.getData().getString("message");
//                }
//            };

                } catch (Exception e) {
//                        TextView tv = (TextView) findViewById(R.id.tvBanner);
//                        tv.setText(strResult + "\n" + e.toString());
                        Log.d("MyError","Data pull not working (maybe network connection issue?)" + e.toString());
                        Log.d("MyErrorLocation", strResult);
//                    Toast.makeText(MainActivity.this, strResult + "\n" + e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        } );    // end of thread
        t.start();
        btn.setText("Refresh");
//        tv.setText(data);
    }
}