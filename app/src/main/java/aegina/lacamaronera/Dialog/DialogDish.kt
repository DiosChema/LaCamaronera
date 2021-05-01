package aegina.lacamaronera.Dialog

import aegina.lacamaronera.Objetos.DishesObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerItemClickListener
import aegina.lacamaronera.RecyclerView.RecyclerViewDishes
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

class DialogDish: AppCompatDialogFragment()
{
    lateinit var contextTmp : Context
    val urls: Urls = Urls()
    lateinit var activityTmp: Activity

    lateinit var recyclerViewDishes: RecyclerViewDishes
    lateinit var dialogIngredients: DialogDishInt
    lateinit var dialogIngredientsIngredient: RecyclerView
    lateinit var dialogIngredientsCancel: Button
    lateinit var titleDialog: String

    lateinit var dialogArticulos : Dialog

    var listIngredients:MutableList<DishesObj> = ArrayList()

    fun createDialog(context: Context, activity : Activity)
    {
        contextTmp = context
        activityTmp = activity
        dialogIngredients = contextTmp as DialogDishInt

        dialogArticulos = Dialog(contextTmp)
        dialogArticulos.setCancelable(false)
        dialogArticulos.setContentView(R.layout.dialog_ingredients)

        dialogArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogArticulos.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogIngredientsCancel = dialogArticulos.findViewById(R.id.dialogIngredientsCancel) as Button
        dialogIngredientsIngredient = dialogArticulos.findViewById(R.id.dialogIngredientsIngredient) as RecyclerView

        recyclerViewDishes = RecyclerViewDishes()
        dialogIngredientsIngredient.setHasFixedSize(true)
        dialogIngredientsIngredient.layoutManager = LinearLayoutManager(contextTmp)
        recyclerViewDishes.RecyclerAdapter(listIngredients, contextTmp)
        dialogIngredientsIngredient.adapter = recyclerViewDishes

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

        getDishes()
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

        dialogTitulo.text = contextTmp.getString(R.string.dialog_dish_title)

        dialogAceptar.setOnClickListener {
            if(dialogText.length() > 0 && dialogText.text.toString() != ".")
            {
                val dishObjTmp = DishesObj(
                    listIngredients[position].idPlatillo,
                    listIngredients[position].nombre,
                    listIngredients[position].precio,
                    listIngredients[position].ingredientes,
                    listIngredients[position].idFamilia,
                    dialogText.text.toString()
                )

                dialogIngredients.getDish(dishObjTmp)
                dialog.dismiss()
                dialogArticulos.dismiss()
            }
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun getDishes()
    {
        val url = urls.url+urls.endPointDishes.endPointConsultarPlatillos
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
                        val model = gson.fromJson(body, Array<DishesObj>::class.java).toList()

                        listIngredients.clear()
                        for(ingredientTmp : DishesObj in model)
                        {
                            listIngredients.add(ingredientTmp)
                        }

                        activityTmp.runOnUiThread()
                        {
                            recyclerViewDishes.notifyDataSetChanged()
                        }

                    }
                }
                catch (e: Exception){}
            }
        })

    }

    interface DialogDishInt {
        fun getDish(dishSaleObj: DishesObj)
    }

}