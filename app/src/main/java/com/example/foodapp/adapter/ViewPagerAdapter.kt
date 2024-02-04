package com.example.foodapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.foodapp.LoginFragment
import com.example.foodapp.RegisterFragment
import com.example.foodapp.login

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    lateinit var a:String

    override fun getItemCount(): Int {
        return 2 // Количество фрагментов
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment() // Фрагмент 1
            1 -> RegisterFragment() // Фрагмент 2
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
