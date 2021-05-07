package aegina.lacamaronera.Activities

import aegina.lacamaronera.Dialog.DialogDate
import aegina.lacamaronera.Dialog.DialogSearch
import aegina.lacamaronera.Objetos.AssormentIngredientObj
import aegina.lacamaronera.Objetos.AssormentListObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.*
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
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
import java.util.*
import kotlin.collections.ArrayList

class Assorments : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    DialogSearch.DialogSearchInt,
    DialogDate.DialogDateInt{

    lateinit var drawerLayout: DrawerLayout
    lateinit var mViewAssorments: RecyclerViewAssorments
    lateinit var progressDialog: ProgressDialog
    lateinit var mViewAssormentsItems: RecyclerViewAssormentIngredient

    lateinit var assormentsTitle: TextView
    lateinit var assormentsDate: TextView
    lateinit var assormentsTotal: TextView
    lateinit var assormentsSearch: ImageButton

    private val urls: Urls = Urls()
    lateinit var contextTmp: Context
    lateinit var activityTmp: Activity

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    var listAssorments = ArrayList<AssormentListObj>()
    var listAssormentsIngredient = ArrayList<AssormentIngredientObj>()

    lateinit var dialogDate: DialogDate
    lateinit var dialogSearch: DialogSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assorments)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        assignResources()
        createProgressDialog()
        draweMenu()
        createDialogDate()
        createDialogSearch()
        dialogDate.assignInitialsDates()
        getAssorments()
    }

    private fun createDialogSearch() {
        dialogSearch = DialogSearch()
        dialogSearch.createWindow(this, this)
    }

    private fun createDialogDate() {
        dialogDate = DialogDate()
        dialogDate.createWindow(this)
    }

    private fun assignResources() {
        contextTmp = this
        activityTmp = this

        createRecyclerView()
        val assormentsSearch: ImageButton = findViewById(R.id.assormentsSearch)

        assormentsSearch.setOnClickListener()
        {
            dialogSearch.showDialog()
        }
    }

    private fun createRecyclerView()
    {
        mViewAssorments = RecyclerViewAssorments()
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

            mViewAssormentsItems = RecyclerViewAssormentIngredient()
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

    private fun fillSale(assormentListObj: AssormentListObj)
    {
        val simpleDate = SimpleDateFormat("dd/MM/yy HH:mm")
        val simpleDateHours = SimpleDateFormat("HH:mm:ss")

        val assormentsLinearLayout = findViewById<LinearLayout>(R.id.assormentsLinearLayout)

        if(assormentsLinearLayout.visibility == View.INVISIBLE)
        {
            assormentsLinearLayout.visibility = View.VISIBLE
        }
        assormentsTitle.text = assormentListObj.idGasto.toString()
        assormentsDate.text = simpleDate.format(assormentListObj.fecha)
        assormentsTotal.text = assormentListObj.totalGasto.toString()

        listAssormentsIngredient.clear()

        for(assormentIngredientObjTmp: AssormentIngredientObj in assormentListObj.ingredientes)
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

    fun getAssorments()
    {
        val url = urls.url+urls.endPointAssorment.endPointGetAssorment

        val jsonObject = JSONObject()
        try {
            jsonObject.put("fechaInicial", dateFormat.format(dialogSearch.initialDate))
            jsonObject.put("fechaFinal", dateFormat.format(dialogSearch.finalDate))
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
                        val model = gson.fromJson(body, Array<AssormentListObj>::class.java).toList()

                        listAssorments.clear()
                        for(assormentTmp : AssormentListObj in model)
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
        toolbar.setTitle(R.string.menu_assorments)
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

    override fun getInitialDate() {
        dialogSearch.assignInitialText(dialogDate.getInitialDateText())
        dialogSearch.initialDate = dialogDate.fechaInicial
    }

    override fun getFinalDate() {
        dialogSearch.assignFinalText(dialogDate.getFinalDateText())
        dialogSearch.finalDate = dialogDate.fechaFinal
    }

    override fun getDate() {
        dialogSearch.assignInitialText(dialogDate.getInitialDateText())
        dialogSearch.assignFinalText(dialogDate.getFinalDateText())

        dialogSearch.initialDate = dialogDate.fechaInicial
        dialogSearch.finalDate = dialogDate.fechaFinal
    }

    override fun updateInitialDate() {
        dialogDate.abrirDialogFechaInicial(dialogSearch.initialDate, dialogSearch.finalDate)
    }

    override fun updateFinalDate() {
        dialogDate.abrirDialogFechaFinal(dialogSearch.initialDate, dialogSearch.finalDate)
    }

    override fun search() {
        getAssorments()
    }

}