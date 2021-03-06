package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_img;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    ImageButton btn_send;
    EditText txt_send;

    //adapter
    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    String userName;

    // local storage
    SharedPreferences sharedPreferences;

    // seen/unseen fun
    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //recycleview to show msg
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext()); //////???
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_img=findViewById(R.id.profile_img);
        username=findViewById(R.id.user_name);
        btn_send=findViewById(R.id.btn_send);
        txt_send=findViewById(R.id.text_send);

        sharedPreferences = getSharedPreferences("chatapp", MODE_PRIVATE);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=txt_send.getText().toString();
                if(!msg.equals("")){
                    /** not assign receiver
                     *?????????????????????????????? ?????????intent??? ??????userid??????
                     **/
                    // firebaseUser.getDisplayName()???????????????firebase auth???????????????
                    sendMessage(firebaseUser.getUid(),"",userName,msg);
                }else{
                    Toast.makeText(MainActivity.this," You can't not send empty message",Toast.LENGTH_SHORT).show();
                }
                txt_send.setText("");
            }
        });

        ref= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        /***
         * ??????????????? (????????????????????????)
         ***/
        ref.addValueEventListener(new ValueEventListener() { //???????????????listener (?????????profile img??????????????????)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName=user.getUsername();
                //username.setText(user.getUsername());
                username.setText("Chat room");

                if(user.getImageURL().equals("default")){
                    profile_img.setImageResource(R.mipmap.ic_launcher);
                }else{
                    //load img with URL
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_img);
                }

                readMessage(firebaseUser.getUid(),"",user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage();
    }

    private void seenMessage(){
        ref=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot:snapshot.getChildren()){
                    Chat chat=shot.getValue(Chat.class);
                    String fuserID =firebaseUser.getUid();
                    if(!chat.getSender().equals(fuserID )){  //???????????????????????????
                        HashMap<String,Boolean> seenMap=chat.getSeenPeople();
                        if(!seenMap.containsKey(fuserID)) { //?????????id???????????????map
                            HashMap<String,Object> map=new HashMap<>();
                            seenMap.put(fuserID,true);
                            map.put("seenPeople",seenMap);
                            shot.getRef().updateChildren(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String senderName, String message){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        long time =new Date().getTime();
        HashMap map=new HashMap<String,Boolean>();
        map.put("default",false);
        reference.child("Chats").push().setValue(new Chat(sender,receiver, message, senderName, map, time)); //??????firebase
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //load option menu
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //sign out
        switch(item.getItemId()){ //menu??????????????????sign out

            case R.id.profile:
                startActivity(new Intent(MainActivity.this,PorfileActivity.class));
                //finish(); //??????destroy??? ?????????task????????????????????????
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,StartActivity.class));
                finish();
                return true;
        }
        return false;
    }

    public void readMessage(final String myid, final String userid, final String imageurl){
        mChat=new ArrayList<>();

        ref=FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() { //?????????????????????
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot shot:snapshot.getChildren()){
                    Chat chat=shot.getValue(Chat.class);

                    /**
                     * ?????????????????? ?????? receiver==myid&&sender==userid || receiver==userid&&sender==myid
                     * ?????????add???mChat
                     */
                    //if(chat.getReceiver().equals(myid)&&chat.getSender().equals(userid)){
                    mChat.add(chat);

                    messageAdapter=new MessageAdapter(MainActivity.this,mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //
    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(seenListener);
    }
}