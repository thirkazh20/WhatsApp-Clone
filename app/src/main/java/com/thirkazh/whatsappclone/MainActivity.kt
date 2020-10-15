package com.thirkazh.whatsappclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thirkazh.whatsappclone.adapter.SectionPagerAdapter
import com.thirkazh.whatsappclone.fragment.ChatsFragment
import com.thirkazh.whatsappclone.listener.FailureCallback
import com.thirkazh.whatsappclone.util.DATA_USERS
import com.thirkazh.whatsappclone.util.DATA_USER_PHONE
import com.thirkazh.whatsappclone.util.PERMISSION_REQUEST_READ_CONTACT
import com.thirkazh.whatsappclone.util.REQUEST_NEW_CHATS
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : AppCompatActivity(), FailureCallback {

    companion object {
        const val PARAM_NAME = "name"
        const val PARAM_PHONE = "phone"
    }

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val chatsFragment = ChatsFragment()
    private val firebaseAuth = FirebaseAuth.getInstance() // connect ke Firebase Authentication
    private var mSectionPagerAdapter: SectionPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatsFragment.setFailureCallbackListener(this)

        setSupportActionBar(toolbar)// menambahkan toolbar dari layout
        mSectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)

        container.adapter = mSectionPagerAdapter
        fab.setOnClickListener {
            onNewChat()
        }
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        resizeTabs()
        tabs.getTabAt(1)?.select()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position){ // ketika TabItem diklik atau TabItem aktif FloatingActionButton
                    0 -> fab.hide()   // akan hilang pada Tab pertama
                    1 -> fab.show()   // tetap ditampilkan pada Tab kedua
                    2 -> fab.hide()   // akan hilang pada Tab ketiga
                }
            }
        })
    }

    private fun onNewChat() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                AlertDialog.Builder(this)
                    .setTitle("Contacts Permission")
                    .setMessage("Aplikasi ingin mengakses kontak di Hp anda")
                    .setPositiveButton("Yes") {dialog, which ->
                        requestContactPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->
                    }
                    .show()
            }
            else {
                requestContactPermission()
            }
        } else {
            startNewActivity()
        }
    }

    private fun startNewActivity() {
        startActivityForResult(Intent(this, ContactsActivity::class.java), REQUEST_NEW_CHATS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                REQUEST_NEW_CHATS -> {
                    val name = data?.getStringExtra(PARAM_NAME) ?: ""
                    val phone = data?.getStringExtra(PARAM_PHONE) ?: ""
                    checkNewChatUser(name, phone)
                }
            }
        }
    }

    private fun checkNewChatUser(name: String, phone: String) {
        if (!name.isNullOrEmpty() && !phone.isNullOrEmpty()) {
            firebaseDb.collection(DATA_USERS)
                .whereEqualTo(DATA_USER_PHONE, phone)
                .get()
                .addOnSuccessListener {
                    if (it.documents.size > 0) {
                        chatsFragment.newChat(it.documents[0].id)
                    } else {
                        // jika tidak terdapat data, alertDialog akan muncul untuk konfirmasi
                        AlertDialog.Builder(this).setTitle("User not found")  // aplikasi mengirim pesan
                            .setMessage("$name akun tidak ditemukan, kirimkan pesan sms untuk menginstal aplikasi.")
                            .setPositiveButton("OK") { dialog, which ->
                                val intent = Intent(Intent.ACTION_VIEW) // intent implicit untuk kirim pesan
                                intent.data = Uri.parse("sms:$phone")
                                intent.putExtra("sms_body",
                                    "Hi I'm using this new cool WhatsAppClone app. You should install it too so we can chat there.")
                                startActivity(intent)
                            }
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show()
                    }
                }
                .addOnFailureListener {e ->
                    Toast.makeText(this,"An error occured. Please try again later",
                        Toast.LENGTH_SHORT) .show()
                    e.printStackTrace()
                }
        }
    }

    private fun requestContactPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permission: Array<out String>,
        grantResults: IntArray
    ){
        when(requestCode){
            PERMISSION_REQUEST_READ_CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    startNewActivity()
                }
            }
        }
    }

    private fun resizeTabs() {
        val layout = (tabs.getChildAt(0)as LinearLayout).getChildAt(0)as LinearLayout
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 0.4f
        layout.layoutParams= layoutParams
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onUserError() {
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_profile -> onProfile()
            R.id.action_logout -> onLogout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onProfile() {
        startActivity(Intent(this,ProfileActivity::class.java))
    }

    private fun onLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }
}