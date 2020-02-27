package com.physmin.android.activities

import FirebaseAuthManager
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
import com.google.firebase.auth.FirebaseUser
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

    private var functions: FirebaseFunctions = FirebaseFunctions.getInstance("europe-west1")

    private val topicMap: Map<String, MenuItemView> by lazy {
        mapOf(
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Concepts" to menuItemView_progressive_concepts,
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Graphs" to menuItemView_progressive_graphs,
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Final" to menuItemView_progressive_final
        )
    }

    lateinit var authManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_subjects)
        authManager = FirebaseAuthManager(this)

        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 8f
        if (BuildConfig.DEBUG) {
            changeLocaleRu()
        }

        this.textViewSignOut.setOnClickListener { authManager.signOut() }
        this.textViewRegistration.setOnClickListener { authManager.upgradeAnonymousAcc() }

        authManager.startAuthActivity()
        loadProgress()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        authManager.onActivityResult(requestCode, resultCode, data)
    }

    fun updateProfileInfo(user: FirebaseUser) {
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
            val initials = getInitials(user.displayName)

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

    private fun getInitials(name: String?): String {
        var initials = ""
        name?.split(' ')?.forEach { str ->
            initials += str[0].toUpperCase() + "."
        }
        return initials
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
