package aegina.lacamaronera.Activities

import aegina.lacamaronera.Dialog.DialogServices
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Double.parseDouble
import java.text.SimpleDateFormat
import java.util.*

class ServicesExpense : AppCompatActivity() {

    val url = Urls()

    lateinit var dialogServicesInt: DialogServices.DialogServicesInt
    lateinit var dialogServicesPhoto : ImageView
    lateinit var dialogTextTitle : TextView
    lateinit var dialogTextText : EditText
    lateinit var dialogServicesCost : EditText
    lateinit var dialogTextAccept : Button
    lateinit var dialogTextCancel : Button
    lateinit var serviceObj: ServiceObj
    lateinit var progressDialog: ProgressDialog
    lateinit var contextTmp: Context
    lateinit var activityTmp: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_expense)

        serviceObj = intent.getSerializableExtra("service") as ServiceObj
        contextTmp = this
        activityTmp = this

        createProgressDialog()
        assignRessources()
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun assignRessources() {
        dialogServicesPhoto = findViewById<View>(R.id.dialogServicesPhoto) as ImageView
        dialogTextTitle = findViewById<View>(R.id.dialogServicesTitle) as TextView
        dialogTextText = findViewById<View>(R.id.dialogServicesText) as EditText
        dialogServicesCost = findViewById<View>(R.id.dialogServicesCost) as EditText
        dialogTextAccept = findViewById<View>(R.id.dialogServicesAccept) as Button
        dialogTextCancel = findViewById<View>(R.id.dialogServicesCancel) as Button

        val url = url.url + url.endPointsImagenes.endPointObtenerImagen + "se" + serviceObj.idServicio+".jpeg"

        dialogTextTitle.setText(serviceObj.nombre)

        dialogServicesPhoto.loadUrl(url)

        dialogTextAccept.setOnClickListener {
            if(dialogServicesCost.length() > 0 && dialogServicesCost.text.toString() != ".") {
                addExpenseService(dialogTextText.text.toString(), parseDouble(dialogServicesCost.text.toString()))
            }
        }

        dialogTextCancel.setOnClickListener()
        {
            finish()
        }
    }

    private fun addExpenseService(text: String, cost: Double)
    {
        val errores = Errores()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(Date())

        val url = url.url+url.endPointExpenses.endPointPostExpensesServices

        val jsonObject = JSONObject()
        try {
            jsonObject.put("idServicio", serviceObj.idServicio)
            jsonObject.put("nombre", serviceObj.nombre)
            jsonObject.put("descripcion", text)
            jsonObject.put("fecha",  currentDate.toString())
            jsonObject.put("gasto",  cost)
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
                errores.procesarError(contextTmp, activityTmp)
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()

                try
                {
                    if(body != null && body.isNotEmpty())
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, ResponseObj::class.java)

                        runOnUiThread()
                        {
                            if(respuesta.status == 0)
                            {
                                finish()
                            }
                            else
                            {
                                errores.procesarErrorMensaje(contextTmp, activityTmp, respuesta)
                            }
                        }
                    }
                }
                catch(e: Exception)
                {
                    errores.procesarError(contextTmp, activityTmp)
                    progressDialog.dismiss()
                }
            }
        })
    }

    fun ImageView.loadUrl(url: String) {
        try {
            Picasso.with(context).load(url).into(this)}
        catch(e: Exception){}
    }
}