package aegina.lacamaronera.Activities.Fragments

import aegina.lacamaronera.Activities.Ingredients
import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerViewIngredientes
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
import java.io.IOException
import java.lang.Exception

class IngredientsFragment : Fragment() {

    var listIngredients: MutableList<IngredientObj> = ArrayList()

    private val urls: Urls = Urls()
    lateinit var progressDialog: ProgressDialog

    lateinit var mViewIngredient : RecyclerViewIngredientes
    lateinit var contextTmp: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        mViewIngredient = RecyclerViewIngredientes()
        val mRecyclerView = IngredientsDish
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(contextTmp)
        mViewIngredient.RecyclerAdapter(listIngredients, contextTmp)
        mRecyclerView.adapter = mViewIngredient
    }

    fun getIngredients()
    {
        val url = urls.url+urls.endPointsIngredientes.endPointObtenerIngrediente
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
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
}