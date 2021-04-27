package aegina.lacamaronera.Dialog

import aegina.lacamaronera.R
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment

class DialogSelectPhoto : AppCompatDialogFragment()
{
    lateinit var dialogFoto: Dialog
    lateinit var dialogFotoGaleria: ImageView
    lateinit var dialogFotoCamara: ImageView
    lateinit var dialogFotoCancelar: Button
    lateinit var interfazFoto : DialogSelectPhotoInt

    fun createDialog(context : Context)
    {
        dialogFoto = Dialog(context)

        dialogFoto.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFoto.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogFoto.setCancelable(false)
        dialogFoto.setContentView(R.layout.dialog_photo)

        dialogFotoGaleria = dialogFoto.findViewById(R.id.dialogPhotoGaleria) as ImageButton
        dialogFotoCamara = dialogFoto.findViewById(R.id.dialogPhotoCamara) as ImageButton
        dialogFotoCancelar = dialogFoto.findViewById(R.id.dialogPhotoCancelar) as Button


        interfazFoto = context as DialogSelectPhotoInt

        dialogFotoGaleria.setOnClickListener()
        {
            interfazFoto.abrirGaleria()
            dialogFoto.dismiss()
        }

        dialogFotoCamara.setOnClickListener()
        {
            interfazFoto.abrirCamara()
            dialogFoto.dismiss()
        }

        dialogFotoCancelar.setOnClickListener()
        {
            dialogFoto.dismiss()
        }
    }

    fun showDialog()
    {
        dialogFoto.show()
    }

    interface DialogSelectPhotoInt {
        fun abrirGaleria()
        fun abrirCamara()
    }
}