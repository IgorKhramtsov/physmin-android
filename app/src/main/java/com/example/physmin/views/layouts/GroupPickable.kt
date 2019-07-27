package com.example.physmin.views.layouts

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.activities.FunctionAnswerParcelable
import com.example.physmin.activities.TextAnswerParcelable
import com.example.physmin.views.*
import com.example.physmin.views.items.ImageViewPickable
import com.example.physmin.views.items.TextViewPickable
import kotlinx.android.synthetic.main.fragment_test_state2graph.view.*

class GroupPickable(context: Context, attrs: AttributeSet?): GroupScrollable(context, attrs),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    lateinit var parentTestConstraintLayout: TestConstraintLayout
    var pickedItem: Pickable? = null
        private set(value) {
            field?.deselect() // Deselect previous
            field = value
            field?.select() // Select new
        }

    init {
        layoutType = TWO_COLUMNS
        setOnHierarchyChangeListener(this)
    }

    fun addImageViewPickable(answerParcelable: FunctionAnswerParcelable, isCorr: Boolean? = null) {
        val answerPic = ImageViewPickable(context, null).apply {
            layoutParams = LayoutParams(150.dpToPx().toInt(), 90.dpToPx().toInt())
            graph.functions = answerParcelable.functions
            answer = answerParcelable.id
            isCorrect = isCorr.toString()
        }

        this.addView(answerPic)
    }

    fun addTextViewPickable(answerParcelable: TextAnswerParcelable, correctIds: IntArray) {
        val textView = TextViewPickable(this.context!!, null).apply {
            setPadding(6.dpToPx().toInt(), 3.dpToPx().toInt(), 6.dpToPx().toInt(), 3.dpToPx().toInt())
            answer = answerParcelable.id
            layoutParams = LayoutParams(150.dpToPx().toInt(), LayoutParams.WRAP_CONTENT)
            text = answerParcelable.text
            isCorrect = correctIds.joinToString()
        }
        this.addView(textView)
    }

    fun setParent(parent: TestConstraintLayout) {
        this.parentTestConstraintLayout = parent
    }

    fun resetPickedItem() {
        this.pickedItem = null
    }

    private fun pickItem(item: Pickable?) {
        pickedItem = if (pickedItem == item) null else item
    }

    override fun onClick(p0: View?) {
        if (p0 is Pickable) pickItem(p0)
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        child.setOnClickListener(this)
        (child as? TextViewPickable)?.setParent(this)
        (child as? ImageViewPickable)?.setParent(this)

    }

    override fun onChildViewRemoved(parent: View?, child: View?) {

    }
}
