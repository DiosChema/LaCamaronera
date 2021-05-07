package aegina.lacamaronera.Dialog

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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment


class DialogGruop : AppCompatDialogFragment() {

    lateinit var dialogGroupIntInt: DialogGroupInt
    lateinit var dialogGroup : Dialog
    lateinit var dialogTextTitle : TextView
    lateinit var dialogTextText : EditText
    lateinit var dialogTextAccept : Button
    lateinit var dialogTextCancel : Button
    lateinit var dialogGroupDelete : ImageButton


    fun createDialogGroup(context : Context){
        dialogGroup = Dialog(context)

        dialogGroup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogGroup.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogGroup.setCancelable(false)
        dialogGroup.setContentView(R.layout.dialog_group)

        dialogGroupIntInt = context as DialogGroupInt

        dialogTextTitle = dialogGroup.findViewById<View>(R.id.dialogGroupTitle) as TextView
        dialogTextText = dialogGroup.findViewById<View>(R.id.dialogGroupText) as EditText
        dialogTextAccept = dialogGroup.findViewById<View>(R.id.dialogGroupAccept) as Button
        dialogTextCancel = dialogGroup.findViewById<View>(R.id.dialogGroupCancel) as Button
        dialogGroupDelete = dialogGroup.findViewById<View>(R.id.dialogGroupDelete) as ImageButton
    }

    fun openGroupDialog(contextTmp: Context, position:Int, textTmp: String)
    {
        if(textTmp == "")
        {
            dialogGroupDelete.visibility = View.INVISIBLE
            dialogTextTitle.text = contextTmp.getString(R.string.group_add_title)
        }
        else
        {
            dialogGroupDelete.visibility = View.VISIBLE
            dialogTextTitle.text = contextTmp.getString(R.string.group_edit_dialog)
        }

        dialogTextText.setText(textTmp)

        dialogTextAccept.setOnClickListener {
            if(dialogTextText.length() > 0) {
                if(textTmp == "")
                {
                    dialogGroupIntInt.newGroup(dialogTextText.text.toString())
                }
                else
                {
                    dialogGroupIntInt.editGroup(dialogTextText.text.toString(), position)
                }

                view?.let { it1 -> hideSoftkeybard(it1) }
                dialogGroup.dismiss()
            }
        }

        dialogTextCancel.setOnClickListener()
        {
            view?.let { it1 -> hideSoftkeybard(it1) }
            dialogGroup.dismiss()
        }

        dialogGroupDelete.setOnClickListener()
        {
            dialogGroupIntInt.deleteGroup(position)
            dialogGroup.dismiss()
        }

        dialogGroup.show()
    }

    private fun hideSoftkeybard(v: View) {
        val inputMethodManager = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    interface DialogGroupInt {
        fun newGroup(text : String)
        fun editGroup(text : String, position: Int)
        fun deleteGroup(position: Int)
    }
}