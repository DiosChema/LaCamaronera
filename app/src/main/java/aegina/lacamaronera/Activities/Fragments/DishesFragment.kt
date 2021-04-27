package aegina.lacamaronera.Activities.Fragments

import aegina.lacamaronera.Activities.Dishes
import aegina.lacamaronera.Activities.Group
import aegina.lacamaronera.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_platillos.*

class DishesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_platillos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        assignResources()
    }

    private fun assignResources() {
        val inventoryAddDish: TextView = inventoryAddDish

        inventoryAddDish.setOnClickListener()
        {
            val intent = Intent(activity, Dishes::class.java)
            startActivity(intent)
        }

        val inventoryAddGroup: TextView = inventoryAddGroup

        inventoryAddGroup.setOnClickListener()
        {
            val intent = Intent(activity, Group::class.java)
            startActivity(intent)
        }
        
    }
}