package aegina.lacamaronera.Dialog

import aegina.lacamaronera.R
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.widget.addTextChangedListener
import org.w3c.dom.Text
import java.lang.Double.parseDouble

class DialogEnterNumber: AppCompatDialogFragment()
{

    lateinit var contextTmp : Context
    lateinit var activityTmp: Activity
    lateinit var dialogIngredients: DialogEnterNumberInt
    lateinit var dialogEnterNumberTotal: TextView
    lateinit var dialogEnterNumberText: TextView
    lateinit var dialogEnterNumberExchange: TextView

    lateinit var dialogEnterNumber : Dialog
    var positionTmp = 0

    fun createDialog(context: Context, activity : Activity)
    {
        contextTmp = context
        activityTmp = activity
        dialogIngredients = contextTmp as DialogEnterNumberInt

        dialogEnterNumber = Dialog(contextTmp)
        dialogEnterNumber.setCancelable(false)
        dialogEnterNumber.setContentView(R.layout.dialog_enter_number)

        dialogEnterNumber.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogEnterNumber.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogEnterNumberTotal = dialogEnterNumber.findViewById(R.id.dialogEnterNumberTotal) as TextView
        dialogEnterNumberText = dialogEnterNumber.findViewById(R.id.dialogEnterNumberText) as TextView
        dialogEnterNumberExchange = dialogEnterNumber.findViewById(R.id.dialogEnterNumberExchange) as TextView
        val dialogEnterNumberOne = dialogEnterNumber.findViewById(R.id.dialogEnterNumberOne) as Button
        val dialogEnterNumberTwo = dialogEnterNumber.findViewById(R.id.dialogEnterNumberTwo) as Button
        val dialogEnterNumberThree = dialogEnterNumber.findViewById(R.id.dialogEnterNumberThree) as Button
        val dialogEnterNumberFour = dialogEnterNumber.findViewById(R.id.dialogEnterNumberFour) as Button
        val dialogEnterNumberFive = dialogEnterNumber.findViewById(R.id.dialogEnterNumberFive) as Button
        val dialogEnterNumberSix = dialogEnterNumber.findViewById(R.id.dialogEnterNumberSix) as Button
        val dialogEnterNumberSeven = dialogEnterNumber.findViewById(R.id.dialogEnterNumberSeven) as Button
        val dialogEnterNumberEight = dialogEnterNumber.findViewById(R.id.dialogEnterNumberEight) as Button
        val dialogEnterNumberNine = dialogEnterNumber.findViewById(R.id.dialogEnterNumberNine) as Button
        val dialogEnterNumberCero = dialogEnterNumber.findViewById(R.id.dialogEnterNumberCero) as Button
        val dialogEnterNumberDot = dialogEnterNumber.findViewById(R.id.dialogEnterNumberDot) as Button
        val dialogEnterNumberDelete = dialogEnterNumber.findViewById(R.id.dialogEnterNumberDelete) as Button
        val dialogEnterNumberCancel = dialogEnterNumber.findViewById(R.id.dialogEnterNumberCancel) as Button
        val dialogEnterNumberEnter = dialogEnterNumber.findViewById(R.id.dialogEnterNumberEnter) as Button

        dialogEnterNumberTotal.isEnabled = false
        dialogEnterNumberTotal.inputType = InputType.TYPE_NULL

        dialogEnterNumberText.isEnabled = false
        dialogEnterNumberText.inputType = InputType.TYPE_NULL

        dialogEnterNumberExchange.isEnabled = false
        dialogEnterNumberExchange.inputType = InputType.TYPE_NULL

        dialogEnterNumberOne.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "1"}
        dialogEnterNumberTwo.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "2"}
        dialogEnterNumberThree.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "3"}
        dialogEnterNumberFour.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "4"}
        dialogEnterNumberFive.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "5"}
        dialogEnterNumberSix.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "6"}
        dialogEnterNumberSeven.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "7"}
        dialogEnterNumberEight.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "8"}
        dialogEnterNumberNine.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "9"}
        dialogEnterNumberCero.setOnClickListener{dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "0"}

        dialogEnterNumberText.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable)
            {
                if(s.toString() == ".")
                {
                    dialogEnterNumberExchange.text = dialogEnterNumberTotal.text.toString()
                }
                else
                {
                    val pago =
                        if(dialogEnterNumberText.text.length > 0)
                        {
                            parseDouble(dialogEnterNumberText.text.toString())
                        }
                        else
                        {
                            0.0
                        }


                    dialogEnterNumberExchange.text = ((parseDouble(dialogEnterNumberTotal.text.toString()) - pago) * -1).toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {}
        })

        dialogEnterNumberDot.setOnClickListener()
        {
            if(!dialogEnterNumberText.text.contains("."))
            {
                dialogEnterNumberText.text = dialogEnterNumberText.text.toString() + "."
            }
        }

        dialogEnterNumberDelete.setOnClickListener()
        {
            if(dialogEnterNumberText.text.isNotEmpty())
            {
                dialogEnterNumberText.text = dialogEnterNumberText.text.toString().subSequence(0, dialogEnterNumberText.text.length - 1)
            }
        }

        dialogEnterNumberCancel.setOnClickListener()
        {
            dialogEnterNumber.dismiss()
        }

        dialogEnterNumberEnter.setOnClickListener()
        {
            if(dialogEnterNumberText.text.isNotEmpty() && dialogEnterNumberText.text.toString() != ".")
            {
                dialogIngredients.finishSale()
                dialogEnterNumber.dismiss()
            }

        }

    }

    fun showDialog(total: String)
    {
        dialogEnterNumberTotal.text = total
        dialogEnterNumberText.text = ""
        //dialogEnterNumberExchange = ""
        dialogEnterNumber.show()
    }


    interface DialogEnterNumberInt {
        fun finishSale()
    }
}