package aegina.lacamaronera.Dialog

import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.ServiceObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso


class DialogServices : AppCompatDialogFragment() {

    val url = Urls()

    lateinit var dialogServicesInt: DialogServicesInt
    lateinit var dialogGroup : Dialog
    lateinit var dialogServicesPhoto : ImageView
    lateinit var dialogTextTitle : TextView
    lateinit var dialogTextText : EditText
    lateinit var dialogServicesCost : EditText
    lateinit var dialogTextAccept : Button
    lateinit var dialogTextCancel : Button

    lateinit var globalVariable: GlobalClass

    fun createDialogService(context: Context, activity: Activity, globalClass: GlobalClass){
        globalVariable = globalClass
        dialogGroup = Dialog(context)

        dialogGroup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogGroup.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogGroup.setCancelable(false)
        dialogGroup.setContentView(R.layout.dialog_services)

        dialogServicesPhoto = dialogGroup.findViewById<View>(R.id.dialogServicesPhoto) as ImageView
        dialogTextTitle = dialogGroup.findViewById<View>(R.id.dialogServicesTitle) as TextView
        dialogTextText = dialogGroup.findViewById<View>(R.id.dialogServicesText) as EditText
        dialogServicesCost = dialogGroup.findViewById<View>(R.id.dialogServicesCost) as EditText
        dialogTextAccept = dialogGroup.findViewById<View>(R.id.dialogServicesAccept) as Button
        dialogTextCancel = dialogGroup.findViewById<View>(R.id.dialogServicesCancel) as Button

        dialogServicesInt = context as DialogServicesInt
    }

    fun openServiceDialog(position:Int, serviceObj: ServiceObj)
    {
        dialogTextText.setText(serviceObj.nombre)

        val url = globalVariable.user!!.url + url.endPointsImagenes.endPointObtenerImagen + "se" + serviceObj.idServicio +".jpeg&token="+ globalVariable.user!!.token

        dialogServicesPhoto.loadUrl(url)

        dialogTextAccept.setOnClickListener {
            if(dialogServicesCost.length() > 0 && dialogServicesCost.text.toString() != ".") {
                //dialogServicesInt.newService(dialogTextText.text.toString(), parseDouble(dialogServicesCost.text.toString()),position)

                view?.let { it1 -> hideSoftkeybard(it1) }
                dialogGroup.dismiss()
            }
        }

        dialogTextCancel.setOnClickListener()
        {
            view?.let { it1 -> hideSoftkeybard(it1) }
            dialogGroup.dismiss()
        }

        dialogGroup.show()
    }

    fun ImageView.loadUrl(url: String) {
        try {
            Picasso.with(context).load(url).into(this)}
        catch(e: Exception){}
    }

    private fun hideSoftkeybard(v: View) {
        val inputMethodManager = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    interface DialogServicesInt {
        fun newService(text : String, double: Double, position: Int)
    }
}