package aegina.lacamaronera.Activities

import aegina.lacamaronera.Dialog.DialogIngredients
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerItemClickListener
import aegina.lacamaronera.RecyclerView.RecyclerViewAssorment
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class Assorment : AppCompatActivity(), DialogIngredients.DialogIngredientsInt {

    lateinit var assormentIngredientsAmount: TextView
    lateinit var assormentIngredientsTotal: TextView
    lateinit var assormentIngredientsRecyclerView: RecyclerView
    lateinit var assormentIngredientsFinish: Button
    lateinit var assormentIngredientsAdd: Button

    lateinit var progressDialog: ProgressDialog
    lateinit var dialogIngredients: DialogIngredients
    private val urls: Urls = Urls()

    lateinit var contextTmp: Context
    lateinit var activityTmp: Context

    lateinit var mViewIngredient: RecyclerViewAssorment
    var listIngredients: MutableList<IngredientObj> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assorment)

        assignResources()
        createProgressDialog()
        updateAmountPrices()
    }

    private fun assignResources() {
        contextTmp = this
        activityTmp = this

        assormentIngredientsRecyclerView = findViewById(R.id.assormentIngredientsRecyclerView)
        assormentIngredientsFinish = findViewById(R.id.assormentIngredientsFinish)
        assormentIngredientsAmount = findViewById(R.id.assormentIngredientsAmount)
        assormentIngredientsTotal = findViewById(R.id.assormentIngredientsTotal)

        if(!isTablet(contextTmp))
        {
            assormentIngredientsAdd = findViewById(R.id.assormentIngredientsAdd)

            dialogIngredients = DialogIngredients()
            dialogIngredients.crearDialogInicial(this, this)

            assormentIngredientsAdd.setOnClickListener()
            {
                dialogIngredients.showDialog()
            }
        }
        else
        {

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

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                //amountDialog(position)
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                listIngredients.removeAt(position)
                runOnUiThread()
                {
                    mViewIngredient.notifyDataSetChanged()
                    updateAmountPrices()
                }
            }
        }))
    }


    fun isTablet(context: Context): Boolean
    {
        return ((context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    override fun getIngredient(ingredientObj: IngredientObj)
    {

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

    private fun updateAmountPrices()
    {
        var amountTmp = listIngredients.size
        var pricesTmp = 0.0

        for(i in 0 until listIngredients.size)
        {
            pricesTmp += (listIngredients[i].existencia * listIngredients[i].costo)
        }

        assormentIngredientsAmount.text = amountTmp.toString()
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


}