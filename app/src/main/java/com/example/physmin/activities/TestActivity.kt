package com.example.physmin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.commit
import com.example.physmin.BuildConfig
import com.example.physmin.R
import com.example.physmin.dev.fragments.FragmentTestList
import com.example.physmin.fragments.tests.*
import com.example.physmin.views.LoadingHorBar
import com.example.physmin.views.layouts.TestConstraintLayout
import com.example.physmin.views.ProgressBarView
import com.example.physmin.views.TimerView
import com.getbase.floatingactionbutton.FloatingActionButton
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

class TestActivity: AppCompatActivity(), FragmentTestBase.OnFragmentTestBaseListener
{
    private lateinit var firebaseFunctions: FirebaseFunctions
    var testBundle = arrayListOf<JSONObject>()

    var getTestFunctionName = "getTest"
    var testConstraintLayout: TestConstraintLayout? = null
    lateinit var progressBarView: ProgressBarView
    private lateinit var debugTextView: TextView
    private lateinit var errorTextView: TextView
    lateinit var timerView: TimerView
    lateinit var buttonNext: Button
    lateinit var loadingAnimation: LoadingHorBar
    lateinit var floatingMenu: FloatingActionsMenu

    private var nextTestIndex = 0
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
        if(BuildConfig.FLAVOR.contains("dev")) {
            getTestFunctionName = "getTestDev"
            floatingMenu.action_next.setOnClickListener { switchTest() }
            floatingMenu.action_list.setOnClickListener {
                supportFragmentManager.commit {
                    addToBackStack(null)
                    replace(R.id.test_host_fragment, FragmentTestList())
                    floatingMenu.collapse()
                }
            }
        } else {
            floatingMenu.removeButton(this.action_list)
            floatingMenu.removeButton(this.action_next)
        }

        loadTest()

        // Greeting
//        supportFragmentManager.transaction {
//            replace(R.id.test_host_fragment, FragmentTestHello.newInstance())
//        }
    }

    private fun loadTest() {
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
        for (i in 0 until data!!.length())
            testBundle.add(data.getJSONObject(i))

        progressBar.segmentCount = testBundle.count()

        buttonNext.setOnClickListener { switchTest() }
    }

    fun switchTest(ind: Int? = null, suppressCallback: Boolean = false) {
        val testIndex = ind ?: nextTestIndex++

        if (testIndex >= testBundle.size)
            onTestComplete()

        timerView.visibility = VISIBLE
        floatingMenu.visibility = VISIBLE
        progressBarView.show()
        supportFragmentManager.commit {
            if (!suppressCallback)
                onTestSwitch()

            replace(R.id.test_host_fragment, parseTest(testBundle[testIndex]))
            timerView.restart()
            hideButtonNext()
        }
    }

    private fun onTestSwitch() {
        testConstraintLayout?.let {

            if (it.isAnswersCorrect()) {
                progressBarView.addSegment()

                if (progressBarView.isAllDone()) {
                    val intent = Intent(this, TestActivity::class.java)
                    intent.putExtra("GetTestFunctionName", "getTestDev")
                    startActivity(intent)
                } else Unit

            } else {
                testBundle.add(testBundle[nextTestIndex])

                // TODO: create custom arrayList
//            testBundle.removeAt(nextTestIndex)
//            nextTestIndex--
            }

        }
    }

    fun onTestComplete() {
        finish()
    }

    fun showDebugMessage(text: String) {
        debugTextView.text = text
        debugTextView.visibility = VISIBLE
        debugTextView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null))
        val cachedCalls = ++debugTextViewCalls
        java.util.Timer().schedule(5000) {
            runOnUiThread {
                if(debugTextViewCalls == cachedCalls) {
                    debugTextView.text = ""
                    debugTextView.visibility = GONE
                    debugTextView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))
                }
            }
        }
    }

    private fun showError(errorCode: Int) {
        errorTextView.text = when(errorCode) {
            ERROR_TIMEOUT -> getString(R.string.messageTimeoutError)
            ERROR_SERVER -> getString(R.string.messageServerError)
            else -> getString(R.string.messageUnknownError)
        }
        errorTextView.visibility = VISIBLE
        buttonNext.text = getString(R.string.messageButtonTryAgain)
        buttonNext.setOnClickListener { loadTest() }
        showButtonNext()
    }

    fun showButtonNext() {
        buttonNext.visibility = VISIBLE
    }

    fun hideButtonNext() {
        buttonNext.visibility = GONE
    }

    override fun asd() {

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
            funcType = readString()?:throw Exception("Parsing funcType is null")
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
        if(jsonObject.has("id"))
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
            letter = readString()?:throw Exception("Parsing funcType is null")
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
            text = readString()?:throw Exception("Parsing funcType is null")
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