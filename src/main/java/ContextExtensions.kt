import android.content.Context
import android.content.res.Resources

// Context-aware (Safest for multi-window/varying densities)
fun Context.dpToPx(dp: Int): Int =
    (dp * resources.displayMetrics.density).toInt()

// Global System Resource
val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density
