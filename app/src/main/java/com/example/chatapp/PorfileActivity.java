package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class PorfileActivity extends AppCompatActivity {

    CircleImageView img_profile;
    TextView username;
    TextView email;

    DatabaseReference ref;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porfile);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //取代actionBar
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img_profile=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        email=findViewById(R.id.useremail);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class); //用class承接fireabase 儲存的data
                username.setText(user.getUsername());
                email.setText(user.getEmail());

                if(user.getImageURL().equals("default")){
                    img_profile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(PorfileActivity.this).load(user.getImageURL()).into(img_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}