package aegina.lacamaronera.Activities

import aegina.lacamaronera.General.Photo
import aegina.lacamaronera.Dialog.DialogSelectPhoto
import aegina.lacamaronera.Objetos.*
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
import java.io.*
import java.lang.Double.parseDouble
import java.util.*


class Services: AppCompatActivity(), DialogSelectPhoto.DialogSelectPhotoInt {

    private val urls = Urls()
    private val general = Photo()
    lateinit var progressDialog: ProgressDialog
    lateinit var contextTmp : Context
    lateinit var activityTmp : Activity
    lateinit var ingredientsPhoto: CircleImageView

    var dialogSelectPhoto = DialogSelectPhoto()
    var cambioFoto : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        createProgressDialog()
        assignResources()
    }

    private fun assignResources()
    {

        contextTmp = this
        activityTmp = this

        var ingredientsName: TextView = findViewById(R.id.ingredientsName)
        var ingredientsDescription: TextView = findViewById(R.id.ingredientsDescription)
        var dishAdd: Button = findViewById(R.id.ingredientsAdd)
        var ingredientsCancel: Button = findViewById(R.id.ingredientsCancel)
        var ingredientsDelete: ImageButton = findViewById(R.id.ingredientsDelete)
        ingredientsPhoto = findViewById(R.id.ingredientsPhoto)

        ingredientsDelete.visibility = View.INVISIBLE
        ingredientsCancel.visibility = View.INVISIBLE


        dishAdd.setOnClickListener()
        {
            if( ingredientsName.text.length > 2 &&
                ingredientsDescription.text.length > 4
            )
            {
                val ingredientObj = ServiceObj(
                    0,
                    ingredientsName.text.toString(),
                    ingredientsDescription.text.toString()
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


    private fun addIngredient(ingredientObj: ServiceObj)
    {
        val errores = Errores()

        val url = urls.url+urls.endPointExpenses.endPointPostExpenses

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(ingredientObj)

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

        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val file = general.bitmapToFile(bitmap, activityTmp)

        val MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "se$nombreImagen.jpeg",
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

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

}