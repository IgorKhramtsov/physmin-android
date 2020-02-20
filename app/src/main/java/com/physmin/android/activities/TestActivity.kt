package com.physmin.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.commit
import com.physmin.android.dev.fragments.FragmentTestList
import com.physmin.android.fragments.FragmentTestComplete
import com.physmin.android.fragments.tasks.*
import com.physmin.android.views.LoadingHorBar
import com.physmin.android.views.ProgressBarView
import com.physmin.android.views.TimerView
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.physmin.android.*
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.activity_test.view.*
import org.json.JSONObject
import parseTask
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.SocketTimeoutException
import kotlin.concurrent.schedule

const val ERROR_UNKNOWN = 0
const val ERROR_TIMEOUT = 1
const val ERROR_SERVER = 2

class TestActivity: AppCompatActivity(), FragmentTestBase.TestCompletingListener {
    private lateinit var firebaseFunctions: FirebaseFunctions
    lateinit var tasksList: TasksList

    var testController: TestController? = null
    lateinit var progressBarView: ProgressBarView
    private lateinit var debugTextView: TextView
    private lateinit var errorTextView: TextView
    lateinit var timerView: TimerView
    lateinit var buttonNext: Button
    lateinit var loadingAnimation: LoadingHorBar
    lateinit var floatingMenu: FloatingActionsMenu
    var API_LINKS: API = API_prod()
    val topic = "Concepts"

    private var debugTextViewCalls = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        debugTextView = test_layout.debugTextView
        errorTextView = test_layout.errorTextView
        buttonNext = test_layout.button_test_next
        timerView = test_layout.Timer
        progressBarView = test_layout.progressBar
        loadingAnimation = test_layout.loadingHorBar
        floatingMenu = test_layout.floating_menu

        timerView.visibility = GONE
        floatingMenu.visibility = GONE
        debugTextView.visibility = GONE

        firebaseFunctions = FirebaseFunctions.getInstance("europe-west1")
        if (isDev()) {
            API_LINKS = API_debug()
            floatingMenu.action_next.setOnClickListener { switchTest() }
            floatingMenu.action_list.setOnClickListener {
                supportFragmentManager.commit {
                    replace(R.id.test_host_fragment, FragmentTestList(tasksList.getAsArray()))
                    addToBackStack("home")
                    floatingMenu.collapse()
                }
            }
        } else {
            floatingMenu.removeButton(this.action_list)
            floatingMenu.removeButton(this.action_next)
        }

        loadBundle(topic)

        // Greeting
//        supportFragmentManager.transaction {
//            replace(R.id.test_host_fragment, FragmentTestHello.newInstance())
//        }
    }

    private fun loadBundle(topic: String) {
        errorTextView.visibility = GONE
        progressBarView.hide()
        hideButtonNext()
        loadingAnimation.show()

        fetchTopicExercise(topic).addOnCompleteListener {
            loadingAnimation.hide()
            if (it.isSuccessful)
                processTestBundle(it.result!!)
        }
    }

    private fun fetchTestBundle(): Task<String> {
        return firebaseFunctions
                .getHttpsCallable(API_LINKS.getTest)
                .call()
                .continueWith { task ->
                    when (task.exception) {
                        is SocketTimeoutException -> {
                            Log.e("TestActivity", "fetchTestBundle() - Timeout!")
                            showError(ERROR_TIMEOUT)
                        }
                        is FirebaseFunctionsException -> {
                            Log.e("TestActivity", "FirebaseException [${(task.exception as FirebaseFunctionsException).code}], ${(task.exception as FirebaseFunctionsException).message}")
                            showError(ERROR_SERVER)
                        }
                        is Exception -> {
                            Log.e("TestActivity", "UnknownError ${task.exception.toString()}")
                            showError(ERROR_UNKNOWN)
                        }
                    }

                    val result = task.result?.data as String
                    val file = File(this.applicationContext.filesDir, "test_bundle.json")
                    FileOutputStream(file).use { stream ->
                        stream.write(result.toByteArray())
                    }
                    result
                }
    }

    private fun fetchTopicExercise(topic: String): Task<HashMap<String, *>> {
        val data = hashMapOf(
                "topic" to topic
        )
        return firebaseFunctions
                .getHttpsCallable(API_LINKS.getExercise)
                .call(data)
                .continueWith { task ->
                    when (task.exception) {
                        is SocketTimeoutException -> {
                            Log.e("TestActivity", "fetchTopicExercise() - Timeout!")
                            showError(ERROR_TIMEOUT)
                        }
                        is FirebaseFunctionsException -> {
                            Log.e("TestActivity", "FirebaseException [${(task.exception as FirebaseFunctionsException).code}], ${(task.exception as FirebaseFunctionsException).message}")
                            showError(ERROR_SERVER)
                        }
                        is Exception -> {
                            Log.e("TestActivity", "UnknownError ${task.exception.toString()}")
                            showError(ERROR_UNKNOWN)
                        }
                    }

                    val result = task.result?.data as HashMap<String, *>
//                    val file = File(this.applicationContext.filesDir, "test_bundle.json")
//                    FileOutputStream(file).use { stream ->
//                        stream.write(result.toByteArray())
//                    }
                    result
                }
    }

    private fun processTestBundle(levelBundle: HashMap<String, *>) {
        showButtonNext()
        buttonNext.text = getString(R.string.messageButtonNext)

        val bundleID = levelBundle["bundleId"]
        val array = levelBundle["tasks"] as ArrayList<TaskObject>
        this.tasksList = TasksList(array)

        progressBar.segmentCount = array.count()

        buttonNext.setOnClickListener { switchTest() }
    }

    fun switchTest(taskObj: TaskObject? = null, suppressCallback: Boolean = false) {
        timerView.visibility = VISIBLE
        floatingMenu.visibility = VISIBLE
        progressBarView.show()
        if (!suppressCallback)
            onTestSwitch()
        if (tasksList.isEnd())
            return

        supportFragmentManager.commit {
            replace(R.id.test_host_fragment, parseTask(taskObj ?: tasksList.pop()))
            timerView.restart()
            hideButtonNext()
        }
    }

    private fun onTestSwitch() {
        testController?.also {
            if (it.isAnswersCorrect() || isDev()) {
                progressBarView.addSegment()
                if (progressBarView.isAllDone())
                    onBundleComplete()
            } else {
                tasksList.pushCurrentToBack()
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            if (progressBarView.getCompletedCount() <= 0) {
                super.onBackPressed()
                return
            }

            AlertDialog.Builder(this)
                    .setTitle("Вы уверены?")
                    .setMessage("Ваш прогресс будет потерян")
                    .setPositiveButton("Да") { _, _ ->
                        super.onBackPressed()
                    }
                    .setNegativeButton("Отмена") { _, _ -> }
                    .show()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun showDebugMessage(text: String) {
        debugTextView.text = text
        debugTextView.visibility = VISIBLE
        debugTextView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null))
        val cachedCalls = ++debugTextViewCalls
        java.util.Timer().schedule(5000) {
            runOnUiThread {
                if (debugTextViewCalls == cachedCalls) {
                    debugTextView.text = ""
                    debugTextView.visibility = GONE
                    debugTextView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))
                }
            }
        }
    }

    private fun showError(errorCode: Int) {
        errorTextView.text = when (errorCode) {
            ERROR_TIMEOUT -> getString(R.string.messageTimeoutError)
            ERROR_SERVER -> getString(R.string.messageServerError)
            else -> getString(R.string.messageUnknownError)
        }
        errorTextView.visibility = VISIBLE
        buttonNext.text = getString(R.string.messageButtonTryAgain)
        buttonNext.setOnClickListener { loadBundle(topic) }
        showButtonNext()
    }

    private fun showButtonNext() {
        buttonNext.visibility = VISIBLE
    }

    private fun hideButtonNext() {
        buttonNext.visibility = GONE
    }

    fun onBundleComplete() {
//        showButtonNext()
//        onTestSwitch()
//        buttonNext.setOnClickListener {
        supportFragmentManager.commit {
            replace(R.id.test_host_fragment, FragmentTestComplete())
            timerView.visibility = GONE
            progressBarView.visibility = GONE
            floatingMenu.visibility = GONE
            hideButtonNext()
        }
//        }
    }

    override fun onTestComplete() {
        showButtonNext()
    }

    override fun onTestCompleteRejected() {
        hideButtonNext()
    }

}