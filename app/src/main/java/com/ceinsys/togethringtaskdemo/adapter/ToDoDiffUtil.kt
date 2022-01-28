package com.ceinsys.togethringtaskdemo.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ceinsys.togethringtaskdemo.model.User

class UserDiffUtil(
    private val oldList: ArrayList<User>,
    private val newList: ArrayList<User>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].userId == newList[newItemPosition].userId
                && oldList[oldItemPosition].firstName == newList[newItemPosition].firstName
                && oldList[oldItemPosition].lastName == newList[newItemPosition].lastName
    }
}