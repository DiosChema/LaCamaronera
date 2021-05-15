package aegina.lacamaronera.Fragments

import aegina.lacamaronera.Activities.Ingredients
import aegina.lacamaronera.General.GetGlobalClass
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerViewIngredients
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_ingredients.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class IngredientsFragment : Fragment() {

    var listIngredients: MutableList<IngredientObj> = ArrayList()

    private val urls: Urls = Urls()
    lateinit var progressDialog: ProgressDialog

    lateinit var mViewIngredient : RecyclerViewIngredients
    lateinit var contextTmp: Context

    lateinit var globalVariable: GlobalClass

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val getGlobalClass = GetGlobalClass()
        globalVariable = getGlobalClass.globalClass(activity!!.applicationContext)

        assignResources()
        createProgressDialog()
        getIngredients()
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun assignResources() {

        contextTmp = activity!!.applicationContext

        val ingredientsFragment: TextView = IngredientsAddDish

        ingredientsFragment.setOnClickListener()
        {
            val intent = Intent(activity, Ingredients::class.java)
            startActivity(intent)
        }

        createRecyclerView()
    }

    private fun createRecyclerView()
    {
        mViewIngredient = RecyclerViewIngredients()
        val mRecyclerView = InventoryIngredient
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(contextTmp)
        mViewIngredient.RecyclerAdapter(listIngredients, contextTmp, globalVariable)
        mRecyclerView.adapter = mViewIngredient
    }

    fun getIngredients()
    {
        val url = globalVariable.user!!.url+urls.endPointsIngredientes.endPointObtenerIngredientes

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.user!!.token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                activity?.runOnUiThread()
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

                        activity?.runOnUiThread()
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

    }

    override fun onResume() {
        super.onResume()

        if(globalVariable.updateWindow!!.refreshIngredient)
        {
            getIngredients()
            globalVariable.updateWindow!!.refreshIngredient = false
        }
    }
}