package com.thirkazh.whatsappclone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.thirkazh.whatsappclone.MainActivity
import com.thirkazh.whatsappclone.fragment.ChatsFragment
import com.thirkazh.whatsappclone.fragment.StatusListFragment
import com.thirkazh.whatsappclone.fragment.StatusUpdateFragment

class SectionPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val chatsFragment = ChatsFragment()
    private val statusUpdateFragment = StatusUpdateFragment()
    private val statusFragment = StatusListFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> statusUpdateFragment // menempatkan StatusUpdateFragment di posisi pertama
            1 -> chatsFragment        // ChatsFragment posisi kedua dalam adapter
            2 -> statusFragment       // StatusListFragment posisi ketiga dalam adapter
            else -> chatsFragment     // menjadikan ChatsFragment default position
        }
    }

    override fun getCount(): Int {
        return 3
    }
}