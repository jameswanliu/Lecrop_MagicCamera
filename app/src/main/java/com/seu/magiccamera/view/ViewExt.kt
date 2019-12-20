package com.seu.magiccamera.view

import android.view.View


inline fun View.onClick(noinline block: () -> Unit) {
    setOnClickListener { block() }
}