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
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import org.w3c.dom.Text
import java.lang.Double.parseDouble

class DialogEnterNumber: AppCompatDialogFragment()
{

    lateinit var contextTmp : Context
    lateinit var activityTmp: Activity
    lateinit var dialogIngredients: DialogEnterNumberInt
    lateinit var dialogEnterNumberText: TextView

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

        dialogEnterNumberText = dialogEnterNumber.findViewById(R.id.dialogEnterNumberText) as TextView
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
                dialogIngredients.getNumber(parseDouble(dialogEnterNumberText.text.toString()), positionTmp)
                dialogEnterNumber.dismiss()
            }

        }

    }

    fun showDialog(position: Int)
    {
        positionTmp = position
        dialogEnterNumberText.text = ""
        dialogEnterNumber.show()
    }


    interface DialogEnterNumberInt {
        fun getNumber(number: Double, position: Int)
    }
}