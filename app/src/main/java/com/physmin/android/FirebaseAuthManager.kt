package com.physmin.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.physmin.android.R
import com.physmin.android.activities.MainActivity
import kotlinx.android.synthetic.main.activity_menu_subjects.*

class FirebaseAuthManager(cntx: MainActivity) {
    private var RC_SIGN_IN: Int = 0
    private val context = cntx

    fun firstLogin() {
        Log.d("FirebaseAuth", "firstLogin")
        // TODO("not implemented")
    }

    fun upgradeAnonymousAcc() {
        startAuthActivity(true)
    }

    fun signOut() {
        if (FirebaseAuth.getInstance().currentUser?.isAnonymous == true) {
            FirebaseAuth.getInstance().currentUser!!.delete()
        }

        AuthUI.getInstance().signOut(context).addOnCompleteListener {
            if (it.isSuccessful) startAuthActivity()
            else Toast.makeText(context, "Произошла ошибка.", Toast.LENGTH_SHORT).show()
        }

    }

    fun startAuthActivity(upgrading: Boolean = false) {
        if (FirebaseAuth.getInstance().currentUser != null && !upgrading) {
            FirebaseAuth.getInstance().currentUser!!.reload()
            context.updateProfileInfo(FirebaseAuth.getInstance().currentUser!!)
            return
        }

        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )
        if (!upgrading) providers.add(AuthUI.IdpConfig.AnonymousBuilder().build())

        // Create and launch sign-in intent
        val builder = AuthUI.getInstance().createSignInIntentBuilder()
        if (upgrading) builder.enableAnonymousUsersAutoUpgrade()
        context.startActivityForResult(builder.setAvailableProviders(providers).build(),
                RC_SIGN_IN)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                context.updateProfileInfo(FirebaseAuth.getInstance().currentUser!!)

                if (FirebaseAuth.getInstance().currentUser!!.metadata!!.creationTimestamp == FirebaseAuth.getInstance().currentUser!!.metadata!!.lastSignInTimestamp)
                    firstLogin()

            } else {
                if (response?.error != null) {
                    if (response.error!!.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT)
                        Toast.makeText(context, "Ошибка. Пользователь уже существует. Выйдите из аккаунта гостя. ", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(context, "Произошла ошибка. " + response.error!!.message, Toast.LENGTH_SHORT).show()
                    startAuthActivity()
                } else {
                    if (FirebaseAuth.getInstance().currentUser == null) // if it`s not an upgrading event
                        context.finish()
                    return
                }

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}