package com.example.chatapp.RoomDataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChatDataUao {
    String tableName="Chats";

    @Insert //if something go wrong, replace
    void insertData(ChatData chatData);

    @Update
    void updateDate(ChatData chatData);

    //delete all
    @Query("DELETE FROM "+tableName)
    void deleteAll();

    // delete data
    @Query("DELETE FROM "+tableName+" WHERE id= :id")
    void deleteData(String id);

    @Query("SELECT * FROM " + tableName +" WHERE id= :id")
    List<ChatData> findDataByID(int id);

    @Query("SELECT * FROM " + tableName)
    List<ChatData> findAllData();

}
