package com.physmin.android.activities

import com.physmin.android.BundleController
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.physmin.android.fragments.tasks.*
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.physmin.android.*
import com.physmin.android.fragments.FragmentTestComplete
import kotlinx.android.synthetic.main.activity_task_player.*
import kotlinx.android.synthetic.main.activity_task_player.view.*
import java.lang.Exception
import java.net.SocketTimeoutException
import kotlin.concurrent.schedule

const val ERROR_UNKNOWN = 0
const val ERROR_TIMEOUT = 1
const val ERROR_SERVER = 2

class TaskPlayerActivity: AppCompatActivity(), FragmentTaskBase.TestCompletingListener, BundleController.BundleControllerListener {
    private var debugTextViewCalls = 0
    private var topicPath: String = ""
    private var isExam: Boolean = false

    private var bundleController: BundleController? = null
    private val firebaseFunctions = FirebaseFunctions.getInstance("europe-west1")
    private val links by lazy { (application as App).API_LINKS }

    private val debugTextView by lazy { test_layout.debugTextView }
    private val errorTextView by lazy { test_layout.errorTextView }
    private val buttonNext by lazy { test_layout.button_test_next }
    private val timerView by lazy { test_layout.Timer }
    private val progressBarView by lazy { test_layout.progressBar }
    private val loadingAnimation by lazy { test_layout.loadingHorBar }
    private val floatingMenu by lazy { test_layout.floating_menu }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_player)
        topicPath = intent.getStringExtra("topicPath")
                ?: throw Exception("topic parameter not passed")
        isExam = intent.getBooleanExtra("isExam", false)

        timerView.Hide()
        floatingMenu.Hide()
        debugTextView.Hide()

        if (isDev()) {
            floatingMenu.action_next.setOnClickListener { switchTask() }
        } else {
            floatingMenu.removeButton(this.action_list)
            floatingMenu.removeButton(this.action_next)
        }

        loadBundle()

        // Greeting
//        supportFragmentManager.transaction {
//            replace(R.id.test_host_fragment, FragmentTestHello.newInstance())
//        }
    }

    private fun loadBundle() {
        errorTextView.Hide()
        progressBarView.hide()
        buttonNext.Hide()
        loadingAnimation.show()

        fetchBundle().addOnCompleteListener {
            loadingAnimation.hide()
            if (!it.isSuccessful)
                return@addOnCompleteListener

            buttonNext.Show()
            buttonNext.text = getString(R.string.messageButtonNext)
            bundleController = BundleController(it.result!!, isExam, timerView).apply { setListener(this@TaskPlayerActivity) }
            progressBar.segmentCount = bundleController!!.getTasksCount()
            buttonNext.setOnClickListener { switchTask() }
        }
    }

    private fun fetchBundle(): Task<HashMap<String, *>> {
        val data = hashMapOf(
                "topic" to topicPath,
                "isExam" to isExam
        )

        return firebaseFunctions
                .getHttpsCallable(links.getTopicBundle)
                .call(data)
                .continueWith { task ->
                    when (task.exception) {
                        is SocketTimeoutException -> {
                            Log.e(javaClass.name, "fetchBundle() - Timeout!")
                            showError(ERROR_TIMEOUT)
                        }
                        is FirebaseFunctionsException -> {
                            Log.e(javaClass.name, "FirebaseException [${(task.exception as FirebaseFunctionsException).code}], ${(task.exception as FirebaseFunctionsException).message}")
                            showError(ERROR_SERVER)
                        }
                        is Exception -> {
                            Log.e(javaClass.name, "UnknownError ${task.exception.toString()}")
                            showError(ERROR_UNKNOWN)
                        }
                    }

                    val result = task.result?.data as HashMap<String, *>
                    result
                }
    }

    private fun sendAnswersBundle(data: HashMap<String, Any>): Task<Double?> {
        data["topicPath"] = topicPath
        return firebaseFunctions
                .getHttpsCallable(links.sendAnswersBundle)
                .call(data)
                .continueWith { task ->
                    when (task.exception) {
                        is SocketTimeoutException -> {
                            Log.e(javaClass.name, "sendAnswersBundle() - Timeout!")
//                            showError(ERROR_TIMEOUT)
                        }
                        is FirebaseFunctionsException -> {
                            Log.e(javaClass.name, "FirebaseException [${(task.exception as FirebaseFunctionsException).code}], ${(task.exception as FirebaseFunctionsException).message}")
//                            showError(ERROR_SERVER)
                        }
                        is Exception -> {
                            Log.e(javaClass.name, "UnknownError ${task.exception.toString()}")
//                            showError(ERROR_UNKNOWN)
                        }
                    }
                    when(task.result?.data){
                        is Int -> ((task.result?.data) as Int).toDouble()
                        is Double -> task.result?.data as Double
                        else -> null
                    }
                }
    }

    private fun switchTask() {
        val fragment = bundleController?.getNextTask()
        if (fragment == null)
            return

        timerView.Show()
        floatingMenu.Show()
        progressBarView.show()
        supportFragmentManager.commit {
            replace(R.id.test_host_fragment, fragment)
            timerView.restart()
            buttonNext.Hide()
        }
    }

    override fun onTaskChecked(isAnswerCorrect: Boolean) {
        if (isAnswerCorrect)
            progressBarView.addSegment()

    }

    override fun onBundleComplete() {
        var status = "Success"
        var score = 1.0

        supportFragmentManager.commit {
            replace(R.id.test_host_fragment, Fragment())
            timerView.Hide()
            progressBarView.Hide()
            floatingMenu.Hide()
            buttonNext.Hide()

            loadingAnimation.Show()
        }
        sendAnswersBundle(bundleController!!.getStats()).addOnCompleteListener {
            if (isExam) {
                score = it.result
                        ?: throw Exception("Cant get bundle result")
                status = if (score > 0.75) "Success" else "Failure"
            }

            supportFragmentManager.commit {
                replace(R.id.test_host_fragment, FragmentTestComplete.newInstance(status, topicPath, score)) // or Fail
                timerView.Hide()
                progressBarView.Hide()
                floatingMenu.Hide()
                buttonNext.Hide()
                loadingAnimation.Hide()
            }
        }
    }

    override fun onTaskComplete() {
        buttonNext.Show()
    }

    override fun onTaskCompleteRejected() {
        buttonNext.Hide()
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
        return;
        debugTextView.text = text
        debugTextView.Show()
        debugTextView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null))
        val cachedCalls = ++debugTextViewCalls
        java.util.Timer().schedule(5000) {
            runOnUiThread {
                if (debugTextViewCalls == cachedCalls) {
                    debugTextView.text = ""
                    debugTextView.Hide()
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
        errorTextView.Show()
        buttonNext.text = getString(R.string.messageButtonTryAgain)
        buttonNext.setOnClickListener { loadBundle() }
        buttonNext.Show()
    }
}