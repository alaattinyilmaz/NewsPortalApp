package sabanciuniv.edu.newsportalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); // Showing this activity

        // Forcing home icon to be appear on the toolbar
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }


    @Override
    protected void onStart() {
        super.onStart();
        GetAllNews tsk = new GetAllNews();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
    }


    // Getting all the news task
    class GetAllNews extends AsyncTask<String,Void,JSONArray> {


        // Loading page
        @Override
        protected void onPreExecute() {

            prgDialog = new ProgressDialog(MainActivity.this);
            prgDialog.setTitle("Loading...");
            prgDialog.setMessage("Please wait.");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            HttpURLConnection conn = null;
            JSONArray jarray = new JSONArray();

            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line ="";
                StringBuilder strBuffer = new StringBuilder();
                while((line = reader.readLine())!=null){
                    strBuffer.append(line);
                }

                JSONObject jsonresponse = new JSONObject(strBuffer.toString());

                jarray = jsonresponse.getJSONArray("items"); // Getting json array


            } catch (JSONException jsex){
                jsex.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                conn.disconnect();
            }

            return jarray;
        }

        @Override
        protected void onPostExecute(JSONArray jarray) {

            try {

                Spinner spinner = (Spinner) findViewById(R.id.myspinner); // Options menu aka spinner

                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        int categoryid = 0;

                        GetNewsByCategory gnbc_tsk = new GetNewsByCategory();

                        // Executing different tasks according to user selection from spinner:
                        switch(position) {
                            case 0:
                                GetAllNews tsk = new GetAllNews();
                                tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getall"); // Choosing all
                                break;
                            case 1:
                                categoryid = 4;
                                gnbc_tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/"+categoryid+"");
                                break;
                            case 2:
                                categoryid = 6;
                                gnbc_tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/"+categoryid+"");
                                break;
                            case 3:
                                categoryid = 5;
                                gnbc_tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/"+categoryid+"");
                                break;
                            default:
                                break;


                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // In case of user did not press the options menu do not do anything
                    }

                });


                final List <NewsItem> myallnews = new ArrayList<>();

                for(int i = 0; i < jarray.length(); i++)
                {
                    String mytitle = jarray.getJSONObject(i).getString("title");
                    int myid = jarray.getJSONObject(i).getInt("id");
                    String imagelink = jarray.getJSONObject(i).getString("image"); // Taking imagelink


                    // Extracting the string between last / and last .
                    imagelink = imagelink.substring(imagelink.lastIndexOf("/") + 1, imagelink.lastIndexOf("."));


                    Long milliSeconds = jarray.getJSONObject(i).getLong("date"); // Taking date object in milliseconds as long integer

                    Date myrealdate = new Date(); // Initializing a date object

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Making format of the date
                    String dateInString = formatter.format(new Date(milliSeconds)); // Converting date into string in desired format


                    try {
                        myrealdate = formatter.parse(dateInString); // Converting from string to date object
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }


                    String mytext = jarray.getJSONObject(i).getString("text");

                    // Converting from string to drawable
                    int resID = getResources().getIdentifier(imagelink, "drawable", getPackageName());

                    NewsItem objnews = new NewsItem(myid,mytitle,mytext,resID, myrealdate);
                    myallnews.add(objnews);



                }

                NewsAdapter newsadapt = new NewsAdapter(MainActivity.this, myallnews); // Creating newsadapter object

                ListView myallnewsview = (ListView) findViewById(R.id.allnews);

                myallnewsview.setAdapter(newsadapt);


                myallnewsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent detail_intent = new Intent(MainActivity.this, NewsDetails.class);
                        detail_intent.putExtra("NewsItem", myallnews.get(position)); // Sending news item as parameter
                        MainActivity.this.startActivity(detail_intent);
                    }
                });



                prgDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
//////////////////








    ////////////
    // Getting the news with the selected category task
    class GetNewsByCategory extends AsyncTask<String,Void,JSONArray> {


        // Loading page
        @Override
        protected void onPreExecute() {

            prgDialog = new ProgressDialog(MainActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait.");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            HttpURLConnection conn = null;
            JSONArray jarray = new JSONArray();

            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line ="";
                StringBuilder strBuffer = new StringBuilder();
                while((line = reader.readLine())!=null){
                    strBuffer.append(line);
                }

                JSONObject jsonresponse = new JSONObject(strBuffer.toString());

                jarray = jsonresponse.getJSONArray("items");


            } catch (JSONException jsex){
                jsex.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                conn.disconnect();
            }



            return jarray;
        }

        @Override
        protected void onPostExecute(JSONArray jarray) {

            try {

                final List <NewsItem> myallnews = new ArrayList<>();

                for(int i = 0; i < jarray.length(); i++)
                {
                    String mytitle = jarray.getJSONObject(i).getString("title");
                    String myidstr = jarray.getJSONObject(i).getString("id");

                    int myid = Integer.parseInt(myidstr);

                    String mydatestr = jarray.getJSONObject(i).getString("date");

                    Date myrealdate = new Date();

                    SimpleDateFormat mydate = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        myrealdate = mydate.parse(mydatestr);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    String mytext = jarray.getJSONObject(i).getString("text");

                    String imagelink = jarray.getJSONObject(i).getString("image");


                    imagelink = imagelink.substring(imagelink.lastIndexOf("/") + 1, imagelink.lastIndexOf("."));


                    // String myimg = imagelink.substring(imagelink.indexOf("/") + 1, imagelink.indexOf("."));

                    int resID = getResources().getIdentifier(imagelink, "drawable", getPackageName());

                    NewsItem objnews = new NewsItem(myid, mytitle, mytext, resID, myrealdate);
                    myallnews.add(objnews);



                }

                // myallnews = mynews.getAllNews(); // Getting and assigning all the news to NewsItem array

                NewsAdapter newsadapt = new NewsAdapter(MainActivity.this, myallnews); // Creating newsadapter object

                ListView myallnewsview = (ListView) findViewById(R.id.allnews);

                myallnewsview.setAdapter(newsadapt);


                myallnewsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent detail_intent = new Intent(MainActivity.this, NewsDetails.class);
                        detail_intent.putExtra("NewsItem", myallnews.get(position));
                        MainActivity.this.startActivity(detail_intent);
                    }
                });



                prgDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }




}