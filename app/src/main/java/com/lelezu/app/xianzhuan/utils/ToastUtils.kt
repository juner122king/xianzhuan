import android.content.Context
import android.widget.Toast

object ToastUtils {
    private var toast: Toast? = null

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        toast?.cancel() // 取消上一个Toast
        toast = Toast.makeText(context, message, duration)
        toast?.show()
    }
}
