package com.zam.photos

import android.view.View

interface ClickListener {
    fun itemClicked(view: View?, position: Int)
}