package sabanciuniv.edu.newsportalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostComment extends AppCompatActivity {

    ProgressDialog prgDialog;
    private TextView inputname;
    private TextView inputmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        // Forcing the homebutton to be seen
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Button post_comment_button = (Button) findViewById(R.id.post_comment_button_view);

        post_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(PostComment.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // Hides the soft keyboard when user clicks post comment

                LetsPostComment tsk = new LetsPostComment();
                tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment");

            }
        });

    }



    class LetsPostComment extends AsyncTask<String,Void,String> {


        TextView inputname = (TextView) findViewById(R.id.person_name_view);
        TextView inputmessage = (TextView) findViewById(R.id.post_message_view);

        String myname = inputname.getText().toString();

        String mymessage = inputmessage.getText().toString();

        int newsid = getIntent().getIntExtra("news_id", 1); // Getting newsid from intent that is passed to this activity


        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(PostComment.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            String result = "";
            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type","application/json");
                JSONObject obj = new JSONObject();

                // Putting these values to a created json object
                obj.put("name",myname);
                obj.put("text",mymessage);
                obj.put("newsid",newsid);

                DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
                writer.writeBytes(obj.toString());

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    String line ="";
                    StringBuilder strBuffer = new StringBuilder();
                    while((line = reader.readLine())!=null){
                        strBuffer.append(line);
                    }
                    JSONObject retObj = new JSONObject(strBuffer.toString());

                    result = retObj.getString("serviceMessageCode");
                    // Getting response of the web service

                }

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

            return result;
        }

        @Override
        protected void onPostExecute(String serviceMessageCode) {

            if(serviceMessageCode.equals("1"))
            {
                Toast.makeText(PostComment.this, "Your message has been post.", Toast.LENGTH_SHORT).show();
                Intent viewcomments_intent = new Intent(PostComment.this, ViewComments.class);
                viewcomments_intent.putExtra("news_id", newsid); // Sending newsid as parameter
                PostComment.this.startActivity(viewcomments_intent);
            }
            else
            {
                Toast.makeText(PostComment.this, "Your message could not be posted.", Toast.LENGTH_SHORT).show();
            }
            prgDialog.dismiss(); // Hiding loading window

        }
    }




}
