package com.thirkazh.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edt_email_reg_in
import kotlinx.android.synthetic.main.activity_login.edt_password_reg_in
import kotlinx.android.synthetic.main.activity_login.progress_layout_in
import kotlinx.android.synthetic.main.activity_login.til_email_in
import kotlinx.android.synthetic.main.activity_login.til_password_in
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        // mengecheck userId yang sedang aktif, jika ada, proses akan langsung intent ke hal.utama
        val user = firebaseAuth.currentUser?.uid
        if (user!=null){
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // menghilangkan ActionBar
        setContentView(R.layout.activity_login)

        setTextChangedListener(edt_email_reg_in, til_email_in)
        setTextChangedListener(edt_password_reg_in, til_password_in)
        progress_layout_in.setOnTouchListener { v, event -> true }

        btn_login.setOnClickListener {
            onLogin()
        }

        txt_signup.setOnClickListener {
            onSignup()
        }
    }

    private fun setTextChangedListener(edt: EditText, til: TextInputLayout) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            // ketika editText diubah memastikan TextInputLayout tidak menunjukkan pesan error
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }
        })
    }


    private fun onSignup() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun onLogin() {
        var proceed = true
        if (edt_email_reg_in.text.isNullOrEmpty()) { // check jika EditText kosong
            til_email_in.error = "Required Password" // TextInputLayout(til) menampilkan pesan
            til_email_in.isErrorEnabled = true // mengubah state til yang sebelumnya tidak
            proceed = false // menampilkan error sekarang menampilkan
        }

        if (edt_password_reg_in.text.isNullOrEmpty()) {
            til_password_in.error = "Required Password"
            til_password_in.isErrorEnabled = true
            proceed = false
        }

        if (proceed) {
            progress_layout_in.visibility = View.VISIBLE // menampilkan ProgressBar
            firebaseAuth.signInWithEmailAndPassword(  // untuk menunjukkan bahwa ada
                edt_email_reg_in.text.toString(), // proses yang sedang dilakukan
                edt_password_reg_in.text.toString() // mengubah data dalam editText jadi string
            )
                .addOnCompleteListener { task ->  // jika proses sebelumnya selesai dilaksanakan
                    if (!task.isSuccessful){
                        progress_layout_in.visibility = View.GONE
                        Toast.makeText(this@LoginActivity,"Login Error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ) .show()
                    }
                }

                .addOnFailureListener { exception ->
                    progress_layout_in.visibility = View.GONE // ProgressBar dihilangkan
                    exception.printStackTrace()  // ditampilkan log errornya
                }
        }
    }

    override fun onStart() {
        super.onStart() // method yang pertama kali dijalankan sebelum method lainnya
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }
    override fun onStop() {
        super.onStop() // dijalankan jika proses dalam activity selesai atau dihentikan system
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

}