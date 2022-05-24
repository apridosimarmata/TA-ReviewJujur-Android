package id.sireto.reviewjujur.rv.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalDecorator(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        when(parent.getChildLayoutPosition(view)){
            0 -> {
                outRect.left = space * 2
                outRect.right = space * 2
            }
            parent.childCount - 1 -> outRect.right = space * 2
            else -> outRect.right = space * 2
        }

    }
}