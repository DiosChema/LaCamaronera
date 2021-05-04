package aegina.lacamaronera.Activities

import aegina.lacamaronera.Dialog.DialogDish
import aegina.lacamaronera.Dialog.DialogEnterNumber
import aegina.lacamaronera.Dialog.DialogIngredients
import aegina.lacamaronera.Dialog.DialogNumber
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerItemClickListener
import aegina.lacamaronera.RecyclerView.RecyclerViewAssorment
import aegina.lacamaronera.RecyclerView.RecyclerViewIngredientsTablet
import aegina.lacamaronera.RecyclerView.RecyclerViewSale
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Double
import java.lang.Double.parseDouble
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class Assorment : AppCompatActivity(),
    DialogIngredients.DialogIngredientsInt,
    DialogEnterNumber.DialogEnterNumberInt,
    RecyclerViewAssorment.AssormentInt{

    lateinit var assormentIngredientsTotal: TextView
    lateinit var assormentIngredientsRecyclerView: RecyclerView
    lateinit var assormentIngredientsListRecyclerView: RecyclerView
    lateinit var assormentIngredientsFinish: Button
    lateinit var assormentIngredientsAdd: Button

    lateinit var progressDialog: ProgressDialog
    lateinit var dialogIngredients: DialogIngredients
    lateinit var dialogNumber: DialogEnterNumber
    private val urls: Urls = Urls()

    lateinit var contextTmp: Context
    lateinit var activityTmp: Context

    lateinit var mViewIngredient: RecyclerViewAssorment
    lateinit var recyclerViewIngredients: RecyclerViewIngredientsTablet
    var listIngredients: MutableList<IngredientObj> = ArrayList()
    var listIngredientsTablet: MutableList<IngredientObj> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assorment)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        assignResources()
        createProgressDialog()
        updateAmountPrices()
    }

    private fun assignResources() {
        contextTmp = this
        activityTmp = this

        assormentIngredientsRecyclerView = findViewById(R.id.assormentIngredientsRecyclerView)
        assormentIngredientsFinish = findViewById(R.id.assormentIngredientsFinish)
        assormentIngredientsTotal = findViewById(R.id.assormentIngredientsTotal)

        if(resources.getBoolean(R.bool.portrait_only))
        {
            assormentIngredientsAdd = findViewById(R.id.assormentIngredientsAdd)

            dialogIngredients = DialogIngredients()
            dialogIngredients.textAssorment(this)
            dialogIngredients.createDialog(this, this)

            assormentIngredientsAdd.setOnClickListener()
            {
                dialogIngredients.showDialog()
            }
        }
        else
        {
            val assormentIngredientsList = findViewById<TextView>(R.id.assormentIngredientsList)
            assormentIngredientsList.text = getText(R.string.assorments_list)

            assormentIngredientsListRecyclerView = findViewById(R.id.assormentIngredientsListRecyclerView)
            createRecyclerViewIngredients()
        }

        assormentIngredientsFinish.setOnClickListener()
        {
            addAssorment()
        }

        createRecyclerView()
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun addAssorment()
    {
        val errores = Errores()

        val listAssorment = ArrayList<AssormentIngredientObj>()

        for(i in 0 until listIngredients.size)
        {
            listAssorment.add(
                AssormentIngredientObj(
                    listIngredients[i].idIngrediente,
                    listIngredients[i].costo,
                    listIngredients[i].existencia,
                    listIngredients[i].nombre,
                    listIngredients[i].descripcion)
            )
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(Date())

        val assormentObj = AssormentObj(0, listAssorment, 0.0, currentDate.toString())

        val url = urls.url+urls.endPointAssorment.endPointPostAssorment

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(assormentObj)

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonTutPretty)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                errores.procesarError(contextTmp, activityTmp as Activity)
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()

                try
                {
                    if(body != null && body.isNotEmpty())
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, ResponseObj::class.java)

                        if(respuesta.status == 0)
                        {
                            runOnUiThread()
                            {
                                clearScreen()
                            }
                        }
                        else
                        {
                            progressDialog.dismiss()
                            errores.procesarErrorMensaje(contextTmp, activityTmp as Activity, respuesta)
                        }
                    }
                }
                catch(e: Exception)
                {
                    errores.procesarError(contextTmp, activityTmp as Activity)
                    progressDialog.dismiss()
                }
                finally
                {
                    progressDialog.dismiss()
                }
            }
        })
    }

    private fun clearScreen() {
        listIngredients.clear()
        mViewIngredient.notifyDataSetChanged()
        updateAmountPrices()
    }

    private fun createRecyclerView()
    {
        mViewIngredient = RecyclerViewAssorment()
        val mRecyclerView = findViewById<RecyclerView>(R.id.assormentIngredientsRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewIngredient.RecyclerAdapter(listIngredients, this)
        mRecyclerView.adapter = mViewIngredient
    }

    private fun createRecyclerViewIngredients()
    {
        recyclerViewIngredients = RecyclerViewIngredientsTablet()
        val mRecyclerView = findViewById<RecyclerView>(R.id.assormentIngredientsListRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(this, 3, RecyclerView.HORIZONTAL, false)
        recyclerViewIngredients.RecyclerAdapter(listIngredientsTablet, this)
        mRecyclerView.adapter = recyclerViewIngredients

        dialogNumber = DialogEnterNumber()
        dialogNumber.createDialog(this,this)

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                //dialogNumber.showDialog(position)
                val ingredientObj = listIngredientsTablet[position]

                ingredientObj.existencia = 1.0

                listIngredients.add(ingredientObj)

                runOnUiThread()
                {
                    updateAmountPrices()
                    mViewIngredient.notifyDataSetChanged()
                }
            }

            override fun onLongItemClick(view: View?, position: Int){}
        }))

        getIngredients()
    }

    /*private fun amountDialog(position: Int) {
        val dialog = Dialog(activityTmp)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_assorment)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogAssormentAmount = dialog.findViewById(R.id.dialogAssormentAmount) as EditText
        val dialogAssormentPrice = dialog.findViewById(R.id.dialogAssormentPrice) as EditText
        val dialogAssormentDescription = dialog.findViewById(R.id.dialogAssormentDescription) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogNumberAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogNumberCancelar) as Button
        val dialogAssormentTitulo = dialog.findViewById(R.id.dialogAssormentTitulo) as TextView

        dialogAssormentTitulo.text = contextTmp.getString(R.string.dialog_assorment_title)

        dialogAssormentAmount.setText(listIngredients[position].existencia.toString())
        dialogAssormentPrice.setText(listIngredients[position].costo.toString())
        dialogAssormentDescription.setText(listIngredients[position].descripcion)

        dialogAceptar.setOnClickListener {
            if(dialogAssormentAmount.length() > 0 && dialogAssormentAmount.text.toString() != "." &&
                dialogAssormentPrice.length() > 0 && dialogAssormentPrice.text.toString() != ".")
            {
                listIngredients[position].existencia =
                    Double.parseDouble(dialogAssormentAmount.text.toString())
                listIngredients[position].costo =
                    Double.parseDouble(dialogAssormentPrice.text.toString())
                listIngredients[position].descripcion =
                    dialogAssormentDescription.text.toString()

                dialog.dismiss()
                runOnUiThread()
                {
                    mViewIngredient.notifyDataSetChanged()
                    updateAmountPrices()
                }
            }
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }*/

    private fun updateAmountPrices()
    {
        var pricesTmp = 0.0

        for(i in 0 until listIngredients.size)
        {
            pricesTmp += (listIngredients[i].existencia * listIngredients[i].costo)
        }

        assormentIngredientsTotal.text = pricesTmp.toString()
    }

    private fun ingredientAlreadyInDish(ingredientObj: IngredientObj): Boolean
     {
        var i = 0
        for(i in 0 until  listIngredients.size)
        {
            if(ingredientObj.idIngrediente == listIngredients[i].idIngrediente)
            {
                listIngredients[i].existencia += ingredientObj.existencia
                return true
            }
        }
        return false
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
                runOnUiThread()
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

                        listIngredientsTablet.clear()
                        for(ingredientTmp : IngredientObj in model)
                        {
                            listIngredientsTablet.add(ingredientTmp)
                        }

                        runOnUiThread()
                        {
                            recyclerViewIngredients.notifyDataSetChanged()
                        }

                    }
                }
                catch (e: Exception){}
            }
        })

    }

    override fun getNumber(number: kotlin.Double, position: Int) {
        val ingredientObj = listIngredientsTablet[position]

        ingredientObj.existencia = number

        if(!ingredientAlreadyInDish(ingredientObj))
        {
            listIngredients.add(ingredientObj)
        }

        runOnUiThread()
        {
            updateAmountPrices()
            mViewIngredient.notifyDataSetChanged()
        }
    }

    override fun actualizarNumero(value: Int, position: Int) {
        val ingredientObj = IngredientObj(
            listIngredients[position].idIngrediente,
            listIngredients[position].nombre,
            listIngredients[position].costo,
            listIngredients[position].descripcion,
            listIngredients[position].existencia + value,
            listIngredients[position].unidad,
            listIngredients[position].usoPlatillo
        )

        listIngredients[position] = ingredientObj

        if(listIngredients[position].existencia <= 0.0)
        {
            listIngredients.removeAt(position)
        }

        runOnUiThread()
        {
            mViewIngredient.notifyDataSetChanged()
            updateAmountPrices()
        }

    }

    override fun getIngredient(ingredientObj: IngredientObj) {
        if(!ingredientAlreadyInDish(ingredientObj))
        {
            listIngredients.add(ingredientObj)
        }

        runOnUiThread()
        {
            updateAmountPrices()
            mViewIngredient.notifyDataSetChanged()
        }
    }

}