package com.example.darius.taller_uber;

import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * MessageAdapter (Adaptador destinado al manejo del ListView del chat)
 * Código extraído del tutorial http://www.devexchanges.info/2016/12/simple-chat-application-using-firebase.html
 * y adaptado a las necesidades de la aplicación.
 */
public class MessageAdapter extends FirebaseListAdapter<ChatMessage> {

    private ChatActivity activity;
    private String userID;

    public MessageAdapter(ChatActivity activity, Class<ChatMessage> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void populateView(View v, ChatMessage model, int position) {
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);

        messageText.setText(model.getMessageText());
        messageUser.setText(model.getMessageUser());

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatMessage chatMessage = getItem(position);
        if (chatMessage.getMessageUserId().equals(userID))
            view = activity.getLayoutInflater().inflate(R.layout.message_out, viewGroup, false);
        else
            view = activity.getLayoutInflater().inflate(R.layout.message_in, viewGroup, false);

        //generating view
        populateView(view, chatMessage, position);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }
}
