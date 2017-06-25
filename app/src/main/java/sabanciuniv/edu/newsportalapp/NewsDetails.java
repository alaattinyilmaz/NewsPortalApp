package sabanciuniv.edu.newsportalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;

public class NewsDetails extends AppCompatActivity {


// This java file shows the details of a news item

    private TextView dtitle;
    private TextView sdate;
    private TextView dtext;
    private ImageView dimageid;
    int newsid;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_news_details);


        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NewsItem news_item = (NewsItem) getIntent().getSerializableExtra("NewsItem");

        dtitle = (TextView) findViewById(R.id.detailtitle);
        sdate = (TextView) findViewById(R.id.detaildate);
        dtext = (TextView) findViewById(R.id.mytext);
        dimageid = (ImageView) findViewById(R.id.detailimage);

        newsid = news_item.getId();

        dtitle.setText(news_item.getTitle());

        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        String datestr = formatter.format(news_item.getNewsDate());
        sdate.setText(datestr);

        dtext.setText(news_item.getText());
        dimageid.setImageResource(news_item.getImageId());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handling the item selection
        switch (item.getItemId()) {
            case R.id.viewcomments:

                Intent viewcomments_intent = new Intent(NewsDetails.this, ViewComments.class);
                viewcomments_intent.putExtra("news_id", newsid); // Sending newsid as parameter
                NewsDetails.this.startActivity(viewcomments_intent);
                // Going to the comments;
                return true;

            case R.drawable.ic_launcher:
                Intent intent = new Intent(NewsDetails.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
