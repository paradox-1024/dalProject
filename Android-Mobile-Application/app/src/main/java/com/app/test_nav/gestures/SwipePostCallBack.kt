package com.app.test_nav.gestures

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.test_nav.R

abstract class SwipeNoteCallBack(private val context: Context, private val recyclerView: RecyclerView) : ItemTouchHelper.Callback() {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_sweep_24)
    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit_24)
    private val intrinsicWidthDeleteIcon = deleteIcon!!.intrinsicWidth
    private val intrinsicHeightDeleteIcon = deleteIcon!!.intrinsicHeight
    private val intrinsicWidthEditIcon = editIcon!!.intrinsicWidth
    private val intrinsicHeightEditIcon = editIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColorDelete =Color.parseColor("#FF2D55")
    private val backgroundColorComplete = Color.parseColor("#34C759")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, swipeFlag)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        when {
            isCanceled -> {
                clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }

            /*
            Swapped the icons of delete and edit
            * */
            dX < 0 -> {

                background.color = backgroundColorDelete
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeightDeleteIcon) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeightDeleteIcon) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidthDeleteIcon
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeightDeleteIcon

                deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon.draw(c)

            }
            else -> {
                background.color = backgroundColorComplete
                background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
                background.draw(c)

                val editIconTop = itemView.top + (itemHeight - intrinsicHeightEditIcon) / 2
                val editIconMargin = (itemHeight - intrinsicHeightEditIcon) / 2
                val editIconRight = editIconMargin + intrinsicWidthEditIcon
                val editIconBottom = editIconTop + intrinsicHeightEditIcon

                editIcon!!.setBounds(editIconMargin, editIconTop, editIconRight, editIconBottom)
                editIcon.draw(c)
            }
        }
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

}