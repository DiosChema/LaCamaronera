package aegina.lacamaronera.Fragments

import aegina.lacamaronera.General.GetGlobalClass
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.ExpenseServiceObj
import aegina.lacamaronera.Objetos.ServiceObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerViewExpenseServices
import aegina.lacamaronera.RecyclerView.RecyclerViewServices
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_ingredients.*
import kotlinx.android.synthetic.main.fragment_platillos.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class ExpensesHisFragment : Fragment() {

    var listDishes: MutableList<ExpenseServiceObj> = ArrayList()

    private val urls: Urls = Urls()
    lateinit var progressDialog: ProgressDialog

    lateinit var mViewDish : RecyclerViewExpenseServices
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
        getDishes()
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun assignResources() {

        contextTmp = activity!!.applicationContext

        val inventoryAddDish: Button = IngredientsAddDish

        inventoryAddDish.visibility = View.GONE
        /*inventoryAddDish.setOnClickListener()
        {
            val intent = Intent(activity, Dishes::class.java)
            startActivity(intent)
        }*/

        createRecyclerView()

    }

    private fun createRecyclerView()
    {
        mViewDish = RecyclerViewExpenseServices()
        val mRecyclerView = InventoryIngredient
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(contextTmp)
        mViewDish.RecyclerAdapter(listDishes, contextTmp, globalVariable)
        mRecyclerView.adapter = mViewDish
    }

    fun getDishes()
    {
        val url = globalVariable.user!!.url+urls.endPointExpenses.endPointGetExpensesServices

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
                        val model = gson.fromJson(body, Array<ExpenseServiceObj>::class.java).toList()

                        listDishes.clear()
                        for(ingredientTmp : ExpenseServiceObj in model)
                        {
                            listDishes.add(ingredientTmp)
                        }

                        activity?.runOnUiThread()
                        {
                            mViewDish.notifyDataSetChanged()
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

        getDishes()
    }

}