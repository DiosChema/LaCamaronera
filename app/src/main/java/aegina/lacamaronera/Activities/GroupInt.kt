package aegina.lacamaronera.Activities

import aegina.lacamaronera.Dialog.DialogGruop
import aegina.lacamaronera.Objetos.Errores
import aegina.lacamaronera.Objetos.GroupObj
import aegina.lacamaronera.Objetos.ResponseObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerItemClickListener
import aegina.lacamaronera.RecyclerView.RecyclerViewGrupos
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.collections.ArrayList

class GroupInt : AppCompatActivity(), DialogGruop.DialogGroupInt {

    lateinit var dialogPhoto: Dialog
    var listGroup: MutableList<GroupObj> = ArrayList()
    private val urls: Urls = Urls()
    lateinit var contextTmp : Context
    lateinit var activityTmp : Activity
    lateinit var progressDialog:ProgressDialog
    lateinit var mViewGroup : RecyclerViewGrupos
    lateinit var mRecyclerView : RecyclerView
    lateinit var dialogGruop : DialogGruop

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        createRecyclerView()
        assignResources()
        createProgressDialog()
        getGroups()
    }

    private fun assignResources()
    {
        contextTmp = this
        activityTmp = this

        var inventoryAddGroup : Button = findViewById(R.id.inventoryAddGroup)

        inventoryAddGroup.setOnClickListener()
        {
            dialogGruop.openGroupDialog(contextTmp, 0, "")
        }

    }

    private fun createRecyclerView()
    {
        mViewGroup = RecyclerViewGrupos()
        mRecyclerView = findViewById(R.id.inventoryGroup)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewGroup.RecyclerAdapter(listGroup, this)
        mRecyclerView.adapter = mViewGroup

        dialogGruop = DialogGruop()
        dialogGruop.createDialogGroup(this)

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(this, mRecyclerView, object :
        RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                dialogGruop.openGroupDialog(contextTmp, position, listGroup[position].nombre)
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                dialogGruop.openGroupDialog(contextTmp, position, listGroup[position].nombre)
            }
        }))

    }

    fun createProgressDialog()
    {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(this.getString(R.string.wait))
        progressDialog.setCancelable(false)
    }

    fun getGroups()
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

                        listGroup.clear()
                        for(groupTmp : GroupObj in model)
                        {
                            listGroup.add(groupTmp)
                        }

                        runOnUiThread()
                        {
                            mViewGroup.notifyDataSetChanged()
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

    override fun newGroup(text: String) {
        val errores = Errores()

        val url = urls.url+urls.endPointsGrupo.endPointAltaGrupo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("nombre", text)
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
                                getGroups()
                            }
                            else
                            {
                                progressDialog.dismiss()
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

    override fun editGroup(text: String, position: Int) {
        val errores = Errores()

        val url = urls.url+urls.endPointsGrupo.endPointActualizarFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("nombre", text)
            jsonObject.put("idFamilia", listGroup[position].idFamilia)
        } catch (e: JSONException)
        {
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
                activityTmp.runOnUiThread()
                {
                    errores.procesarError(contextTmp, activityTmp)
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

                        activityTmp.runOnUiThread()
                        {
                            if(respuesta.status == 0)
                            {
                                getGroups()
                            }
                            else
                            {
                                progressDialog.dismiss()
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

    override fun deleteGroup(position: Int) {
        val errores = Errores()

        val url = urls.url+urls.endPointsGrupo.endPointEliminarFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("idFamilia", listGroup[position].idFamilia)
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
                activityTmp.runOnUiThread()
                {
                    errores.procesarError(contextTmp, activityTmp)
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

                        activityTmp.runOnUiThread()
                        {
                            if(respuesta.status == 0)
                            {
                                getGroups()
                            }
                            else
                            {
                                progressDialog.dismiss()
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

}