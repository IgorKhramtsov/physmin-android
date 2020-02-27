package com.physmin.android.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.physmin.android.App
import com.physmin.android.BuildConfig
import com.physmin.android.R
import com.physmin.android.views.MenuItemView
import kotlinx.android.synthetic.main.activity_tests_subjects.*
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.HashMap


class MainActivity: AppCompatActivity() {

    private var RC_SIGN_IN: Int = 0
    private var functions: FirebaseFunctions = FirebaseFunctions.getInstance("europe-west1")

    private val topicMap: Map<String, MenuItemView> by lazy {
        mapOf(
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Concepts" to menuItemView_progressive_concepts,
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Graphs" to menuItemView_progressive_graphs,
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Final" to menuItemView_progressive_final
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_subjects)

        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 8f
        if (BuildConfig.DEBUG) {
            changeLocaleRu()
        }

        startAuthActivity()

        loadProgress()






        this.textViewSignOut.setOnClickListener { signOut() }
        this.textViewRegistration.setOnClickListener { upgradeAnonymousAcc() }

    }

    private fun loadProgress() {
        fetchProgress().addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.e("MainActivity", "Cant fetch user progress.")
                return@addOnCompleteListener
            }
            val res = it.result!!

            var data: HashMap<String, Int>
            var completion: Float
            for (topic in topicMap) {
                if (!res.contains(topic.key))
                    continue
                if (res[topic.key] !is HashMap<*, *>) {
                    Log.e("MainActivity", "loadProgress: fetched data[${topic.key}] is not a HashMap")
                    continue
                }
                data = res[topic.key] as HashMap<String, Int>
                completion = data["completed"]!!.toFloat() / data["totalExercise"]!!.toFloat()

                topic.value.setComplenteessPercent(completion)
                if (completion <= 1)
                    topic.value.setAction(if (completion < 1) "Практика" else "Контрольный тест") {
                        val intent = Intent(this, TestActivity::class.java)
                        intent.putExtra("topic", topic.key)
                        startActivity(intent)
                    }
                topic.value.setAction("Обучение") {}
            }

        }
    }

    private fun fetchProgress(): Task<HashMap<String, *>> {
        return functions
                .getHttpsCallable((application as App).API_LINKS.getUserProgress)
                .call()
                .continueWith { task ->
                    when (task.exception) {
                        is SocketTimeoutException -> {
                            Log.e("MainActivity", "fetchTopicExercise() - Timeout!")
//                            showError(ERROR_TIMEOUT)
                        }
                        is FirebaseFunctionsException -> {
                            Log.e("MainActivity", "FirebaseException [${(task.exception as FirebaseFunctionsException).code}], ${(task.exception as FirebaseFunctionsException).message}")
//                            showError(ERROR_SERVER)
                        }
                        is Exception -> {
                            Log.e("MainActivity", "UnknownError ${task.exception.toString()}")
//                            showError(ERROR_UNKNOWN)
                        }
                    }

                    val result = task.result?.data as HashMap<String, *>
                    result
                }
    }

    private fun setItemViewAction(item: MenuItemView, actionName: String, topicPath: String) {
        item.setAction(actionName) {
            val intent = Intent(this, TestActivity::class.java)
            intent.putExtra("topic", topicPath)
            startActivity(intent)
        }
    }

    private fun upgradeAnonymousAcc() {
        startAuthActivity(true)
    }

    private fun signOut() {
        if (FirebaseAuth.getInstance().currentUser?.isAnonymous == true) {
            FirebaseAuth.getInstance().currentUser!!.delete()
        }

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
                    if (response.error!!.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT)
                        Toast.makeText(this, "Ошибка. Пользователь уже существует. Выйдите из аккаунта гостя. ", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(this, "Произошла ошибка. " + response.error!!.message, Toast.LENGTH_SHORT).show()
                    startAuthActivity()
                } else {
                    if (FirebaseAuth.getInstance().currentUser == null) // if it`s not an upgrading event
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
        // TODO("not implemented")
    }

    private fun startAuthActivity(upgrading: Boolean = false) {
        if (FirebaseAuth.getInstance().currentUser != null && !upgrading) {
            FirebaseAuth.getInstance().currentUser!!.reload()
            updateProfileInfo()
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
        startActivityForResult(builder.setAvailableProviders(providers).build(),
                RC_SIGN_IN)
    }

    private fun updateProfileInfo() {
        val user = FirebaseAuth.getInstance().currentUser!!
        if (user.isAnonymous) {
            textViewEmail.visibility = View.GONE
            textViewEmailVerification.visibility = View.GONE
            textViewSignOut.visibility = View.VISIBLE
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


    private fun changeLocaleRu() {
        val language_code = "ru"
        val dm: DisplayMetrics = resources.getDisplayMetrics()
        val conf: Configuration = resources.getConfiguration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(Locale(language_code.toLowerCase()))
        }
        resources.updateConfiguration(conf, dm)
    }
}
