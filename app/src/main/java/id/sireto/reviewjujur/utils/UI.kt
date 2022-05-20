package id.sireto.reviewjujur.utils

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar

object UI {
    fun snackbarTop(view: View, msg : String){
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity =  Gravity.TOP
        view.layoutParams = params
        snackbar.show()
    }

    fun snackbar(view: View, msg : String){
        Snackbar.make(
            view, msg, Snackbar.LENGTH_SHORT
        ).show()
    }
}