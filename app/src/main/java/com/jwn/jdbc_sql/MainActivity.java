package com.jwn.jdbc_sql;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ItemAdapter itemAdapter;
    Context thisContext;
    ListView myListView;
    TextView progressTextView;
    Map<String, Double> fruitsMap = new LinkedHashMap<String, Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        myListView = (ListView)findViewById(R.id.myListView);
        progressTextView = (TextView)findViewById(R.id.progressTextView);
        thisContext = this;

        progressTextView.setText(""); // set the text in the progress TextView to blank
        Button btn = (Button)findViewById(R.id.getDataButton);//Find getDataButton object and assign it to btn
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData retrieveData = new GetData();
                retrieveData.execute("");
            }
        });
    }// end onCreate

    private class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String msg = "";
            //JDBC driver and database URL
            final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

            static final String DB_URL = "jdbc:mysql://" +
                    Dbstrings.DATABASE_URL + "/" +
                    Dbstrings.DATABASE_NAME;

            @Override
            protected void onPreExecute() {
                progressTextView.setText("Connecting to database..");
            }

            @Override
            protected String doInBackground(String... strings){

                Connection conn = null;
                Statement stmt = null;


                try {
                    Class.forName(JDBC_DRIVER);
                    conn = DriverManager.getConnection(DB_URL, Dbstrings.USERNAME, Dbstrings.PASSWORD);

                    stmt = conn.createStatement();
                    String sql = "SELECT * FROM fruits";
                    ResultSet rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        String name = rs.getString("fruit_name");
                        double price = rs.getDouble("price");

                        fruitsMap.put(name, price);
                    }

                    msg = "Process completed.";
                    rs.close();
                    stmt.close();
                    conn.close();


                 }catch (SQLException connError) {
                    msg = "An exception was thrown for JDBC.";
                    connError.printStackTrace();
                } catch (ClassNotFoundException e) {
                    msg = "A class not found exception occurred.";
                    e.printStackTrace();
                }finally {
                    try {
                        if (stmt != null){
                            stmt.close();
                        }
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (conn != null){
                            conn.close();
                        }
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
                    protected void onPostExecute(String msg){
                progressTextView.setText(this.msg);
                itemAdapter = new ItemAdapter(thisContext, fruitsMap);
                myListView.setAdapter(itemAdapter);
            }
        }
    }

}// end MainActivity
