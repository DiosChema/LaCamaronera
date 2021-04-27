package aegina.lacamaronera.Activities

import aegina.lacamaronera.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import java.util.ArrayList

class Dishes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dishes)

        assignResources()

    }

    private fun assignResources() {
        var dishName: TextView = findViewById(R.id.dishName)
        var dishPrice: TextView = findViewById(R.id.dishPrice)
        var dishDescription: TextView = findViewById(R.id.dishDescription)
        var dishGroup: Spinner = findViewById(R.id.dishGroup)
        var dishAdd: Button = findViewById(R.id.dishAdd)

        var listaFamilia:ArrayList<String> = ArrayList()

        listaFamilia.add("Frio")
        listaFamilia.add("Caliente")

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, listaFamilia)
        dishGroup.adapter = adapter

        dishAdd.setOnClickListener()
        {
            addDish()
        }


    }

    private fun addDish()
    {

    }
}