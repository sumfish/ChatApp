package com.example.chatapp.RoomDataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities={ChatData.class}, version = 1, exportSchema = false) //export data to a document
public abstract class DataBase extends RoomDatabase {

    public static final String DB_NAME = "ChatData.db";
    private static volatile DataBase instance;

    public static synchronized DataBase getInstance(Context context){
        if(instance==null){
            instance= create(context); // new db
        }
        return instance;
    }

    private static DataBase create(final Context context){
        return Room.databaseBuilder(context,DataBase.class,DB_NAME).build();
    }

    // return DAO
    public abstract ChatDataUao getChatUao();
}
