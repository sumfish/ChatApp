package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PorfileActivity extends AppCompatActivity {

    CircleImageView img_profile;
    TextView username;
    TextView email;

    DatabaseReference ref;
    FirebaseUser fuser;

    //img storage
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;

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

        //get firebase storage reference
        storageReference= FirebaseStorage.getInstance().getReference("upload");

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        ref.addValueEventListener(new ValueEventListener() { //update profile into from firebase
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

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

    }

    private void openImage(){ //選取圖片intent
        Intent intent= new Intent();
        intent.setType("image/*"); //指定選取裝置裡的圖片檔案-指定MIME Type
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST); //不是回傳圖片(可能太大)，是回傳url路徑
    }

    private String getFileExtension(Uri url){ //get副檔名
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(url));
    }

    private void uploadImage(){
        final ProgressDialog pd =new ProgressDialog(PorfileActivity.this); //畫面會秀loading progress
        pd.setMessage("UPLOADING");
        pd.show();

        if(imageUri!=null){

            //在firebase以time命名檔案
            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();

                        ref=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map =new HashMap<>();
                        map.put("imageURL",mUri);
                        ref.updateChildren(map); //修改原本url

                        pd.dismiss();
                    }else{
                        Toast.makeText(PorfileActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PorfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else{
            Toast.makeText(PorfileActivity.this,"No image selected!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //處理openImage 那邊的回傳結果
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode ==RESULT_OK
                && data!=null && data.getData()!=null){
            imageUri=data.getData();

            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(PorfileActivity.this,"Upload in progress!",Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }
        }
    }
}