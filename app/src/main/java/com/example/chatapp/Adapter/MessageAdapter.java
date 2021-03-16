package com.example.chatapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MainActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private List<Chat> mChats;
    private String imageurl;

    FirebaseUser fuser;
    DatabaseReference ref;

    public MessageAdapter(Context mContext, List<Chat> mChats, String imageurl) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageurl =imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {
        Chat chat=mChats.get(position);
        holder.show_msg.setText(chat.getMessage());
        holder.sender_name.setText(chat.getSenderName());

        /**
         * 可以再想想日期的部分
         * */
        String sendTime= new SimpleDateFormat("HH:mm a", Locale.ENGLISH).format(chat.getMsgTime());
        holder.msg_time.setText(sendTime);

        ref= FirebaseDatabase.getInstance().getReference("Users").child(chat.getSender());
        ref.addListenerForSingleValueEvent(new ValueEventListener() { //只做一次listener拿sender的imageURL
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user= snapshot.getValue(User.class);
                    imageurl=user.getImageURL();

                if(imageurl.equals("default")){
                    holder.profile_img.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(mContext).load(imageurl).into(holder.profile_img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //已讀顯示
        //if(position==mChats.size()-1) //如果只在最新一則顯示的話
        if(chat.getSeenPeople().size()>1){
            holder.msg_seen.setText("已讀"+(chat.getSeenPeople().size()-1));
        }else{
            holder.msg_seen.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_msg;
        public ImageView profile_img;
        public TextView msg_time;
        public TextView sender_name;
        public TextView msg_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_msg=itemView.findViewById(R.id.show_message);
            profile_img=itemView.findViewById(R.id.profile_image);
            msg_time=itemView.findViewById(R.id.msg_time);
            sender_name=itemView.findViewById(R.id.sendername);
            msg_seen=itemView.findViewById(R.id.msg_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChats.get(position).getSender().equals(fuser.getUid())){
            return  MSG_TYPE_RIGHT; //發信訊息
        }else{
            return MSG_TYPE_LEFT; //收到訊息
        }
    }
}
