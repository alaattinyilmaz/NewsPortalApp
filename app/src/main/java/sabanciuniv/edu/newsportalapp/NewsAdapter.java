package sabanciuniv.edu.newsportalapp;

/**
 * Created by alaat on 4.05.2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import java.text.Format;
import java.text.SimpleDateFormat;


    public class NewsAdapter extends ArrayAdapter<NewsItem> {

        public NewsAdapter(Context context, List<NewsItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            NewsItem item = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_layout, parent, false);
            }


            // Connections between image objects to the java code
            ImageView caption = (ImageView) convertView.findViewById(R.id.myimage);
            TextView title = (TextView) convertView.findViewById(R.id.mytitle);
            TextView date = (TextView) convertView.findViewById(R.id.mydate);



            title.setText(item.getTitle()); // Printing text out to the screen

            caption.setImageResource(item.getImageId()); // Getting image

            Format formatter = new SimpleDateFormat("dd/MM/yyyy"); // This line is formatting the date into desired form
            String datestr = formatter.format(item.getNewsDate()); // Then it converts it from date type to string type

            date.setText(datestr);  // Printing out on the screen

            return convertView;
        }


}
