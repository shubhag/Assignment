package com.example.shubham.calculator;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myApp";
    private  ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

        final TextView operator = (TextView) findViewById(R.id.operator);
        final TextView resultView = (TextView) findViewById(R.id.result);
        Button add = (Button) findViewById(R.id.add);
        Button sub = (Button) findViewById(R.id.sub);
        Button mul = (Button) findViewById(R.id.mult);
        Button div = (Button) findViewById(R.id.div);
        Button exp = (Button) findViewById(R.id.exp);
        Button pow = (Button) findViewById(R.id.pow);
        Button sin = (Button) findViewById(R.id.sin);
        Button cos = (Button) findViewById(R.id.cos);
        Button tan = (Button) findViewById(R.id.tan);
        Button calculate = (Button) findViewById(R.id.calculate);
        final EditText operA = (EditText) findViewById(R.id.operanda);
        final EditText operB = (EditText) findViewById(R.id.operandb);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                operator.setText("+");
            }
        });
        sub.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("-");
            }
        });
        mul.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("*");
            }
        });
        div.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("/");
            }
        });
        pow.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("^");
            }
        });
        exp.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("e");
            }
        });
        sin.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("sin");
            }
        });
        cos.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("cos");
            }
        });
        tan.setOnClickListener(new View.OnClickListener(){
            public  void  onClick(View v){
                operator.setText("tan");
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                final String operAvalue = operA.getText().toString();
                final String operBvalue = operB.getText().toString();
                final String operatorval = operator.getText().toString();
                params.put("operanda", operAvalue);
                params.put("operator", operatorval);
                params.put("operandb", operBvalue);

                progressBar = new ProgressDialog(v.getContext());
                progressBar.setCancelable(true);
                progressBar.setMessage("Fetching result ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                progressBar.show();
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try {
                            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                new FetchTask().execute(operAvalue, operBvalue, operatorval);
                            } else {
                                Thread.sleep(600);
                                progressBar.dismiss();
                                resultView.setText("No network connection");

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
    public class FetchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...params) {
            try {

                return getResult(params[0], params[1], params[2]);
            } catch (IOException e) {
                return "Unable to connect to server";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.dismiss();
            Log.d(TAG, result);
            final TextView resultView = (TextView) findViewById(R.id.result);
            if (result != null) {
                String err = "null";
                String value = null;
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    if(obj == null){
                        Log.d(TAG, result);
                    } else {
                        err = obj.getString("error");
                        value = obj.getString("result");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String output = null;
                if(!err.equals("null")){
                    output = err;
                } else{
                    output = value;
                }
                resultView.setText(output);
            } else {
                resultView.setText("Error in connection");
            }
        }
    }

    private String getResult(String operanda, String operandb, String operator) throws IOException {
        String response = "";
        try {
            URL url = new URL("http://54.201.143.63:8080/calculate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );

            HashMap<String, String> postParams = new HashMap<String, String>();
            postParams.put("operanda", operanda);
            postParams.put("operator", operator);
            postParams.put("operandb", operandb);

            writer.write(getPostDataString(postParams));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if( responseCode == HttpURLConnection.HTTP_OK)  {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                br.close();
            } else{
                JSONObject errorCode = new JSONObject();
                try {
                    errorCode.put("error", "Error in Connection");
                    errorCode.put("result", 0);
                } catch (JSONException exp) {
                    exp.printStackTrace();
                }
                return(errorCode.toString());
            }
            return response;
        } catch(final java.net.SocketTimeoutException e){
            JSONObject errorCode = new JSONObject();
            try {
                errorCode.put("error", "Unable to connect to server");
                errorCode.put("result", 0);
            } catch (JSONException exp) {
                exp.printStackTrace();
            }
            return(errorCode.toString());
        } catch (java.io.IOException e) {
            e.printStackTrace();
            JSONObject errorCode = new JSONObject();
            try {
                errorCode.put("error", "Unable to connect to server");
                errorCode.put("result", 0);
            } catch (JSONException exp) {
                exp.printStackTrace();
            }
            return(errorCode.toString());
        }
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
            }
            else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
