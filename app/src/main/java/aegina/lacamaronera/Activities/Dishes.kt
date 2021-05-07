package aegina.lacamaronera.Activities

import aegina.lacamaronera.General.Photo
import aegina.lacamaronera.Dialog.DialogIngredients
import aegina.lacamaronera.Dialog.DialogSelectPhoto
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerItemClickListener
import aegina.lacamaronera.RecyclerView.RecyclerViewDishIngredients
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
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.*
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Double.parseDouble
import java.lang.Exception
import java.util.ArrayList

class Dishes : AppCompatActivity(), DialogIngredients.DialogIngredientsInt,
    DialogSelectPhoto.DialogSelectPhotoInt {

    var listIngredients: MutableList<IngredientObj> = ArrayList()
    private val general = Photo()

    private val urls: Urls = Urls()
    lateinit var contextTmp : Context
    lateinit var activityTmp : Activity
    lateinit var progressDialog: ProgressDialog

    lateinit var dialogIngredients: DialogIngredients
    var dialogSelectPhoto = DialogSelectPhoto()

    lateinit var dishName: TextView
    lateinit var dishPrice: TextView
    lateinit var dishDescription: TextView
    lateinit var dishGroup: Spinner
    lateinit var dishAdd: Button
    lateinit var dishAddIngredient: Button
    lateinit var dishLinearLayout: LinearLayout

    lateinit var dishesPhoto: CircleImageView
    var cambioFoto = false

    lateinit var mViewIngredient : RecyclerViewDishIngredients

    var listaFamilia:ArrayList<String> = ArrayList()
    var listGroup: ArrayList<GroupObj> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dishes)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        assignResources()
        createProgressDialog()
        getGroups()
        createRecyclerView()
        //getIngredients()
        dialogIngredients = DialogIngredients()
        dialogIngredients.textDish(this)
        dialogIngredients.createDialog(contextTmp, activityTmp)

    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun assignResources() {
        contextTmp = this
        activityTmp = this

        dishesPhoto = findViewById(R.id.dishesPhoto)
        dishName = findViewById(R.id.dishName)
        dishPrice = findViewById(R.id.dishPrice)
        dishDescription = findViewById(R.id.dishDescription)
        dishGroup = findViewById(R.id.dishGroup)
        dishAdd = findViewById(R.id.dishAdd)
        dishAddIngredient = findViewById(R.id.dishAddIngredient)
        dishLinearLayout = findViewById(R.id.dishLinearLayout)

        dishLinearLayout.visibility = View.GONE


        dishAdd.setOnClickListener()
        {
            if(dishName.text.length > 2 &&
                dishPrice.text.isNotEmpty() &&
                dishPrice.text.toString() != "." &&
                dishDescription.text.length> 4
            )
            {
                var ingredientsDish = ArrayList<IngredientDishObj>()

                for(ingredientObjTmp: IngredientObj in listIngredients)
                {
                    ingredientsDish.add(IngredientDishObj(ingredientObjTmp.idIngrediente, ingredientObjTmp.existencia))
                }
                var dishesObj = DishesObj(
                    0,
                    dishName.text.toString(),
                    parseDouble(dishPrice.text.toString()),
                    ingredientsDish,
                    listGroup[dishGroup.selectedItemPosition].idFamilia,
                    dishDescription.text.toString()
                )

                addDish(dishesObj)
            }
        }

        dishAddIngredient.setOnClickListener()
        {
            dialogIngredients.showDialog()
        }

        dishesPhoto.setOnClickListener{
            dialogSelectPhoto.showDialog()
        }

        dialogSelectPhoto.createDialog(this)

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
                            listGroup.add(groupTmp)
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
        val mRecyclerView = findViewById<RecyclerView>(R.id.dishIngredients)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL ,false)
        mViewIngredient.RecyclerAdapter(listIngredients, this)
        mRecyclerView.adapter = mViewIngredient

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                amountDialog(position)
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                listIngredients.removeAt(position)
                runOnUiThread()
                {
                    mViewIngredient.notifyDataSetChanged()
                }
            }
        }))
    }

    private fun amountDialog(position: Int) {
        val dialog = Dialog(activityTmp)
        dialog.setTitle(activityTmp.title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_number)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogNumberEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogNumberAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogNumberCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogNumberTitulo) as TextView

        dialogTitulo.text = contextTmp.getString(R.string.dish_add_ingredient_title_dish)

        dialogText.setText(listIngredients[position].existencia.toString())

        dialogAceptar.setOnClickListener {
            if(dialogText.length() > 0 && dialogText.text.toString() != ".")
            {
                listIngredients[position].existencia = parseDouble(dialogText.text.toString())

                dialog.dismiss()
                runOnUiThread()
                {
                    mViewIngredient.notifyDataSetChanged()
                }
            }
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getIngredient(ingredientObj: IngredientObj)
    {
        if(!ingredientAlreadyInDish(ingredientObj))
        {
            listIngredients.add(ingredientObj)
        }

        runOnUiThread()
        {
            mViewIngredient.notifyDataSetChanged()
        }
    }

    private fun ingredientAlreadyInDish(ingredientObj: IngredientObj): Boolean
    {
        var i = 0
        for(i in 0 until  listIngredients.size)
        {
            if(ingredientObj.idIngrediente == listIngredients[i].idIngrediente)
            {
                listIngredients[i].existencia += ingredientObj.existencia
                return true
            }
        }
        return false
    }

    private fun addDish(dishesObj: DishesObj)
    {
        val errores = Errores()

        val url = urls.url+urls.endPointDishes.endPointAltaPlatillos

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(dishesObj)

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

                        runOnUiThread()
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
        val drawable = dishesPhoto.drawable

        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val file = general.bitmapToFile(bitmap, activityTmp)

        val MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "pl$nombreImagen.jpeg",
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

                dishesPhoto.setImageBitmap(bitmap)

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