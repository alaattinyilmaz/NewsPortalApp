package sabanciuniv.edu.newsportalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewComments extends AppCompatActivity {

    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewcomments);



        getSupportActionBar().setIcon(R.drawable.ic_launcher); // Setting icon as desired image


        //Drawable myhomebut = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher, null);

        //getSupportActionBar().setIcon(R.drawable.ic_launcher);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true); // Activating the return home button


        NewsItem mycommentitems = new NewsItem();

        List <CommentItem> allcomments_array = mycommentitems.getComments();

        NewsItem mynews = new NewsItem();


        CommentAdapter mycommentadapt = new CommentAdapter(this, allcomments_array);

        ListView mycomments_listview = (ListView) findViewById(R.id.viewallcomments);

        mycomments_listview.setAdapter(mycommentadapt);




        /*
        mycomments_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail_intent = new Intent(MainActivity.this, NewsDetails.class);
                detail_intent.putExtra("NewsItem", myallnews.get(position));
                MainActivity.this.startActivity(detail_intent);
            }
        });
        */



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comments_menu, menu);
        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        ViewComments.GetComments tsk = new ViewComments.GetComments();

        int newsid = getIntent().getIntExtra("news_id", 1);

        //Toast.makeText(ViewComments.this, "Your Name: " + newsid + "\n Your Message: ", Toast.LENGTH_LONG).show();

        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/"+newsid+"");
    }


    ////////////
    // Getting all the news task
    class GetComments extends AsyncTask<String,Void,JSONArray> {


        // Loading page
        @Override
        protected void onPreExecute() {

            prgDialog = new ProgressDialog(ViewComments.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait.");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            HttpURLConnection conn = null;
            JSONArray movies = new JSONArray();

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

                movies = jsonresponse.getJSONArray("items");


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



            return movies;
        }

        @Override
        protected void onPostExecute(JSONArray movies) {

            try {

                final List <CommentItem> myallcomments = new ArrayList<>();

                for(int i = 0; i < movies.length(); i++)
                {
                    String myname = movies.getJSONObject(i).getString("name");
                    String mytext = movies.getJSONObject(i).getString("text");

                    String myidstr = movies.getJSONObject(i).getString("id");
                    String newsidstr = movies.getJSONObject(i).getString("news_id");

                    int myid = Integer.parseInt(myidstr);
                    int mynewsid = Integer.parseInt(newsidstr);
                    CommentItem objnews = new CommentItem(myid, myname, mytext);
                    myallcomments.add(objnews);

                }

                // myallnews = mynews.getAllNews(); // Getting and assigning all the news to NewsItem array

                CommentAdapter commentadapt = new CommentAdapter(ViewComments.this, myallcomments); // Creating newsadapter object

                ListView myallnewsview = (ListView) findViewById(R.id.viewallcomments);

                myallnewsview.setAdapter(commentadapt);


                prgDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
//////////////////






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.post_commentsview:

                int newsid = getIntent().getIntExtra("news_id", 1);

                Intent post_comment_intent = new Intent(ViewComments.this, PostComment.class);
                //  detail_intent.putExtra("NewsItem", myallnews.get(position)); // Sending with parameter
                post_comment_intent.putExtra("news_id", newsid); // parameter
                ViewComments.this.startActivity(post_comment_intent);

                // Go to the comments;

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
