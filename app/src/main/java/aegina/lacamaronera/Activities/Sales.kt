package aegina.lacamaronera.Activities

import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.*
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat

class Sales : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener{

    lateinit var drawerLayout: DrawerLayout
    lateinit var mViewAssorments: RecyclerViewSales
    lateinit var progressDialog: ProgressDialog
    lateinit var mViewAssormentsItems: RecyclerViewSaleDish

    lateinit var assormentsTitle: TextView
    lateinit var assormentsDate: TextView
    lateinit var assormentsTotal: TextView

    private val urls: Urls = Urls()
    lateinit var contextTmp: Context
    lateinit var activityTmp: Activity
    var listAssormentsIngredient = ArrayList<DishSaleObj>()

    var listAssorments = ArrayList<SaleDishObj>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assorments)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        draweMenu()
        assignResources()
        createProgressDialog()
        getSales()
    }

    private fun assignResources() {
        contextTmp = this
        activityTmp = this

        createRecyclerView()
    }

    private fun createRecyclerView()
    {
        mViewAssorments = RecyclerViewSales()
        val mRecyclerView = findViewById<RecyclerView>(R.id.assormentsRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(contextTmp)
        mViewAssorments.RecyclerAdapter(listAssorments, contextTmp)
        mRecyclerView.adapter = mViewAssorments

        if(!resources.getBoolean(R.bool.portrait_only))
        {
            assormentsTitle = findViewById(R.id.assormentsTitle)
            assormentsDate = findViewById(R.id.assormentsDate)
            assormentsTotal = findViewById(R.id.assormentsTotal)

            mViewAssormentsItems = RecyclerViewSaleDish()
            val mRecyclerViewIngedient = findViewById<RecyclerView>(R.id.assormentsRecyclerViewItems)
            mRecyclerViewIngedient.setHasFixedSize(true)
            mRecyclerViewIngedient.layoutManager = LinearLayoutManager(contextTmp)
            mViewAssormentsItems.RecyclerAdapter(listAssormentsIngredient, contextTmp)
            mRecyclerViewIngedient.adapter = mViewAssormentsItems

            mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewIngedient, object :
                RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int)
                {
                    runOnUiThread()
                    {
                        mViewAssorments.notifyItemChanged(mViewAssorments.selected_position)
                        mViewAssorments.selected_position = position
                        mViewAssorments.notifyItemChanged(mViewAssorments.selected_position)


                    }

                    fillSale(listAssorments[position])
                }

                override fun onLongItemClick(view: View?, position: Int)
                {
                    fillSale(listAssorments[position])
                }
            }))
        }
    }

    private fun fillSale(saleDishObj: SaleDishObj)
    {
        val simpleDate = SimpleDateFormat("dd/MM/yyyy")
        val simpleDateHours = SimpleDateFormat("HH:mm:ss")

        val assormentsLinearLayout = findViewById<LinearLayout>(R.id.assormentsLinearLayout)

        if(assormentsLinearLayout.visibility == View.INVISIBLE)
        {
            assormentsLinearLayout.visibility = View.VISIBLE
        }
        assormentsTitle.text = "Orden " + saleDishObj.idVenta.toString()
        assormentsDate.text = simpleDateHours.format(saleDishObj.fecha)
        assormentsTotal.text = saleDishObj.totalVenta.toString()

        listAssormentsIngredient.clear()

        for(assormentIngredientObjTmp: DishSaleObj in saleDishObj.platillos)
        {
            listAssormentsIngredient.add(assormentIngredientObjTmp)
        }

        runOnUiThread()
        {
            mViewAssormentsItems.notifyDataSetChanged()
        }


    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    fun getSales()
    {
        val url = urls.url+urls.endPointSale.endPointGetSale

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
                        val model = gson.fromJson(body, Array<SaleDishObj>::class.java).toList()

                        listAssorments.clear()
                        for(assormentTmp : SaleDishObj in model)
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

    private fun draweMenu()
    {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.menu_sale)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerMenu = DrawerMenu()

        drawerMenu.menu(item, this, this)

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}