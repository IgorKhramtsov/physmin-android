package com.physmin.android.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.physmin.android.BuildConfig
import com.physmin.android.R
import com.physmin.android.TestBundle
import com.physmin.android.dev.fragments.FragmentTestList
import com.physmin.android.fragments.FragmentTestComplete
import com.physmin.android.fragments.tests.*
import com.physmin.android.isDev
import com.physmin.android.views.LoadingHorBar
import com.physmin.android.views.ProgressBarView
import com.physmin.android.views.TimerView
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.activity_test.view.*
import org.json.JSONObject
import java.lang.Exception
import java.net.SocketTimeoutException
import kotlin.concurrent.schedule

const val ERROR_UNKNOWN = 0
const val ERROR_TIMEOUT = 1
const val ERROR_SERVER = 2

class TestActivity: AppCompatActivity(), FragmentTestBase.TestCompletingListener {
    private lateinit var firebaseFunctions: FirebaseFunctions
    lateinit var testBundle: TestBundle

    var getTestFunctionName = "getTest"
    var testController: TestController? = null
    lateinit var progressBarView: ProgressBarView
    private lateinit var debugTextView: TextView
    private lateinit var errorTextView: TextView
    lateinit var timerView: TimerView
    lateinit var buttonNext: Button
    lateinit var loadingAnimation: LoadingHorBar
    lateinit var floatingMenu: FloatingActionsMenu

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
            getTestFunctionName = "getTestDev"
            floatingMenu.action_next.setOnClickListener { switchTest() }
            floatingMenu.action_list.setOnClickListener {
                supportFragmentManager.commit {
                    replace(R.id.test_host_fragment, FragmentTestList(testBundle.getAsArray()))
                    addToBackStack("home")
                    floatingMenu.collapse()
                }
            }
        } else {
            floatingMenu.removeButton(this.action_list)
            floatingMenu.removeButton(this.action_next)
        }

        loadTestBundle()

        // Greeting
//        supportFragmentManager.transaction {
//            replace(R.id.test_host_fragment, FragmentTestHello.newInstance())
//        }
    }

    private fun loadTestBundle() {
        errorTextView.visibility = GONE
        progressBarView.hide()
        hideButtonNext()
        loadingAnimation.show()

        fetchTestBundle().addOnCompleteListener {
            loadingAnimation.hide()
            if (it.isSuccessful)
                processTestBundle(it.result!!)
        }
    }

    private fun fetchTestBundle(): Task<String> {
        return firebaseFunctions
                .getHttpsCallable(getTestFunctionName)
                .call()
                .continueWith { task ->
                    when {
                        task.exception is SocketTimeoutException -> {
                            Log.e("TestActivity", "fetchTestBundle() - Timeout!")
                            showError(ERROR_TIMEOUT)
                        }
                        task.exception is FirebaseFunctionsException -> {
                            Log.e("TestActivity", "FirebaseException [${(task.exception as FirebaseFunctionsException).code}], ${(task.exception as FirebaseFunctionsException).message}")
                            showError(ERROR_SERVER)
                        }
                        task.exception is Exception -> {
                            Log.e("TestActivity", "UnknownError ${task.exception.toString()}")
                            showError(ERROR_UNKNOWN)
                        }
                    }

                    val result = task.result?.data as String
                    result
                }
    }

    private fun processTestBundle(test: String) {
        showButtonNext()
        buttonNext.text = getString(R.string.messageButtonNext)

        val data = JSONObject(test.substring(test.indexOf("{"), test.lastIndexOf("}") + 1)).optJSONArray("tests")
        val array = arrayListOf<JSONObject>()
        for (i in 0 until data!!.length())
            array.add(data.getJSONObject(i))
        testBundle = TestBundle(array)

        progressBar.segmentCount = array.count()

        buttonNext.setOnClickListener { switchTest() }
    }

    fun switchTest(test: JSONObject? = null, suppressCallback: Boolean = false) {
        timerView.visibility = VISIBLE
        floatingMenu.visibility = VISIBLE
        progressBarView.show()
        if (!suppressCallback)
            onTestSwitch()
        if(testBundle.isEnd())
            return

        supportFragmentManager.commit {
            replace(R.id.test_host_fragment, parseTest(test ?: testBundle.pop()))
            timerView.restart()
            hideButtonNext()
        }
    }

    private fun onTestSwitch() {
        testController?.also {
            if (it.isAnswersCorrect() || isDev()) {
                progressBarView.addSegment()
                if(progressBarView.isAllDone())
                    onBundleComplete()
            } else {
                testBundle.pushCurrentToBack()
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
        buttonNext.setOnClickListener { loadTestBundle() }
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


    private fun parseTest(test: JSONObject): androidx.fragment.app.Fragment {
        return when (test.getString("type")) {
            "relationSings" -> parseRS(test)
            "graph2graph2" -> parseG2G2(test)
            "graph2state" -> parseS2G(test)
            "graph2graph" -> parseG2G(test)
            // TODO: Log error
            else -> parseG2G2(test)
        }
    }

    private fun parseRS(test: JSONObject): androidx.fragment.app.Fragment {
        val questionJson = test.getJSONArray("question")
        val answersJson = test.getJSONArray("answers")

        var question = ArrayList<QuestionParcelable>()
        var answers = ArrayList<FunctionAnswerRelationSignParcelable>()

        for (i in 0 until questionJson.length())
            question.add(QuestionParcelable(questionJson.getJSONObject(i)))
        for (i in 0 until answersJson.length())
            answers.add(FunctionAnswerRelationSignParcelable(answersJson.getJSONObject(i)))

        return FragmentTestSign2Relation.newInstance(question, answers)
    }

    private fun parseG2G2(test: JSONObject): androidx.fragment.app.Fragment {

        val questionJson = test.getJSONObject("question")
        val answersJson = test.getJSONArray("answers")

        val questions = ArrayList<QuestionParcelable>()
        val answers = ArrayList<FunctionAnswerParcelable>()

        questions.add(QuestionParcelable(questionJson))
        for (i in 0 until answersJson.length()) {
            answers.add(FunctionAnswerParcelable(answersJson.getJSONObject(i)))
        }

        return FragmentTestGraph2Graph2.newInstance(questions, answers)
    }

    private fun parseG2G(test: JSONObject): androidx.fragment.app.Fragment {

        val questionJson = test.getJSONObject("question")
        val answersJson = test.getJSONArray("answers")

        val questions = ArrayList<QuestionParcelable>()
        val answers = ArrayList<FunctionAnswerParcelable>()

        questions.add(QuestionParcelable(questionJson))
        for (i in 0 until answersJson.length()) {
            answers.add(FunctionAnswerParcelable(answersJson.getJSONObject(i)))
        }

        return FragmentTestGraph2Graph.newInstance(questions, answers)
    }

    private fun parseS2G(test: JSONObject): androidx.fragment.app.Fragment {
        val questionJson = test.getJSONArray("question")
        val answersJson = test.getJSONArray("answers")

        var question = ArrayList<QuestionParcelable>()
        var answers = ArrayList<TextAnswerParcelable>()

        for (i in 0 until questionJson.length())
            question.add(QuestionParcelable(questionJson.getJSONObject(i)))
        for (i in 0 until answersJson.length())
            answers.add(TextAnswerParcelable(answersJson.getJSONObject(i)))

        return FragmentTestGraph2State.newInstance(question, answers)
    }
}

class FunctionParcelable(): Parcelable {
    var x: Float = 0f
    var v: Float = 0f
    var a: Float = 0f
    var len: Int = 0
    var funcType: String = ""

    constructor(jsonObject: JSONObject): this() {
        jsonObject.getJSONObject("params").let {
            if (it.has("x"))
                this.x = it.getDouble("x").toFloat()
            if (it.has("v"))
                this.v = it.getDouble("v").toFloat()
            this.a = it.getDouble("a").toFloat()

            if (it.has("len"))
                this.len = it.getInt("len")
        }
        this.funcType = jsonObject.getString("funcType")
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            x = readFloat()
            v = readFloat()
            a = readFloat()
            len = readInt()
            funcType = readString() ?: throw Exception("Parsing funcType is null")
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeFloat(x)
            writeFloat(v)
            writeFloat(a)
            writeInt(len)
            writeString(funcType)
        }
    }

    companion object CREATOR: Parcelable.Creator<FunctionParcelable> {
        override fun createFromParcel(source: Parcel?): FunctionParcelable {
            return FunctionParcelable(source)
        }

        override fun newArray(size: Int): Array<FunctionParcelable?> {
            return arrayOfNulls(size)
        }
    }

}

class QuestionParcelable(): Parcelable {
    var id: Int = 0
    var correctIDs = ArrayList<Int>()
    var functions = ArrayList<FunctionParcelable>()

    constructor(jsonObject: JSONObject): this() {
        if (jsonObject.has("id"))
            id = jsonObject.getInt("id")
        if (jsonObject.has("correctIDs"))
            jsonObject.getJSONArray("correctIDs").let {
                for (i in 0 until it.length())
                    correctIDs.add(it.getInt(i))
            }
        jsonObject.getJSONArray("graph").let {
            for (i in 0 until it.length())
                functions.add(FunctionParcelable(it.optJSONObject(i)))
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = this.readInt()
            val intArray = createIntArray()!!
            correctIDs = intArray.toCollection(ArrayList())
            functions = createTypedArrayList(FunctionParcelable.CREATOR)!!
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeIntArray(correctIDs.toIntArray())
            writeTypedList(functions)
        }
    }

    companion object CREATOR: Parcelable.Creator<QuestionParcelable> {
        override fun createFromParcel(source: Parcel?): QuestionParcelable {
            return QuestionParcelable(source)
        }

        override fun newArray(size: Int): Array<QuestionParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

class FunctionAnswerParcelable(): Parcelable {
    var id = 0
    var functions = ArrayList<FunctionParcelable>()

    constructor(jsonObject: JSONObject): this() {
        id = jsonObject.getInt("id")
        jsonObject.getJSONArray("graph").let {
            for (i in 0 until it.length())
                functions.add(FunctionParcelable(it.optJSONObject(i)))
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            functions = createTypedArrayList(FunctionParcelable.CREATOR)!!
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeTypedList(functions)
        }
    }

    companion object CREATOR: Parcelable.Creator<QuestionParcelable> {
        override fun createFromParcel(source: Parcel?): QuestionParcelable {
            return QuestionParcelable(source)
        }

        override fun newArray(size: Int): Array<QuestionParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

class FunctionAnswerRelationSignParcelable(): Parcelable {
    var id = 0
    var letter = ""
    var leftIndex = IntArray(2)
    var rightIndex = IntArray(2)
    var correctSign = 0

    constructor(jsonObject: JSONObject): this() {
        jsonObject.apply {
            id = getInt("id")
            letter = getString("letter")
            val leftIndArr = getJSONArray("leftIndexes")
            leftIndex = arrayOf(leftIndArr.getInt(0), leftIndArr.getInt(1)).toIntArray()
            val rightIndArr = getJSONArray("rightIndexes")
            rightIndex = arrayOf(rightIndArr.getInt(0), rightIndArr.getInt(1)).toIntArray()
            correctSign = getInt("correctSign")
        }
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            letter = readString() ?: throw Exception("Parsing funcType is null")
            val intArrayLeft = createIntArray()!!
            leftIndex = intArrayLeft.toCollection(ArrayList()).toIntArray()
            val intArrayRight = createIntArray()!!
            rightIndex = intArrayRight.toCollection(ArrayList()).toIntArray()
            correctSign = readInt()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeString(letter)
            writeIntArray(leftIndex)
            writeIntArray(rightIndex)
            writeInt(correctSign)
        }
    }

    companion object CREATOR: Parcelable.Creator<QuestionParcelable> {
        override fun createFromParcel(source: Parcel?): QuestionParcelable {
            return QuestionParcelable(source)
        }

        override fun newArray(size: Int): Array<QuestionParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

class TextAnswerParcelable(): Parcelable {
    var id = 0
    var text: String = ""

    constructor(jsonObject: JSONObject): this() {
        id = jsonObject.getInt("id")
        text = jsonObject.getString("text")
    }

    constructor(parcel: Parcel?): this() {
        parcel?.apply {
            id = readInt()
            text = readString() ?: throw Exception("Parsing funcType is null")
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(id)
            writeString(text)
        }
    }

    companion object CREATOR: Parcelable.Creator<QuestionParcelable> {
        override fun createFromParcel(source: Parcel?): QuestionParcelable {
            return QuestionParcelable(source)
        }

        override fun newArray(size: Int): Array<QuestionParcelable?> {
            return arrayOfNulls(size)
        }
    }
}