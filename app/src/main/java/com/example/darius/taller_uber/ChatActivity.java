package com.example.darius.taller_uber;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    ListView listView;
    ListAdapter adapter;
    String peerUID;
    protected FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference ref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        peerUID = getIntent().getStringExtra("peerUID");
        peerUID = "YEN8EoPUY5YF4vEfYnJIlsMsTWT2";
        setContentView(R.layout.chat);
        final EditText input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);
        ref = database.getReference(MainActivity.DBREFERENCES.chats.name()).child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()+peerUID);
        showAllOldMessages();

        FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    //todO
                } else {
//                    FirebaseDatabase.getInstance()
//                            .getReference()
//                            .push()
//                            .setValue(new ChatMessage(input.getText().toString(),
//                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
//                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            );
                    ref.push().setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()));
                    input.setText("");
                }
            }
        });
    }

    private void showAllOldMessages() {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);

        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.message_out, ref);
        listView.setAdapter(adapter);
    }
}