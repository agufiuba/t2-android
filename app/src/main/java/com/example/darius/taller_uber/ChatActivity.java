package com.example.darius.taller_uber;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.darius.taller_uber.ChatMessage;
import com.example.darius.taller_uber.MainActivity;
import com.example.darius.taller_uber.MessageAdapter;
import com.example.darius.taller_uber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Chat. Fragmento destinado a la parte de chat.
 * Código extraído del tutorial http://www.devexchanges.info/2016/12/simple-chat-application-using-firebase.html
 * y adaptado a las necesidades de la aplicación.
 */
public class ChatActivity extends AppCompatActivity {

    String loggedInUserName;
    ListAdapter adapter;
    EditText input;
    ListView listView;
    LinearLayout aviso;
    FloatingActionButton fab_send;
    protected FirebaseDatabase database;

    DatabaseReference ref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        load_items();

        if (!getIntent().getExtras().getString("ChatID").equals("")) {
            unBlockChat();
            ref = database.getReference(MainActivity.DBREFERENCES.chats.name()).
                    child(getIntent().getExtras().getString("ChatID"));
            showAllOldMessages();

            fab_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!input.getText().toString().trim().equals("")) {
                        ref.push().setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        input.setText("");
                    }
                }
            });
        } else {
            blockChat();
        }
    }

    private void load_items(){
        database = FirebaseDatabase.getInstance();
        listView = findViewById(R.id.list);
        aviso = findViewById(R.id.aviso);
        fab_send = findViewById(R.id.fab_send);
        input = findViewById(R.id.input);
    }
    private void showAllOldMessages() {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);

        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.message_out, ref);
        listView.setAdapter(adapter);
    }

    private void blockChat(){
        this.aviso.setVisibility(View.VISIBLE);
        this.fab_send.setClickable(false);
    }

    private void unBlockChat(){
        this.aviso.setVisibility(View.GONE);
        this.fab_send.setClickable(true);
    }
}