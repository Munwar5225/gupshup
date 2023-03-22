package com.example.gupshup.SignUpSignIn
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gupshup.MainActivity
import com.example.gupshup.Models.UserModel
import com.example.gupshup.R
import com.example.gupshup.SignupOptions.FacebookLogin
import com.example.gupshup.SignupOptions.PhoneAuthActivity
import com.example.gupshup.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.*
class SignUp : AppCompatActivity() {
    var binding: ActivitySignUpBinding? = null
    private var mAuth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var dialog: ProgressDialog? = null
    private var authStateListener: AuthStateListener? = null
    private var googleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(binding!!.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setTitle("Creating your Account ")
        dialog!!.setMessage("please wait....")
        val googleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(
            this@SignUp, googleSignInOptions
        )
        authStateListener = AuthStateListener {
            val user = mAuth!!.currentUser
            if (user != null) {
                updateUI(user)
            } else {
                updateUI(null)
            }
        }
        binding!!.btnSignUp.setOnClickListener { view: View? ->
            dialog!!.show()
            mAuth!!.createUserWithEmailAndPassword(
                binding!!.etEmails.text.toString(),
                binding!!.etPassword.text.toString()
            )
                .addOnCompleteListener { task: Task<AuthResult> ->
                    dialog!!.dismiss()
                    if (task.isSuccessful) {
                        val userModel = UserModel(
                            binding!!.etUsers.text.toString(), binding!!.etEmails.text.toString(),
                            binding!!.etPassword.text.toString()
                        )
                        val userId = Objects.requireNonNull(task.result.user)?.uid
                        if (userId != null) {
                            database!!.reference.child("User").child(userId).setValue(userModel)
                        }
                        Toast.makeText(
                            this@SignUp,
                            "Toast User Created Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignUp, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SignUp,
                            Objects.requireNonNull(task.exception).toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
        binding!!.tvAlreadyAccount.setOnClickListener { view: View? ->
            val intent = Intent(this@SignUp, SignInActivity::class.java)
            startActivity(intent)
        }
        binding!!.btnGooglr.setOnClickListener { view: View? ->
            val intent = googleSignInClient!!.signInIntent
            startActivityForResult(intent, REQ_ONE_TAP)
        }
        binding!!.btnFacebooks.setOnClickListener { view: View? ->
            val intent = Intent(this@SignUp, FacebookLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }
        binding!!.tvMobileNumber.setOnClickListener { view: View? ->
            val intent = Intent(this, PhoneAuthActivity::class.java)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ONE_TAP) {
            val googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = googleSignInAccountTask.getResult(
                    ApiException::class.java
                )
                Toast.makeText(this, "Signed into your Account", Toast.LENGTH_SHORT).show()
                GoogleSignInWithGoogle(account.idToken)
            } catch (e: ApiException) {
                e.statusCode
            }
        }
    }

    private fun GoogleSignInWithGoogle(idToken: String?) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = mAuth!!.currentUser
                    val userModel = UserModel()
                    assert(user != null)
                    userModel.userId = user!!.uid
                    userModel.username = user.displayName
                    userModel.picture = Objects.requireNonNull(user.photoUrl).toString()
                    database!!.reference.child("users").child(user.uid).setValue(userModel)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(authStateListener!!)
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this@SignUp, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val REQ_ONE_TAP = 2
    }
}