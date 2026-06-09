import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomGridItemDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        when (position) {
            0 -> { // First item
                outRect.right = spacing
            }
            1 -> { // Second item
                outRect.right = spacing
            }
            3 ->{
                outRect.right = spacing
            }
            4->{
                outRect.right = spacing
            }
        }

        // Common bottom margin
       // outRect.bottom = spacing
    }
}
