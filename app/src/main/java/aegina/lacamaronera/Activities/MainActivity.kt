package aegina.lacamaronera.Activities

import aegina.lacamaronera.R
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
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
        var loginEmail: TextView = findViewById(R.id.loginEmail)
        var loginPassword:TextView = findViewById(R.id.loginPassword)
        var loginLogin:TextView = findViewById(R.id.loginLogin)

        loginLogin.setOnClickListener()
        {
            logIn()
        }

    }

    private fun logIn()
    {
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
    }
}