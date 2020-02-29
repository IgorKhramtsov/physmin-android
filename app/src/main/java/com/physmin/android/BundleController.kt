package com.physmin.android

import com.physmin.android.fragments.tasks.FragmentTaskBase
import com.physmin.android.fragments.tasks.TaskController
import com.physmin.android.views.TimerView

class BundleController(levelBundle: HashMap<String, *>, isExam: Boolean, timerView: TimerView) {
    private val bundleID = levelBundle["bundleId"]!!
    private val isExam = isExam
    private val timerView = timerView
    private val tasksList = TasksList(levelBundle["tasks"] as ArrayList<TaskObject>)


    private var taskController: TaskController? = null
    private var listener: BundleControllerListener? = null
    private val userAnswers = HashMap<String, HashMap<String, out Any>>() // id: {type, tries}

    fun getTasksCount() = tasksList.count
    fun setListener(receiver: BundleControllerListener) {
        listener = receiver
    }

    private fun checkCurrentTask() {
        if (taskController == null)
            return

        // this will create stat if task appear first time, or use old val if task was previously pushed to end of taskList
        taskController?.let {
            userAnswers[it.taskId.toString()] = userAnswers[it.taskId.toString()]
                    ?: (hashMapOf("taskType" to it.taskType, "tries" to 1, "times" to ArrayList<Int>()))
        }

        val isAnswerCorrect = (taskController!!.isTaskCompleted && taskController!!.isAnswersCorrect())
                || isDev()

        if (!isAnswerCorrect) {
            tasksList.pushCurrentToBack()
            (userAnswers[taskController!!.taskId.toString()]!!["tries"] as Int).inc()
        }
        (userAnswers[taskController!!.taskId.toString()]!!["times"] as ArrayList<Int>).add(60 - timerView.getTime())
        listener?.onTaskChecked(isAnswerCorrect || isExam)
    }

    fun getNextTask(taskObj: TaskObject? = null): FragmentTaskBase? {
        checkCurrentTask()

        if (tasksList.isEnd()) {
            listener?.onBundleComplete()
            return null
        } else {
            val fragment = parseTask(taskObj
                    ?: tasksList.pop())
            taskController = fragment

            return fragment
        }
    }

    fun getStats(): HashMap<String, Any> {
        return hashMapOf(
                "bundleId" to bundleID,
                "isExam" to isExam,
                "answers" to userAnswers
        )
    }

    interface BundleControllerListener {

        /**
         * Return true if task correct OR if bundle is Exam
         **/
        fun onTaskChecked(isAnswerCorrect: Boolean)

        fun onBundleComplete()
    }
}