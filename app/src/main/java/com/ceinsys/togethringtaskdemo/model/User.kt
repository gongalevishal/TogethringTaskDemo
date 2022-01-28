package com.ceinsys.togethringtaskdemo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users_data")
data class User(

    @ColumnInfo(name = "id")
    @SerializedName("id")
    val userId: String?,

    @ColumnInfo(name = "email")
    @SerializedName("email")
    val email: String?,

    @ColumnInfo(name = "first_name")
    @SerializedName("first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    @SerializedName("last_name")
    val lastName: String?,

    @ColumnInfo(name = "avatar")
    @SerializedName("avatar")
    val avatar: String?,
){
    @PrimaryKey(autoGenerate = true)
    var UUId: Int = 0
}
