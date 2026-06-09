package com.app.panditji.utils.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

abstract class UserBaseFragment<VB:ViewBinding> : Fragment() {

        lateinit var binding:VB
        abstract fun setLayout():Int
        lateinit var mContext: Context


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
            if(!this::binding.isInitialized){
                initBinding(inflater,container)
                initView(savedInstanceState)
            }
            return binding.root
        }

        abstract fun initView(savedInstanceState: Bundle?)

        private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
            binding  = DataBindingUtil.inflate(inflater,setLayout(),container,false)
        }

    }