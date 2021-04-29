package aegina.lacamaronera.Dialog

import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerItemClickListener
import aegina.lacamaronera.RecyclerView.RecyclerViewIngredients
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Double.parseDouble

class DialogIngredients : AppCompatDialogFragment()
{
    lateinit var contextTmp : Context
    val urls: Urls = Urls()
    lateinit var activityTmp: Activity

    lateinit var recyclerViewIngredients: RecyclerViewIngredients
    lateinit var dialogIngredients: DialogIngredientsInt
    lateinit var dialogIngredientsIngredient : RecyclerView
    lateinit var dialogIngredientsCancel : Button

    lateinit var dialogArticulos : Dialog

    var listIngredients:MutableList<IngredientObj> = ArrayList()

    fun crearDialogInicial(context: Context, activity : Activity)
    {
        contextTmp = context
        activityTmp = activity
        dialogIngredients = contextTmp as DialogIngredientsInt

        dialogArticulos = Dialog(contextTmp)
        dialogArticulos.setCancelable(false)
        dialogArticulos.setContentView(R.layout.dialog_ingredients)

        dialogArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogArticulos.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogIngredientsCancel = dialogArticulos.findViewById(R.id.dialogIngredientsCancel) as Button
        dialogIngredientsIngredient = dialogArticulos.findViewById(R.id.dialogIngredientsIngredient) as RecyclerView

        recyclerViewIngredients = RecyclerViewIngredients()
        dialogIngredientsIngredient.setHasFixedSize(true)
        dialogIngredientsIngredient.layoutManager = LinearLayoutManager(contextTmp)
        recyclerViewIngredients.RecyclerAdapter(listIngredients, contextTmp)
        dialogIngredientsIngredient.adapter = recyclerViewIngredients

        dialogIngredientsIngredient.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, dialogIngredientsIngredient, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                amountDialog(position)
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                amountDialog(position)
            }
        }))

        dialogIngredientsCancel.setOnClickListener()
        {
            dialogArticulos.dismiss()
        }

        getIngredients()
    }

    fun showDialog()
    {
        dialogArticulos.show()
    }

    private fun amountDialog(position: Int) {
        val dialog = Dialog(activityTmp)
        dialog.setTitle(activityTmp.title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_number)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogNumberEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogNumberAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogNumberCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogNumberTitulo) as TextView

        dialogTitulo.text = contextTmp.getString(R.string.dish_add_ingredient_title)

        dialogAceptar.setOnClickListener {
            if(dialogText.length() > 0 && dialogText.text.toString() != ".")
            {
                var ingredientObjTmp = listIngredients[position]
                ingredientObjTmp.existencia = parseDouble(dialogText.text.toString())

                dialogIngredients.getIngredient(ingredientObjTmp)
                dialog.dismiss()
                dialogArticulos.dismiss()
            }
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun getIngredients()
    {
        val url = urls.url+urls.endPointsIngredientes.endPointObtenerIngredientes
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activityTmp.runOnUiThread()
                {
                    Toast.makeText(contextTmp, contextTmp.getString(R.string.error), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                try
                {
                    val body = response.body()?.string()

                    if(body != null && body.isNotEmpty())
                    {
                        val gson = GsonBuilder().create()
                        val model = gson.fromJson(body, Array<IngredientObj>::class.java).toList()

                        listIngredients.clear()
                        for(ingredientTmp : IngredientObj in model)
                        {
                            listIngredients.add(ingredientTmp)
                        }

                        activityTmp.runOnUiThread()
                        {
                            recyclerViewIngredients.notifyDataSetChanged()
                        }

                    }
                }
                catch (e: Exception){}
            }
        })

    }

    interface DialogIngredientsInt {
        fun getIngredient(ingredientObj: IngredientObj)
    }

}