package aegina.lacamaronera.Activities

import aegina.lacamaronera.DB.Query
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    private val urls: Urls = Urls()
    lateinit var loginEmail: TextView
    lateinit var loginPassword:TextView
    lateinit var contextTmp: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        assignResources()
    }

    private fun assignResources() {
        contextTmp = this

        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        var loginLogin:TextView = findViewById(R.id.loginLogin)

        loginLogin.setOnClickListener()
        {
            logIn()
        }

    }

    private fun logIn()
    {
        var email = loginEmail.text.toString()
        email = email.toLowerCase(Locale.ROOT)
        val password = loginPassword.text.toString()

        if(!email.isEmailValid())
        {
            Toast.makeText(this, getString(R.string.message_invalid_email), Toast.LENGTH_SHORT).show()
            return
        }

        if(password.length < 8)
        {
            Toast.makeText(this, getString(R.string.message_invalid_password), Toast.LENGTH_SHORT).show()
            return
        }

        val url = urls.url+urls.endPointUser.endPointGetUser

        val jsonObject = JSONObject()
        try
        {
            jsonObject.put("user", email)
            jsonObject.put("password", password)
            loginPassword.text = ""
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.wait))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback
        {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()

                runOnUiThread()
                {
                    if(getUser(contextTmp, email, password))
                    {
                        val intent = Intent(contextTmp, Menu::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(contextTmp, getString(R.string.error), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response)
            {
                val json = response.body()!!.string()
                val gson = GsonBuilder().create()

                try
                {
                    val respuesta = gson.fromJson(json, LoginUser::class.java)

                    if(respuesta.status == 0)
                    {
                        insertUser(contextTmp, respuesta.usuario, password)

                        val globalVariable = applicationContext as GlobalClass
                        globalVariable.user = respuesta.usuario

                        val intent = Intent(contextTmp, Menu::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        runOnUiThread()
                        {
                            Toast.makeText(contextTmp, getString(R.string.invalid), Toast.LENGTH_LONG).show()
                        }
                    }
                }
                catch (e: Exception)
                {
                    val perro = 1
                }

                runOnUiThread()
                {
                    progressDialog.dismiss()
                }

            }
        })

    }

    fun insertUser(
        contextTmp: Context,
        user: User,
        password: String
    )
    {
        val query = Query()

        query.insertUser(contextTmp, user, password)
    }

    fun getUser(
        contextTmp: Context,
        email: String,
        password: String
    ): Boolean
    {
        val query = Query()

        val user = query.getUser(contextTmp, email, password)

        return user.idEmpleado != -1
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}