package sabanciuniv.edu.newsportalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class CommentAdapter extends ArrayAdapter <CommentItem> {

    public CommentAdapter(Context context, List <CommentItem> items) {
        super(context, 0, items);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        CommentItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comments_view, parent, false);
        }

        TextView commenter = (TextView) convertView.findViewById(R.id.comment_name_view);

        TextView comment_message = (TextView) convertView.findViewById(R.id.comment_text_view);

        commenter.setText(item.getName());
        comment_message.setText(item.getMessage());

        return convertView;
    }


}
