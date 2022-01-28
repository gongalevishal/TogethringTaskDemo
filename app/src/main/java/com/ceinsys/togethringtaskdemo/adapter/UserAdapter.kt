package com.ceinsys.togethringtaskdemo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ceinsys.togethringtaskdemo.R
import com.ceinsys.togethringtaskdemo.databinding.RowItemUserBinding
import com.ceinsys.togethringtaskdemo.model.User

class UserAdapter(val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    @SuppressLint("NotifyDataSetChanged")
    fun updateUserList(newDogList: List<User>) {
        userList.clear()
        userList.addAll(newDogList)
        notifyDataSetChanged()
    }

    class UserViewHolder(binding: RowItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val mainLL = binding.mainLL
        val Image = binding.userImage
        val Name = binding.userName
        val email = binding.userEmail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            RowItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val context = holder.itemView.context
        val currentItem = userList[position]
        holder.Name.text = currentItem.firstName + " " + currentItem.lastName
        holder.email.text = currentItem.email
        holder.Image.load(currentItem.avatar)

        if (position % 2 == 0) {
            holder.mainLL.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.gray
                )
            )
        } else {
            holder.mainLL.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}