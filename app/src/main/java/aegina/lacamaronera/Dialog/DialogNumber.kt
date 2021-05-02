package aegina.lacamaronera.Dialog

import aegina.lacamaronera.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment

class DialogNumber: AppCompatDialogFragment()
{

    lateinit var dialogGroupInt: DialogNumberInt
    lateinit var dialogNumber: Dialog
    var position = 0

    fun crearDialog(activityTmp: Activity, contextTmp: Context) {

        dialogGroupInt = contextTmp as DialogNumberInt

        dialogNumber = Dialog(activityTmp)
        dialogNumber.setTitle(activityTmp.title)
        dialogNumber.setCancelable(false)
        dialogNumber.setContentView(R.layout.dialog_number)
        dialogNumber.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialogNumber.findViewById(R.id.dialogNumberEntrada) as EditText
        val dialogAceptar = dialogNumber.findViewById(R.id.dialogNumberAceptar) as Button
        val dialogCancelar = dialogNumber.findViewById(R.id.dialogNumberCancelar) as Button
        val dialogTitulo = dialogNumber.findViewById(R.id.dialogNumberTitulo) as TextView

        dialogTitulo.text = contextTmp.getString(R.string.dish_add_ingredient_title_assorment)

        dialogAceptar.setOnClickListener {
            if(dialogText.length() > 0 && dialogText.text.toString() != ".")
            {
                dialogGroupInt.number(dialogText.text.toString(), position)
                dialogNumber.dismiss()
            }
        }

        dialogCancelar.setOnClickListener {
            dialogNumber.dismiss()
        }
    }

    fun showDialog(positionTmp: Int)
    {
        position = positionTmp
        dialogNumber.show()
    }

    interface DialogNumberInt {
        fun number(text: String, position: Int)
    }
}