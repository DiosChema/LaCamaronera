package aegina.lacamaronera.Activities

import aegina.lacamaronera.General.Photo
import aegina.lacamaronera.Dialog.DialogSelectPhoto
import aegina.lacamaronera.General.GetGlobalClass
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.Errores
import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.ResponseObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.Double.parseDouble
import java.util.*


class Ingredients : AppCompatActivity(), DialogSelectPhoto.DialogSelectPhotoInt {

    private val urls = Urls()
    private val general = Photo()
    lateinit var progressDialog: ProgressDialog
    lateinit var contextTmp : Context
    lateinit var activityTmp : Activity
    lateinit var ingredientsPhoto: CircleImageView

    var dialogSelectPhoto = DialogSelectPhoto()
    var cambioFoto : Boolean = false

    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        val getGlobalClass = GetGlobalClass()
        globalVariable = getGlobalClass.globalClass(applicationContext)

        createProgressDialog()
        assignResources()
    }

    private fun assignResources()
    {

        contextTmp = this
        activityTmp = this

        var ingredientsName: TextView = findViewById(R.id.ingredientsName)
        var ingredientsPrice: TextView = findViewById(R.id.ingredientsPrice)
        var ingredientsDescription: TextView = findViewById(R.id.ingredientsDescription)
        var ingredientsExistence: TextView = findViewById(R.id.ingredientsExistence)
        var dishMeasurement: Spinner = findViewById(R.id.ingredientsMeasurement)
        var dishAdd: Button = findViewById(R.id.ingredientsAdd)
        var ingredientsCancel: Button = findViewById(R.id.ingredientsCancel)
        var ingredientsDelete: ImageButton = findViewById(R.id.ingredientsDelete)
        ingredientsPhoto = findViewById(R.id.ingredientsPhoto)

        ingredientsDelete.visibility = View.INVISIBLE
        ingredientsCancel.visibility = View.INVISIBLE

        var ListMeasurement: ArrayList<String> = ArrayList()

        ListMeasurement.add(getString(R.string.measurement_kg))
        ListMeasurement.add(getString(R.string.measurement_lt))
        ListMeasurement.add(getString(R.string.measurement_pz))

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, ListMeasurement)
        dishMeasurement.adapter = adapter

        dishAdd.setOnClickListener()
        {
            if( ingredientsName.text.length > 2 &&
                ingredientsDescription.text.length > 4 &&
                ingredientsExistence.text.isNotEmpty() &&
                ingredientsExistence.text.toString() != "." &&
                ingredientsPrice.text.isNotEmpty() &&
                ingredientsPrice.text.toString() != "."
            )
            {
                val ingredientObj = IngredientObj(
                    0,
                    ingredientsName.text.toString(),
                    parseDouble(ingredientsPrice.text.toString()),
                    ingredientsDescription.text.toString(),
                    parseDouble(ingredientsExistence.text.toString()),
                    ListMeasurement[dishMeasurement.selectedItemPosition],
                    ArrayList()
                )

                addIngredient(ingredientObj)
            }

        }

        ingredientsPhoto.setOnClickListener{
            dialogSelectPhoto.showDialog()
        }

        dialogSelectPhoto.createDialog(this)
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }


    private fun addIngredient(ingredientObj: IngredientObj)
    {
        val errores = Errores()

        val url = globalVariable.user!!.url+urls.endPointsIngredientes.endPointAltaIngrediente

        val jsonObject = JSONObject()
        try {
            jsonObject.put("nombre", ingredientObj.nombre)
            jsonObject.put("costo", ingredientObj.costo)
            jsonObject.put("descripcion", ingredientObj.descripcion)
            jsonObject.put("existencia", ingredientObj.existencia)
            jsonObject.put("unidad", ingredientObj.unidad)
            jsonObject.put("usoPlatillo", ingredientObj.usoPlatillo)
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

                        activityTmp.runOnUiThread()
                        {
                            if(respuesta.status == 0)
                            {
                                if(cambioFoto)
                                {
                                    uploadImage(respuesta.dato)
                                }
                                else
                                {
                                    globalVariable.updateWindow!!.refreshIngredient = true
                                    finish()
                                }
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

    private fun uploadImage(nombreImagen : String)
    {
        val drawable = ingredientsPhoto.drawable
        globalVariable.updateWindow!!.refreshIngredient = true

        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val file = general.bitmapToFile(bitmap, activityTmp)

        val MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "token",
                globalVariable.user!!.token)
            .addFormDataPart(
                "image",
            "in$nombreImagen.jpeg",
                RequestBody.create(MEDIA_TYPE_JPEG, file)
            ).build()
        val request = Request.Builder()
            .url(globalVariable.user!!.url+urls.endPointsImagenes.endPointAltaImagen)
            .post(req)
            .build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                finish()
            }

            override fun onResponse(call: Call, response: Response) {
                finish()
            }
        })
    }

    override fun abrirGaleria()
    {
        general.abrirGaleria(contextTmp, activityTmp)
    }

    override fun abrirCamara() {

        general.abrirCamara(contextTmp, activityTmp)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)
        {
            try
            {
                val inputStream: InputStream? =
                    if(requestCode == IMAGE_PICK_CODE)
                    {
                        data?.data?.let { contentResolver.openInputStream(it) }
                    }
                    else
                    {
                        general.image_uri?.let { contentResolver.openInputStream(it) }
                    }

                cambioFoto = true
                val bitmap = general.resizeBitmap( (Drawable.createFromStream(inputStream, general.image_uri.toString()) as BitmapDrawable).bitmap )

                ingredientsPhoto.setImageBitmap(bitmap)

            } catch (e: FileNotFoundException) { }
        }


    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val PERMISSION_CODE = 1001
    }

}