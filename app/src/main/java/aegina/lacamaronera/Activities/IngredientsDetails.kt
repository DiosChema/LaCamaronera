package aegina.lacamaronera.Activities

import aegina.lacamaronera.Activities.General.Photo
import aegina.lacamaronera.Dialog.DialogSelectPhoto
import aegina.lacamaronera.Objetos.Errores
import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.ResponseObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
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
import java.lang.Long
import java.util.ArrayList

class IngredientsDetails : AppCompatActivity() , DialogSelectPhoto.DialogSelectPhotoInt {

    private val urls = Urls()
    private val general = Photo()
    lateinit var progressDialog: ProgressDialog
    lateinit var contextTmp : Context
    lateinit var activityTmp : Activity

    lateinit var ingredientsName: TextView
    lateinit var ingredientsPrice: TextView
    lateinit var ingredientsDescription: TextView
    lateinit var ingredientsExistence: TextView
    lateinit var ingredientsMeasurement: Spinner
    lateinit var ingredientsAdd: Button
    lateinit var ingredientsCancel: Button
    lateinit var ingredientsDelete: ImageButton

    lateinit var ingredientsPhoto: CircleImageView

    var dialogSelectPhoto = DialogSelectPhoto()
    var cambioFoto : Boolean = false
    lateinit var ingredientObj : IngredientObj
    var idIngrediente = 0
    val ListMeasurement: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        createProgressDialog()

        idIngrediente = intent.getSerializableExtra("idIngrediente") as Int

        assignResources()
        getIngredient()
    }

    fun getIngredient()
    {
        var url = urls.url+urls.endPointsIngredientes.endPointObtenerIngrediente
        url += "?idIngrediente=$idIngrediente"
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
                    Log.v("TAG", "ENTRE");
                    val body = response.body()?.string()
                    Log.v("TAG", "ENTRE2");
                    if(body != null && body.isNotEmpty())
                    {
                        Log.v("TAG", "ENTRE3");
                        val gson = GsonBuilder().create()
                        Log.v("TAG", "ENTRE4");
                        Log.v("TAG", body.toString());
                        val model = gson.fromJson(body, IngredientObj::class.java)
                        Log.v("TAG", "ENTRE5");

                        ingredientObj = model

                        Log.v("TAG", ingredientObj.nombre);

                        runOnUiThread()
                        {
                            llenarDatos()
                        }

                    }
                }
                catch (e: java.lang.Exception)
                {
                    Log.v("TAG", e.toString());
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
        ingredientsPhoto.loadUrl(urls.url + urls.endPointsImagenes.endPointObtenerImagen + "in" + ingredientObj.idIngrediente+".jpeg")
        ingredientsName.text = ingredientObj.nombre
        ingredientsPrice.text = ingredientObj.costo.toString()
        ingredientsDescription.text = ingredientObj.descripcion
        ingredientsExistence.text = ingredientObj.existencia.toString()

        for(stringTmp : String in ListMeasurement)
        {
           if(ingredientObj.unidad == stringTmp)
           {
               break
           }
            contador++
        }

        ingredientsMeasurement.setSelection(contador)

    }

    private fun assignResources()
    {
        contextTmp = this
        activityTmp = this

        ingredientsName = findViewById(R.id.ingredientsName)
        ingredientsPrice = findViewById(R.id.ingredientsPrice)
        ingredientsDescription = findViewById(R.id.ingredientsDescription)
        ingredientsExistence = findViewById(R.id.ingredientsExistence)
        ingredientsMeasurement = findViewById(R.id.ingredientsMeasurement)
        ingredientsAdd = findViewById(R.id.ingredientsAdd)
        ingredientsPhoto = findViewById(R.id.ingredientsPhoto)

        ingredientsCancel = findViewById(R.id.ingredientsCancel)
        ingredientsDelete = findViewById(R.id.ingredientsDelete)

        ListMeasurement.add(getString(R.string.measurement_kg))
        ListMeasurement.add(getString(R.string.measurement_lt))
        ListMeasurement.add(getString(R.string.measurement_pz))

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, ListMeasurement)
        ingredientsMeasurement.adapter = adapter

        ingredientsAdd.setOnClickListener()
        {
            if(ingredientsName.isEnabled)
            {
                if( ingredientsName.text.length > 2 &&
                    ingredientsDescription.text.length > 4 &&
                    ingredientsExistence.text.isNotEmpty() &&
                    ingredientsPrice.text.isNotEmpty()
                )
                {
                    val ingredientObjTmp = IngredientObj(
                        ingredientObj.idIngrediente,
                        ingredientsName.text.toString(),
                        Double.parseDouble(ingredientsPrice.text.toString()),
                        ingredientsDescription.text.toString(),
                        Double.parseDouble(ingredientsExistence.text.toString()),
                        ListMeasurement[ingredientsMeasurement.selectedItemPosition],
                        ingredientObj.usoPlatillo
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
        ingredientsPrice.isEnabled = habilitar
        ingredientsDescription.isEnabled = habilitar
        ingredientsExistence.isEnabled = habilitar
        ingredientsMeasurement.isEnabled = habilitar
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


    private fun updateIngredient(ingredientObj: IngredientObj)
    {
        val errores = Errores()

        val url = urls.url+urls.endPointsIngredientes.endPointActualizarIngrediente

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(ingredientObj)

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonTutPretty)

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
                                    uploadImage(ingredientObj.idIngrediente.toString())
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

        dialogTitulo.text = getString(R.string.ingredient_delete)
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

        val url = urls.url+urls.endPointsIngredientes.endPointEliminarIngrediente

        val jsonObject = JSONObject()
        try {
            jsonObject.put("idIngrediente", ingredientObj.idIngrediente)
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
                "image",
                "in$nombreImagen.jpeg",
                RequestBody.create(MEDIA_TYPE_JPEG, file)
            ).build()
        val request = Request.Builder()
            .url(urls.url+urls.endPointsImagenes.endPointAltaImagen)
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
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

}