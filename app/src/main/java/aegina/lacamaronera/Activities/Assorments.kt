package aegina.lacamaronera.Activities

import aegina.lacamaronera.Objetos.AssormentListObj
import aegina.lacamaronera.Objetos.AssormentObj
import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerViewAssorments
import aegina.lacamaronera.RecyclerView.RecyclerViewDishes
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_platillos.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class Assorments : AppCompatActivity() {

    lateinit var mViewAssorments: RecyclerViewAssorments
    lateinit var progressDialog: ProgressDialog

    private val urls: Urls = Urls()
    lateinit var contextTmp: Context
    lateinit var activityTmp: Activity

    var listAssorments = ArrayList<AssormentListObj>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assorments)

        assignResources()
        createProgressDialog()
        getAssorments()
    }

    private fun assignResources() {
        contextTmp = this
        activityTmp = this

        createRecyclerView()
    }

    private fun createRecyclerView()
    {
        mViewAssorments = RecyclerViewAssorments()
        val mRecyclerView = findViewById<RecyclerView>(R.id.assormentsRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(contextTmp)
        mViewAssorments.RecyclerAdapter(listAssorments, contextTmp)
        mRecyclerView.adapter = mViewAssorments
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    fun getAssorments()
    {
        val url = urls.url+urls.endPointAssorment.endPointGetAssorment

        val jsonObject = JSONObject()
        try {
        } catch (e: JSONException)
        {
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
                        val model = gson.fromJson(body, Array<AssormentListObj>::class.java).toList()

                        listAssorments.clear()
                        for(assormentTmp : AssormentListObj in model)
                        {
                            listAssorments.add(assormentTmp)
                        }

                        runOnUiThread()
                        {
                            mViewAssorments.notifyDataSetChanged()
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