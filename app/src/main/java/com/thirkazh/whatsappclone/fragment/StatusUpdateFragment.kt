package com.thirkazh.whatsappclone.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.thirkazh.whatsappclone.R
import com.thirkazh.whatsappclone.util.REQUEST_CODE_PHOTO
import com.thirkazh.whatsappclone.util.populateImage
import kotlinx.android.synthetic.main.fragment_status_update.*

class StatusUpdateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_update, container, false)
    }

}