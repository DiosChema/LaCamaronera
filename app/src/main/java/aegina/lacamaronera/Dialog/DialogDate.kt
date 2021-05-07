package aegina.lacamaronera.Dialog

import aegina.lacamaronera.General.Moths
import aegina.lacamaronera.R
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.*

class DialogDate : AppCompatDialogFragment()
{
    val formatoFecha = SimpleDateFormat("MM-dd-yyyy")
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaTmp : Date
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date

    lateinit var dialogSelectDateTittle : TextView
    lateinit var dialogSelectDateToday : Button
    lateinit var dialogSelectDateWeek : Button
    lateinit var dialogSelectDateMoth : Button
    lateinit var dialogSelectDatePicker : DatePicker
    lateinit var dialogSelectDateCancel : Button
    lateinit var dialogSelectDateAccept : Button
    val mothName = Moths()

    lateinit var dateInt: DialogDateInt
    lateinit var dialogFecha : Dialog

    var typeInitialDate = false
    var dialogCreada = false

    lateinit var contextTmp: Context

    lateinit var contextDialog : Context

    fun createWindow(context: FragmentActivity)
    {
        contextTmp = context
        dialogCreada = true
        dialogFecha = Dialog(context)

        contextDialog = context

        dialogFecha.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFecha.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogFecha.setCancelable(false)
        dialogFecha.setContentView(R.layout.dialog_select_date)

        dateInt = context as DialogDateInt
        assignRessources()
    }

    fun createWindow(context: Context)
    {

        dialogCreada = true
        dialogFecha = Dialog(context)

        contextDialog = context

        dialogFecha.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFecha.setCancelable(false)
        dialogFecha.setContentView(R.layout.dialog_select_date)

        dateInt = context as DialogDateInt
        assignRessources()
    }

    fun assignInitialsDates()
    {
        asignarFechaSemana()
    }

    fun abrirDialogFechaInicial(fechaInicialTmp : Date, fechaFinalTmp : Date)
    {
        dialogSelectDateTittle.text = contextDialog.getString(R.string.dialogSelectDateInitial)

        typeInitialDate = true
        assignInitialDate(fechaInicialTmp, fechaFinalTmp)
        dialogFecha.show()
    }

    fun abrirDialogFechaFinal(fechaInicialTmp : Date, fechaFinalTmp : Date)
    {
        dialogSelectDateTittle.text = contextDialog.getString(R.string.dialogSelectDateFinal)

        typeInitialDate = false
        assignFinalDate(fechaInicialTmp, fechaFinalTmp)
        dialogFecha.show()
    }

    fun assignRessources()
    {

        dialogSelectDateTittle = dialogFecha.findViewById(R.id.dialogSelectDateTittle)
        dialogSelectDateToday = dialogFecha.findViewById(R.id.dialogSelectDateToday)
        dialogSelectDateWeek = dialogFecha.findViewById(R.id.dialogSelectDateWeek)
        dialogSelectDateMoth = dialogFecha.findViewById(R.id.dialogSelectDateMoth)
        dialogSelectDatePicker = dialogFecha.findViewById(R.id.dialogSelectDatePicker)
        dialogSelectDateCancel = dialogFecha.findViewById(R.id.dialogSelectDateCancel)
        dialogSelectDateAccept = dialogFecha.findViewById(R.id.dialogSelectDateAccept)

        dialogSelectDateToday.setOnClickListener {asignarFechaHoy()}
        dialogSelectDateWeek.setOnClickListener {asignarFechaSemana()}
        dialogSelectDateMoth.setOnClickListener {asignarFechaMes()}

        dialogSelectDateCancel.setOnClickListener {dialogFecha.dismiss()}
        dialogSelectDateAccept.setOnClickListener {assignDate()}

    }

    fun assignDate()
    {
        if(typeInitialDate) {
            assignInitialDate()

            dateInt.getInitialDate()
        }
        else {
            assignFinalDate()
            dateInt.getFinalDate()
        }

        dialogFecha.dismiss()
    }

    fun assignInitialDate()
    {
        val day: Int = dialogSelectDatePicker.dayOfMonth
        val month: Int = dialogSelectDatePicker.month
        val year: Int = dialogSelectDatePicker.year

        var calendarTmp = Calendar.getInstance()
        calendarTmp.set(year, month, day)
        calendarTmp = asignarHoraCalendar(calendarTmp, 0, 0, 0)
        calendarTmp.timeZone = Calendar.getInstance().timeZone

        fechaInicial = calendarTmp.time
    }

    fun assignFinalDate() {
        val day: Int = dialogSelectDatePicker.dayOfMonth
        val month: Int = dialogSelectDatePicker.month
        val year: Int = dialogSelectDatePicker.year

        var calendarTmp = Calendar.getInstance()
        calendarTmp.set(year, month, day)
        calendarTmp = asignarHoraCalendar(calendarTmp, 23, 59, 59)
        calendarTmp.timeZone = Calendar.getInstance().timeZone;

        fechaFinal = calendarTmp.time
    }

    fun assignInitialDate(fechaInicialTmp : Date, fechaFinalTmp : Date)
    {

        fechaInicial = fechaInicialTmp
        fechaFinal = fechaFinalTmp

        dialogSelectDatePicker.minDate = formatoFecha.parse("01-01-2021").time
        dialogSelectDatePicker.maxDate = fechaFinal.time

        val calendar = Calendar.getInstance()
        calendar.time = fechaInicial
        dialogSelectDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    fun assignFinalDate(fechaInicialTmp : Date, fechaFinalTmp : Date)
    {
        fechaInicial = fechaInicialTmp
        fechaFinal = fechaFinalTmp

        var currentTime = Calendar.getInstance()
        currentTime = asignarHoraCalendar(currentTime, 0, 0, 0)

        dialogSelectDatePicker.minDate = fechaInicial.time
        dialogSelectDatePicker.maxDate = currentTime.time.time

        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal
        dialogSelectDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    fun asignarFechaHoy()
    {
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        dateInt.getDate()
        dialogFecha.dismiss()
    }

    fun asignarFechaSemana()
    {
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, -6)

        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        dateInt.getDate()
        dialogFecha.dismiss()
    }

    fun asignarFechaMes()
    {
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaFinal = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaInicial = calendar.time

        dateInt.getDate()
        dialogFecha.dismiss()
    }



    fun asignarHoraCalendar(calendar : Calendar, hora : Int, minuto : Int, segundo : Int) : Calendar
    {
        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, segundo)

        return calendar
    }

    fun getInitialDateText(): String{
        val calendar = Calendar.getInstance()
        calendar.time = fechaInicial

        val text = if(Integer.parseInt(contextTmp.getString(R.string.languaje)) > 1)
        {
            "" + mothName.getMoths((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + mothName.getMoths((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.YEAR)
        }

        return text
    }

    fun getFinalDateText(): String{
        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal

        val text = if(Integer.parseInt(contextTmp.getString(R.string.languaje)) > 1)
        {
            "" + mothName.getMoths((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + mothName.getMoths((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.YEAR)
        }

        return text
    }

    interface DialogDateInt
    {
        fun getInitialDate()
        fun getFinalDate()
        fun getDate()
    }

}