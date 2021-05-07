package aegina.lacamaronera.Dialog

import aegina.lacamaronera.R
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatDialogFragment
import java.util.*

class DialogSearch: AppCompatDialogFragment()
{

    lateinit var dialogSearch: Dialog
    lateinit var initialDate : Date
    lateinit var finalDate : Date

    lateinit var searchInt: DialogSearchInt

    lateinit var dialogSearchDateInitial: Button
    lateinit var dialogSearchDateFinal: Button

    fun createWindow(activityTmp: Activity, contextTmp: Context)
    {
        dialogSearch = Dialog(activityTmp)
        dialogSearch.setTitle(activityTmp.title)
        dialogSearch.setCancelable(false)
        dialogSearch.setContentView(R.layout.dialog_search)


        dialogSearch.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogSearch.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogSearchDateInitial =  dialogSearch.findViewById(R.id.dialogSearchDateInitial) as Button
        dialogSearchDateFinal = dialogSearch.findViewById(R.id.dialogSearchDateFinal) as Button
        val dialogSearchCancel = dialogSearch.findViewById(R.id.dialogSearchCancel) as Button
        val dialogSearchAccept = dialogSearch.findViewById(R.id.dialogSearchAccept) as Button

        dialogSearchDateInitial.setOnClickListener()
        {
            searchInt.updateInitialDate()
        }

        dialogSearchDateFinal.setOnClickListener()
        {
            searchInt.updateFinalDate()
        }

        dialogSearchCancel.setOnClickListener()
        {
            dialogSearch.dismiss()
        }

        dialogSearchAccept.setOnClickListener()
        {
            searchInt.search()
            dialogSearch.dismiss()
        }


        searchInt = contextTmp as DialogSearchInt
        assignDate()
    }

    fun showDialog()
    {
        dialogSearch.show()
    }

    fun assignDate(){
        var calendar = Calendar.getInstance()
        calendar = assignHourCalendar(calendar, 23, 59, 59)
        finalDate = calendar.time

        calendar = assignHourCalendar(calendar, 0, 0, 0)
        initialDate = calendar.time
    }

    private fun assignHourCalendar(calendar : Calendar, hour : Int, minute : Int, second : Int) : Calendar{
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)

        return calendar
    }

    fun assignInitialText(text: String)
    {
        dialogSearchDateInitial.text = text
    }

    fun assignFinalText(text: String)
    {
        dialogSearchDateFinal.text = text
    }

    interface DialogSearchInt
    {
        fun updateInitialDate()
        fun updateFinalDate()
        fun search()
    }

}