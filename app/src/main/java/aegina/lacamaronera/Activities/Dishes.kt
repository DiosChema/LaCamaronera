package aegina.lacamaronera.Activities

import aegina.lacamaronera.Objetos.Errores
import aegina.lacamaronera.Objetos.GroupObj
import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerViewDishIngredients
import aegina.lacamaronera.RecyclerView.RecyclerViewIngredientes
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_dishes.*
import kotlinx.android.synthetic.main.fragment_ingredients.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class Dishes : AppCompatActivity() {

    var listIngredients: MutableList<IngredientObj> = ArrayList()

    private val urls: Urls = Urls()
    lateinit var contextTmp : Context
    lateinit var activityTmp : Activity
    lateinit var progressDialog: ProgressDialog

    lateinit var dishName: TextView
    lateinit var dishPrice: TextView
    lateinit var dishDescription: TextView
    lateinit var dishGroup: Spinner
    lateinit var dishAdd: Button

    lateinit var mViewIngredient : RecyclerViewDishIngredients

    var listaFamilia:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dishes)

        assignResources()
        createProgressDialog()
        getGroups()
        createRecyclerView()
        //getIngredients()

    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun assignResources() {
        dishName = findViewById(R.id.dishName)
        dishPrice = findViewById(R.id.dishPrice)
        dishDescription = findViewById(R.id.dishDescription)
        dishGroup = findViewById(R.id.dishGroup)
        dishAdd = findViewById(R.id.dishAdd)

        dishAdd.setOnClickListener()
        {
            //addDish()
        }

    }

    private fun getGroups()
    {
        val errores = Errores()

        val url = urls.url+urls.endPointsGrupo.endPointObtenerGrupos
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                activityTmp.runOnUiThread()
                {
                    errores.procesarError(contextTmp, activityTmp)
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
                        val model = gson.fromJson(body, Array<GroupObj>::class.java).toList()

                        for(groupTmp : GroupObj in model)
                        {
                            listaFamilia.add(groupTmp.nombre)
                        }

                        runOnUiThread()
                        {
                            val adapter = ArrayAdapter(contextTmp,
                                android.R.layout.simple_spinner_item, listaFamilia)
                            dishGroup.adapter = adapter
                        }

                    }
                }
                catch (e: Exception){}
                finally
                {
                    progressDialog.dismiss()
                }
            }
        })
    }

    private fun createRecyclerView()
    {
        mViewIngredient = RecyclerViewDishIngredients()
        val mRecyclerView = dishIngredients
        mRecyclerView.setHasFixedSize(true)
        //mRecyclerView.layoutManager = LinearLayoutManager(contextTmp)
        mRecyclerView.layoutManager = LinearLayoutManager(contextTmp, LinearLayoutManager.HORIZONTAL ,false)
        mViewIngredient.RecyclerAdapter(listIngredients, contextTmp)
        mRecyclerView.adapter = mViewIngredient
    }

    /*fun getIngredients()
    {
        val url = urls.url+urls.endPointsIngredientes.endPointObtenerIngredientes
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
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

                        listIngredients.clear()
                        for(ingredientTmp : IngredientObj in model)
                        {
                            listIngredients.add(ingredientTmp)
                        }

                        runOnUiThread()
                        {
                            mViewIngredient.notifyDataSetChanged()
                        }

                    }
                }
                catch (e: Exception){}
                finally
                {
                    progressDialog.dismiss()
                }
            }
        })

    }*/
}