package aegina.lacamaronera.Activities

import aegina.lacamaronera.DB.Query
import aegina.lacamaronera.Dialog.DialogDish
import aegina.lacamaronera.Dialog.DialogEnterNumber
import aegina.lacamaronera.General.GetGlobalClass
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.*
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Double
import java.lang.Double.parseDouble
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class Sale : AppCompatActivity(),
    DialogDish.DialogDishInt,
    DialogEnterNumber.DialogEnterNumberInt,
    RecyclerViewSale.SaleInt,
    NavigationView.OnNavigationItemSelectedListener{

    lateinit var drawerLayout: DrawerLayout
    lateinit var assormentIngredientsTotal: TextView
    lateinit var assormentIngredientsRecyclerView: RecyclerView
    lateinit var assormentIngredientsListRecyclerView: RecyclerView
    lateinit var assormentIngredientsFinish: Button
    lateinit var assormentIngredientsAdd: Button

    lateinit var progressDialog: ProgressDialog
    lateinit var dialogIngredients: DialogDish
    lateinit var dialogNumber: DialogEnterNumber
    private val urls: Urls = Urls()

    lateinit var contextTmp: Context
    lateinit var activityTmp: Context

    lateinit var mViewIngredient: RecyclerViewSale
    lateinit var recyclerViewIngredients: RecyclerViewDishesTablet
    var listIngredients: MutableList<DishesObj> = ArrayList()
    var listIngredientsTablet: MutableList<DishesObj> = ArrayList()

    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assorment)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        val getGlobalClass = GetGlobalClass()
        globalVariable = getGlobalClass.globalClass(applicationContext)

        draweMenu()
        assignResources()
        createProgressDialog()
        updateAmountPrices()
    }

    private fun assignResources() {
        contextTmp = this
        activityTmp = this

        assormentIngredientsRecyclerView = findViewById(R.id.assormentIngredientsRecyclerView)
        assormentIngredientsFinish = findViewById(R.id.assormentIngredientsFinish)
        assormentIngredientsTotal = findViewById(R.id.assormentIngredientsTotal)

        if(resources.getBoolean(R.bool.portrait_only))
        {
            assormentIngredientsAdd = findViewById(R.id.assormentIngredientsAdd)

            dialogIngredients = DialogDish()

            dialogIngredients.createDialog(this, this, globalVariable)

            assormentIngredientsAdd.setOnClickListener()
            {
                dialogIngredients.showDialog()
            }
        }
        else
        {
            val assormentIngredientsList = findViewById<TextView>(R.id.assormentIngredientsList)
            assormentIngredientsList.text = getText(R.string.sale_order)

            assormentIngredientsListRecyclerView = findViewById(R.id.assormentIngredientsListRecyclerView)
            createRecyclerViewIngredients()
        }

        assormentIngredientsFinish.setOnClickListener()
        {
            dialogNumber.showDialog(assormentIngredientsTotal.text.toString())
        }

        createRecyclerView()
    }

    private fun draweMenu()
    {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.menu_sales)
        setSupportActionBar(toolbar)

        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun addAssorment()
    {
        val errores = Errores()

        val listAssorment = ArrayList<DishSaleObj>()

        for(i in 0 until listIngredients.size)
        {
            listAssorment.add(
                DishSaleObj(
                    listIngredients[i].idPlatillo,
                    parseDouble(listIngredients[i].descripcion),
                    listIngredients[i].precio,
                    listIngredients[i].nombre,
                    listIngredients[i].ingredientes
                )
            )
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(Date())

        val saleObj = SaleObj(0, listAssorment, 0.0, currentDate.toString(), globalVariable.user!!.idUsuario, globalVariable.user!!.nombre, globalVariable.user!!.token)

        val url = globalVariable.user!!.url+urls.endPointSale.endPointPostSale

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(saleObj)

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

                runOnUiThread()
                {
                    try
                    {
                        postDatabase(saleObj)
                    }
                    catch (e: Exception){}
                    finally
                    {
                        progressDialog.dismiss()
                    }

                }

            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()

                try
                {
                    if(body != null && body.isNotEmpty())
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, ResponseObj::class.java)

                        if(respuesta.status == 0)
                        {
                            runOnUiThread()
                            {
                                clearScreen()
                            }
                        }
                        else
                        {
                            progressDialog.dismiss()
                            errores.procesarErrorMensaje(contextTmp, activityTmp as Activity, respuesta)
                        }
                    }
                }
                catch(e: Exception)
                {
                    errores.procesarError(contextTmp, activityTmp as Activity)
                    progressDialog.dismiss()
                }
                finally
                {
                    progressDialog.dismiss()
                }
            }
        })
    }

    private fun postDatabase(saleObj: SaleObj) {

        val query = Query()

        if(query.insertSale(contextTmp, saleObj))
        {
            clearScreen()
        }
        else
        {
            val errores = Errores()
            errores.procesarError(contextTmp, activityTmp as Activity)
        }

    }

    private fun clearScreen() {
        listIngredients.clear()
        mViewIngredient.notifyDataSetChanged()
        updateAmountPrices()
    }

    private fun createRecyclerView()
    {
        mViewIngredient = RecyclerViewSale()
        val mRecyclerView = findViewById<RecyclerView>(R.id.assormentIngredientsRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewIngredient.RecyclerAdapter(listIngredients, this, globalVariable)
        mRecyclerView.adapter = mViewIngredient


    }

    private fun createRecyclerViewIngredients()
    {
        recyclerViewIngredients = RecyclerViewDishesTablet()
        val mRecyclerView = findViewById<RecyclerView>(R.id.assormentIngredientsListRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(this, 3, RecyclerView.HORIZONTAL, false)
        recyclerViewIngredients.RecyclerAdapter(listIngredientsTablet, this, globalVariable)
        mRecyclerView.adapter = recyclerViewIngredients

        dialogNumber = DialogEnterNumber()
        dialogNumber.createDialog(this,this)

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                //dialogNumber.showDialog(position)
                val ingredientObj = listIngredientsTablet[position]

                ingredientObj.descripcion = "1.0"

                listIngredients.add(ingredientObj)

                runOnUiThread()
                {
                    updateAmountPrices()
                    mViewIngredient.notifyDataSetChanged()
                }
            }

            override fun onLongItemClick(view: View?, position: Int){}
        }))

        getDishes()
    }

    private fun amountDialog(position: Int) {
        val dialog = Dialog(activityTmp)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_assorment)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogAssormentAmount = dialog.findViewById(R.id.dialogAssormentAmount) as EditText
        val dialogAssormentPrice = dialog.findViewById(R.id.dialogAssormentPrice) as EditText
        val dialogAssormentDescription = dialog.findViewById(R.id.dialogAssormentDescription) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogNumberAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogNumberCancelar) as Button
        val dialogAssormentTitulo = dialog.findViewById(R.id.dialogAssormentTitulo) as TextView

        dialogAssormentDescription.visibility = View.GONE
        dialogAssormentTitulo.text = contextTmp.getString(R.string.dialog_assorment_title)

        dialogAssormentAmount.setText(listIngredients[position].descripcion)
        dialogAssormentPrice.setText(listIngredients[position].precio.toString())

        dialogAceptar.setOnClickListener {
            if(dialogAssormentAmount.length() > 0 && dialogAssormentAmount.text.toString() != "." &&
                dialogAssormentPrice.length() > 0 && dialogAssormentPrice.text.toString() != ".")
            {
                listIngredients[position].descripcion =
                    dialogAssormentAmount.text.toString()
                listIngredients[position].precio =
                    Double.parseDouble(dialogAssormentPrice.text.toString())

                dialog.dismiss()
                runOnUiThread()
                {
                    mViewIngredient.notifyDataSetChanged()
                    updateAmountPrices()
                }
            }
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateAmountPrices()
    {
        var pricesTmp = 0.0

        for(i in 0 until listIngredients.size)
        {
            pricesTmp += (parseDouble(listIngredients[i].descripcion) * listIngredients[i].precio)
        }

        assormentIngredientsTotal.text = pricesTmp.toString()
    }

    private fun ingredientAlreadyInDish(dishSaleObj: DishesObj): Boolean
    {
        var i = 0
        for(i in 0 until  listIngredients.size)
        {
            if(dishSaleObj.idPlatillo == listIngredients[i].idPlatillo)
            {
                listIngredients[i].descripcion = (parseDouble(listIngredients[i].descripcion) + parseDouble(dishSaleObj.descripcion)).toString()
                return true
            }
        }
        return false
    }

    fun getDishes()
    {
        val url = globalVariable.user!!.url+urls.endPointDishes.endPointConsultarPlatillos

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

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread()
                {
                    getLocalDishes()
                    //Toast.makeText(contextTmp, contextTmp.getString(R.string.error), Toast.LENGTH_LONG).show()
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
                        val model = gson.fromJson(body, Array<DishesObj>::class.java).toList()

                        listIngredientsTablet.clear()
                        for(ingredientTmp : DishesObj in model)
                        {
                            listIngredientsTablet.add(ingredientTmp)
                        }

                        runOnUiThread()
                        {
                            recyclerViewIngredients.notifyDataSetChanged()
                            insertDishes(listIngredientsTablet)
                        }

                    }
                }
                catch (e: Exception){}
            }
        })

    }

    private fun getLocalDishes() {
        val query = Query()

        val dishesTmp = query.getDishes(this)

        listIngredientsTablet.clear()

        for(dishesObjTmp: DishesObj in dishesTmp)
        {
            listIngredientsTablet.add(dishesObjTmp)
        }

        recyclerViewIngredients.notifyDataSetChanged()
    }

    private fun insertDishes(dishesTmp: MutableList<DishesObj>)
    {
        val query = Query()

        query.insertDishes(contextTmp, dishesTmp)
    }

    override fun getDish(dishSaleObj: DishesObj)
    {

        listIngredients.add(dishSaleObj)

        runOnUiThread()
        {
            updateAmountPrices()
            mViewIngredient.notifyDataSetChanged()
        }
    }

    /*override fun getNumber(number: kotlin.Double, position: Int) {
        val ingredientObj = listIngredientsTablet[position]

        ingredientObj.descripcion = number.toString()

        if(!ingredientAlreadyInDish(ingredientObj))
        {
            listIngredients.add(ingredientObj)
        }

        runOnUiThread()
        {
            updateAmountPrices()
            mViewIngredient.notifyDataSetChanged()
        }
    }*/

    override fun actualizarNumero(value: Int, position: Int) {
        val dishesObj = DishesObj(
            listIngredients[position].idPlatillo,
            listIngredients[position].nombre,
            listIngredients[position].precio,
            listIngredients[position].ingredientes,
            listIngredients[position].idFamilia,
            (parseDouble(listIngredients[position].descripcion) + value).toString(),
            listIngredients[position].token
        )

        listIngredients[position] = dishesObj

        if(parseDouble(listIngredients[position].descripcion) <= 0.0)
        {
            listIngredients.removeAt(position)
        }

        runOnUiThread()
        {
            mViewIngredient.notifyDataSetChanged()
            updateAmountPrices()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerMenu = DrawerMenu()

        drawerMenu.menu(item, this, this)

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun finishSale() {
        addAssorment()
    }

}