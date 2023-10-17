package com.example.androidstarting.callback

interface OnItemClickPost<T> {
    fun onItemClick(item: T, isVote: Boolean)
}