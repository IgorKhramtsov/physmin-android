package com.physmin.android.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.physmin.android.BuildConfig
import com.physmin.android.R
import kotlinx.android.synthetic.main.activity_tests_subjects.*
import java.util.*


class MainActivity: AppCompatActivity() {

    private var RC_SIGN_IN: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_subjects)

        if (BuildConfig.DEBUG) {
            val language_code = "ru"
            val dm: DisplayMetrics = resources.getDisplayMetrics()
            val conf: Configuration = resources.getConfiguration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                conf.setLocale(Locale(language_code.toLowerCase()))
            }
            resources.updateConfiguration(conf, dm)
        }

        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 8f

        menuItemView_progressive_concepts.setAction("Тестирование") {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
        menuItemView_progressive_concepts.setAction("Обучение") {}

        startAuthActivity()

        this.textViewSignOut.setOnClickListener { signOut() }
        this.textViewRegistration.setOnClickListener { upgradeAnonymousAcc() }

    }

    private fun upgradeAnonymousAcc() {
        startAuthActivity(true)
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            if (it.isSuccessful) startAuthActivity()
            else Toast.makeText(this, "Произошла ошибка.", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                updateProfileInfo()

                if (FirebaseAuth.getInstance().currentUser!!.metadata!!.creationTimestamp == FirebaseAuth.getInstance().currentUser!!.metadata!!.lastSignInTimestamp)
                    firstLogin()

            } else {
                if (response?.error != null) {
                    Toast.makeText(this, "Произошла ошибка " + response.error!!.message, Toast.LENGTH_SHORT).show()
                    startAuthActivity()
                } else {
                    if(FirebaseAuth.getInstance().currentUser == null) // if it`s not an upgrading event
                        this.finish()
                    return
                }

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private fun firstLogin() {
        Log.d("FirebaseAuth", "firstLogin")
        TODO("not implemented")
    }

    private fun startAuthActivity(upgrading: Boolean = false) {
        if (FirebaseAuth.getInstance().currentUser != null && !upgrading) {
            FirebaseAuth.getInstance().currentUser!!.reload()
            Log.d("FirebaseAuth", "startAuthActivity: user already logged")
            updateProfileInfo()
            return
        }

        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )
        if(!upgrading) providers.add(AuthUI.IdpConfig.AnonymousBuilder().build())

        // Create and launch sign-in intent
        val builder = AuthUI.getInstance().createSignInIntentBuilder()
        if (upgrading) builder.enableAnonymousUsersAutoUpgrade()
        startActivityForResult(builder.setAvailableProviders(providers).build(),
                RC_SIGN_IN)
    }

    private fun updateProfileInfo() {
        val user = FirebaseAuth.getInstance().currentUser!!
        if (user.isAnonymous) {
            textViewEmail.visibility = View.GONE
            textViewEmailVerification.visibility = View.GONE
            textViewSignOut.visibility = View.GONE
            textViewRegistration.visibility = View.VISIBLE
            textViewName.text = "Гость"
            textViewInitials.text = "Г"
            textViewStatistics.isClickable = false
            textViewStatistics.setTextColor(ContextCompat.getColor(this, R.color.textColorLightDisabled))
            textViewSettings.isClickable = false
            textViewSettings.setTextColor(ContextCompat.getColor(this, R.color.textColorLightDisabled))
        } else {
            var initials = ""
            if (user.displayName != null) {
                val words = user.displayName!!.split(' ')
                words.forEach { str ->
                    initials += str[0].toUpperCase() + "."
                }
            }

            textViewEmail.visibility = View.VISIBLE
            textViewSignOut.visibility = View.VISIBLE
            textViewRegistration.visibility = View.GONE

            textViewName.text = user.displayName
            textViewInitials.text = initials
            textViewEmail.text = user.email
            textViewEmailVerification.visibility = if (user.isEmailVerified) View.GONE else View.VISIBLE
            textViewStatistics.isClickable = true
            textViewStatistics.setTextColor(ContextCompat.getColor(this, R.color.textColorLight))
            textViewSettings.isClickable = true
            textViewSettings.setTextColor(ContextCompat.getColor(this, R.color.textColorLight))
        }
    }
}
