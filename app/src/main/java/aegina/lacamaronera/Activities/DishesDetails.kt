package aegina.lacamaronera.Activities

import aegina.lacamaronera.Activities.General.Photo
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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_dishes.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Double
import java.lang.Exception
import java.util.ArrayList

class DishesDetails : AppCompatActivity(), DialogIngredients.DialogIngredientsInt,
    DialogSelectPhoto.DialogSelectPhotoInt {

    var listIngredients: MutableList<IngredientObj> = ArrayList()
    private val general = Photo()
    lateinit var dishObj: DishesObj
    var idDish = 0

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
    lateinit var dishCancel: Button
    lateinit var dishDelete: ImageButton
    lateinit var dishLinearLayout: LinearLayout

    lateinit var dishesPhoto: CircleImageView
    var cambioFoto = false


    lateinit var mViewIngredient : RecyclerViewDishIngredients

    var listaFamilia: ArrayList<String> = ArrayList()
    var listGroup: ArrayList<GroupObj> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dishes)

        idDish = intent.getSerializableExtra("idDish") as Int

        assignResources()
        createProgressDialog()
        getGroups()
        createRecyclerView()
        dialogIngredients = DialogIngredients()
        dialogIngredients.crearDialogInicial(contextTmp, activityTmp)

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
        dishCancel = findViewById(R.id.dishCancel)
        dishDelete = findViewById(R.id.dishDelete)
        dishLinearLayout = findViewById(R.id.dishLinearLayout)

        dishLinearLayout.visibility = View.GONE

        dishAdd.setOnClickListener()
        {
            if(dishName.isEnabled)
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
                        idDish,
                        dishName.text.toString(),
                        Double.parseDouble(dishPrice.text.toString()),
                        ingredientsDish,
                        listGroup[dishGroup.selectedItemPosition].idFamilia,
                        dishDescription.text.toString()
                    )

                    updateDish(dishesObj)
                }
            }
            else
            {
                habilitarBotones(true)
                dishAdd.text = getString(R.string.ingredient_update)
            }
        }

        dishAddIngredient.setOnClickListener()
        {
            dialogIngredients.showDialog()
        }

        dishCancel.setOnClickListener()
        {
            habilitarBotones(false)
            llenarDatos()
            dishAdd.text = getString(R.string.ingredient_edit)
        }

        dishDelete.setOnClickListener()
        {
            showDialogDeleteDish()
        }

        dishesPhoto.setOnClickListener{
            dialogSelectPhoto.showDialog()
        }

        dialogSelectPhoto.createDialog(this)

        habilitarBotones(false)
        dishAdd.text = getString(R.string.ingredient_edit)

    }

    private fun llenarDatos()
    {
        var contador = 0
        dishesPhoto.loadUrl(urls.url + urls.endPointsImagenes.endPointObtenerImagen + "pl" + dishObj.idPlatillo+".jpeg")
        dishName.text = dishObj.nombre
        dishPrice.text = dishObj.precio.toString()
        dishDescription.text = dishObj.descripcion

        for(groupTmp: GroupObj in listGroup)
        {
            if(groupTmp.idFamilia == dishObj.idFamilia)
            {
                break
            }
            contador++
        }

        listIngredients.clear()

        for(ingredientDishObj: IngredientDishObj in dishObj.ingredientes)
        {
            listIngredients.add(IngredientObj(
                ingredientDishObj.idIngrediente,
                "",
                0.0,
                "",
                ingredientDishObj.cantidad,
                "",
                ArrayList()
            ))
        }

        mViewIngredient.notifyDataSetChanged()

        dishGroup.setSelection(contador)

    }

    fun getDish(){

        var url = urls.url+urls.endPointDishes.endPointConsultaPlatillo
        url += "?idPlatillo=$idDish"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

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
                        val model = gson.fromJson(body, DishesObj::class.java)

                        dishObj = model

                        runOnUiThread()
                        {
                            llenarDatos()
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

    private fun habilitarBotones(habilitar: Boolean) {
        dishName.isEnabled = habilitar
        dishPrice.isEnabled = habilitar
        dishDescription.isEnabled = habilitar
        dishGroup.isEnabled = habilitar
        dishesPhoto.isEnabled = habilitar
        dishAddIngredient.isEnabled = habilitar

        mostarCampos(habilitar)
    }

    private fun mostarCampos(habilitar: Boolean) {
        if(habilitar)
        {
            dishLinearLayout.visibility = View.VISIBLE
        }
        else
        {
            dishLinearLayout.visibility = View.INVISIBLE
        }
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
                            getDish()
                        }

                    }
                }
                catch (e: Exception)
                {
                    progressDialog.dismiss()
                    finish()
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
                if(dishName.isEnabled)
                {
                    amountDialog(position)
                }
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                if(dishName.isEnabled)
                {
                    listIngredients.removeAt(position)
                    runOnUiThread()
                    {
                        mViewIngredient.notifyDataSetChanged()
                    }
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

        dialogTitulo.text = contextTmp.getString(R.string.dish_add_ingredient_title)

        dialogText.setText(listIngredients[position].existencia.toString())

        dialogAceptar.setOnClickListener {
            if(dialogText.length() > 0 && dialogText.text.toString() != ".")
            {
                listIngredients[position].existencia =
                    Double.parseDouble(dialogText.text.toString())

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

    private fun showDialogDeleteDish() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogTextEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogTextAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogTextCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTextTitulo) as TextView

        dialogTitulo.text = getString(R.string.dish_delete)
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

        val url = urls.url+urls.endPointDishes.endPointBajaPlatillo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("idPlatillo", idDish)
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
                }
                finally
                {
                    progressDialog.dismiss()
                }
            }
        })
    }

    private fun updateDish(dishesObj: DishesObj)
    {
        val errores = Errores()

        val url = urls.url+urls.endPointDishes.endPointActualizarPlatillos

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(dishesObj)

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
                }
                finally
                {
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

    fun ImageView.loadUrl(url: String) {
        try {
            Picasso.with(context).load(url).noFade().into(this)}
        catch(e: Exception){}
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun getIngredient(ingredientObj: IngredientObj) {

        if(!ingredientAlreadyInDish(ingredientObj))
        {
            listIngredients.add(ingredientObj)
        }

        runOnUiThread()
        {
            mViewIngredient.notifyDataSetChanged()
        }
    }

    private fun ingredientAlreadyInDish(ingredientObj: IngredientObj): Boolean {
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

}