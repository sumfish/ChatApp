package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=txt_send.getText().toString();
                if(!msg.equals("")){
                    /** not assign receiver
                     *如果開個人聊天室的話 可以用intent包 對象userid過來
                     **/
                    // firebaseUser.getDisplayName()會拿不到，firebase auth不會存資料
                    sendMessage(firebaseUser.getUid(),"",userName,msg);
                }else{
                    Toast.makeText(MainActivity.this," You can't not send empty message",Toast.LENGTH_SHORT).show();
                }
                txt_send.setText("");
            }
        });

        ref= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        /***
         * 共同聊天室 (原先設定個別視窗)
         ***/
        ref.addValueEventListener(new ValueEventListener() { //一直存在的listener (如果改profile img就會立刻更新)
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
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_img);
                }

                readMessage(firebaseUser.getUid(),"",user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String senderName, String message){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        long time =new Date().getTime();
        reference.child("Chats").push().setValue(new Chat(sender,receiver, message, senderName, time)); //丟到firebase
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //load option menu
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //sign out
        switch(item.getItemId()){ //menu中的選擇只放sign out

            case R.id.profile:
                startActivity(new Intent(MainActivity.this,PorfileActivity.class));
                //finish(); //不能destroy掉 原本的task會消失就跳不回來
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
        ref.addValueEventListener(new ValueEventListener() { //現在會不斷刷新
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot shot:snapshot.getChildren()){
                    Chat chat=shot.getValue(Chat.class);

                    /**
                     * 個人的話就是 判斷 receiver==myid&&sender==userid || receiver==userid&&sender==myid
                     * 之後再add進mChat
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
}