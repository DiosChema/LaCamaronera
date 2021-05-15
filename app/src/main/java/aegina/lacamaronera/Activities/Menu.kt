package aegina.lacamaronera.Activities

import aegina.lacamaronera.DB.Query
import aegina.lacamaronera.General.GetGlobalClass
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.Pagers.PagerInventory
import aegina.lacamaronera.R
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.collections.ArrayList


class Menu : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener{

    lateinit var drawerLayout: DrawerLayout
    lateinit var progressDialog: ProgressDialog
    lateinit var contextTmp: Context
    lateinit var activityTmp: Context
    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

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
        uploadSales()
    }

    private fun assignResources() {

        contextTmp = this
        activityTmp = this

        val menuSale = findViewById<ImageView>(R.id.menuSale)
        menuSale.setOnClickListener()
        {
            val intent = Intent(this, Sale::class.java)
            startActivity(intent)
        }

        val menuSales = findViewById<ImageView>(R.id.menuSales)
        menuSales.setOnClickListener()
        {
            val intent = Intent(this, Sales::class.java)
            startActivity(intent)
        }

        val menuInventory = findViewById<ImageView>(R.id.menuInventory)
        menuInventory.setOnClickListener()
        {
            val intent = Intent(this, PagerInventory::class.java)
            startActivity(intent)
        }

        val menuAssorment = findViewById<ImageView>(R.id.menuAssorment)
        menuAssorment.setOnClickListener()
        {
            val intent = Intent(this, Assorment::class.java)
            startActivity(intent)
        }

        val menuAssorments = findViewById<ImageView>(R.id.menuAssorments)
        menuAssorments.setOnClickListener()
        {
            val intent = Intent(this, Assorments::class.java)
            startActivity(intent)
        }

    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    private fun uploadSales()
    {
        val query = Query()

        val sales = query.getLocalSales(this)
        if(sales.isNotEmpty())
        {
            addDishes(sales)
        }
    }

    private fun addDishes(sales: ArrayList<SaleObj>)
    {
        val urls = Urls()
        val errores = Errores()

        val url = globalVariable.user!!.url+urls.endPointSale.endPointPostSales

        val dishesTmp = UploadDishesObj(sales, globalVariable.user!!.token)
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(dishesTmp)

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonTutPretty)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException)
            {
                progressDialog.dismiss()
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
                                val query = Query()
                                query.cleanDishes(contextTmp)
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarErrorMensaje(contextTmp, activityTmp as Activity, respuesta)
                        }
                    }
                }
                catch(e: Exception)
                {
                    errores.procesarError(contextTmp, activityTmp as Activity)
                }
                finally
                {
                    progressDialog.dismiss()
                }
            }
        })
    }

    private fun draweMenu()
    {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.menu)
        setSupportActionBar(toolbar)

        var navigationView: NavigationView = findViewById(R.id.navigation_view)
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

    override fun onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        val drawerMenu = DrawerMenu()

        drawerMenu.menu(item, this)

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}