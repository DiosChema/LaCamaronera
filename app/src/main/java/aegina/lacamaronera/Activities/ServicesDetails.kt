package aegina.lacamaronera.Activities

import aegina.lacamaronera.General.Photo
import aegina.lacamaronera.Dialog.DialogSelectPhoto
import aegina.lacamaronera.General.GetGlobalClass
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Double
import java.util.ArrayList

class ServicesDetails : AppCompatActivity() , DialogSelectPhoto.DialogSelectPhotoInt {

    private val urls = Urls()
    private val general = Photo()
    lateinit var progressDialog: ProgressDialog
    lateinit var contextTmp : Context
    lateinit var activityTmp : Activity

    lateinit var ingredientsName: TextView
    lateinit var ingredientsDescription: TextView
    lateinit var ingredientsAdd: Button
    lateinit var ingredientsCancel: Button
    lateinit var ingredientsDelete: ImageButton

    lateinit var ingredientsPhoto: CircleImageView

    var dialogSelectPhoto = DialogSelectPhoto()
    var cambioFoto : Boolean = false
    lateinit var serviceObj : ServiceObj
    var idIngrediente = 0

    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        val getGlobalClass = GetGlobalClass()
        globalVariable = getGlobalClass.globalClass(applicationContext)

        createProgressDialog()

        idIngrediente = intent.getSerializableExtra("idServicio") as Int

        assignResources()
        getIngredient()
    }

    fun getIngredient()
    {
        val url = globalVariable.user!!.url+urls.endPointExpenses.endPointGetExpenses

        val jsonObject = JSONObject()
        try {
            jsonObject.put("idServicio", idIngrediente)
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
                        val model = gson.fromJson(body, Array<ServiceObj>::class.java)

                        runOnUiThread()
                        {
                            if(model.isNotEmpty())
                            {
                                serviceObj = model[0]
                                llenarDatos()
                            }
                            else
                            {
                                finish()
                            }
                        }

                    }
                }
                catch (e: Exception)
                {
                    runOnUiThread()
                    {
                        Toast.makeText(contextTmp, contextTmp.getString(R.string.error), Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
                finally
                {
                    progressDialog.dismiss()
                }
            }
        })

    }

    private fun llenarDatos()
    {
        var contador = 0
        ingredientsPhoto.loadUrl(globalVariable.user!!.url + urls.endPointsImagenes.endPointObtenerImagen + "se" + serviceObj.idServicio+ ".jpeg&token=" + globalVariable.user!!.token +".jpeg")
        ingredientsName.text = serviceObj.nombre
        ingredientsDescription.text = serviceObj.descripcion

    }

    private fun assignResources()
    {
        contextTmp = this
        activityTmp = this

        ingredientsName = findViewById(R.id.ingredientsName)
        ingredientsDescription = findViewById(R.id.ingredientsDescription)
        ingredientsAdd = findViewById(R.id.ingredientsAdd)
        ingredientsPhoto = findViewById(R.id.ingredientsPhoto)

        ingredientsCancel = findViewById(R.id.ingredientsCancel)
        ingredientsDelete = findViewById(R.id.ingredientsDelete)

        ingredientsAdd.setOnClickListener()
        {
            if(ingredientsName.isEnabled)
            {
                if( ingredientsName.text.length > 2 &&
                    ingredientsDescription.text.length > 4
                )
                {
                    val ingredientObjTmp = ServiceObj(
                        serviceObj.idServicio,
                        ingredientsName.text.toString(),
                        ingredientsDescription.text.toString()
                    )

                    updateIngredient(ingredientObjTmp)
                }
            }
            else
            {
                habilitarBotones(true)
                ingredientsAdd.text = getString(R.string.ingredient_update)
            }
        }

        ingredientsPhoto.setOnClickListener()
        {
            dialogSelectPhoto.showDialog()
        }

        ingredientsCancel.setOnClickListener()
        {
            habilitarBotones(false)
            llenarDatos()
            ingredientsAdd.text = getString(R.string.ingredient_edit)
        }

        ingredientsDelete.setOnClickListener()
        {
            showDialogEliminarArticulo()
        }

        dialogSelectPhoto.createDialog(this)

        habilitarBotones(false)
        ingredientsAdd.text = getString(R.string.ingredient_edit)
    }

    private fun habilitarBotones(habilitar: Boolean) {
        ingredientsName.isEnabled = habilitar
        ingredientsDescription.isEnabled = habilitar
        ingredientsPhoto.isEnabled = habilitar

        mostarCampos(habilitar)
    }

    private fun mostarCampos(habilitar: Boolean) {
        if(habilitar)
        {
            ingredientsDelete.visibility = View.VISIBLE
            ingredientsCancel.visibility = View.VISIBLE
        }
        else
        {
            ingredientsDelete.visibility = View.INVISIBLE
            ingredientsCancel.visibility = View.INVISIBLE
        }
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }


    private fun updateIngredient(ingredientObj: ServiceObj)
    {
        val errores = Errores()

        val url = globalVariable.user!!.url+urls.endPointExpenses.endPointUpdateExpenses

        val jsonObject = JSONObject()
        try {
            jsonObject.put("idServicio", ingredientObj.idServicio)
            jsonObject.put("nombre", ingredientObj.nombre)
            jsonObject.put("descripcion", ingredientObj.descripcion)
            jsonObject.put("token", globalVariable.user!!.token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .put(body)
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
                                    uploadImage(ingredientObj.idServicio.toString())
                                }
                                else
                                {
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

    private fun showDialogEliminarArticulo() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogTextEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogTextAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogTextCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTextTitulo) as TextView

        dialogTitulo.text = getString(R.string.expenses_delete)
        dialogText.visibility = View.INVISIBLE

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            deleteIngredient()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteIngredient()
    {
        val errores = Errores()

        val url = globalVariable.user!!.url+urls.endPointExpenses.endPointDeleteServices

        val jsonObject = JSONObject()
        try {
            jsonObject.put("idServicio", serviceObj.idServicio)
            jsonObject.put("token", globalVariable.user!!.token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .delete(body)
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

    private fun uploadImage(nombreImagen : String)
    {
        val drawable = ingredientsPhoto.drawable

        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val file = general.bitmapToFile(bitmap, activityTmp)

        val MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "token",
                globalVariable.user!!.token
            )
            .addFormDataPart(
                "image",
                "se$nombreImagen.jpeg",
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

    fun ImageView.loadUrl(url: String) {
        try {
            Picasso.with(context).load(url).noFade().into(this)}
        catch(e: java.lang.Exception){}
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val PERMISSION_CODE = 1001
    }

}