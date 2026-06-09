package com.app.panditji.custom.customVP

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomVP :ViewPager{
    var childId = 0
    constructor(context: Context) : super(context)
    constructor(context : Context, attrs : AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (childId > 0) {
            val  pager = findViewById<ViewPager>(childId)

            pager?.requestDisallowInterceptTouchEvent(true)

        }
        return super.onInterceptTouchEvent(ev)
    }




}