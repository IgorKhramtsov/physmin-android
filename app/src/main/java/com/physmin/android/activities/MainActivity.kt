package com.physmin.android.activities

import com.physmin.android.FirebaseAuthManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.physmin.android.App
import com.physmin.android.BuildConfig
import com.physmin.android.R
import com.physmin.android.fragments.RC_SUCC
import com.physmin.android.views.MenuItemView
import kotlinx.android.synthetic.main.activity_menu_subjects.*
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.HashMap

const val RC_PLAY_BUNDLE = 1

class MainActivity: AppCompatActivity() {

    private lateinit var functions: FirebaseFunctions
    private val authManager: FirebaseAuthManager = FirebaseAuthManager(this)
    private val topicMap: Map<String, MenuItemView> by lazy {
        mapOf(
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Concepts" to menuItemView_progressive_concepts,
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Graphs" to menuItemView_progressive_graphs,
                "/Subjects/Mechanics/Branches/Kinematics/Chapters/ProgressiveMovement/Topics/Final" to menuItemView_progressive_final
        )
    }
    private var userProgressObject: HashMap<String, *>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_subjects)

        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 8f
        if (BuildConfig.DEBUG) {
            changeLocaleRu()
        }

        this.textViewSignOut.setOnClickListener { authManager.signOut() }
        this.textViewRegistration.setOnClickListener { authManager.upgradeAnonymousAcc() }

        functions = FirebaseFunctions.getInstance("europe-west1")
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
            userProgressObject = res

            updateProgress()
        }
    }

    private fun updateProgress() {
        val userProgressObject = this.userProgressObject ?: return

        var data: HashMap<String, Int>
        var completion: Float
        for (topic in topicMap) {
            if (!userProgressObject.contains(topic.key))
                continue
            if (userProgressObject[topic.key] !is HashMap<*, *>) {
                Log.e("MainActivity", "loadProgress: fetched data[${topic.key}] is not a HashMap")
                continue
            }
            data = userProgressObject[topic.key] as HashMap<String, Int>
            completion = data["completed"]!!.toFloat() / data["totalExercise"]!!.toFloat()
            val isExam = (completion >= 1f)

            topic.value.setComplenteessPercent(completion)
            topic.value.clearActions()
            if (completion >= 0)
                topic.value.setAction(if (isExam) "Контрольный тест" else "Практика") {
                    val intent = Intent(this, TaskPlayerActivity::class.java)
                    intent.putExtra("topicPath", topic.key)
                    intent.putExtra("isExam", isExam)
                    startActivityForResult(intent, RC_PLAY_BUNDLE)
                }
            topic.value.setAction("Обучение") {}
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

        if (requestCode == RC_PLAY_BUNDLE) {
            if (resultCode == RC_SUCC) {
                val topicPath = data!!.getStringExtra("topicPath")!!
                val data = (userProgressObject!![topicPath] as HashMap<String, Any>)
                data["completed"] = (data["completed"] as Int).inc()
                updateProgress()
                loadProgress()
            }

        } else {
            authManager.onActivityResult(requestCode, resultCode, data)
        }
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
