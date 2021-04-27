package aegina.lacamaronera.Activities

import aegina.lacamaronera.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_ingredients.*
import kotlinx.android.synthetic.main.fragment_platillos.*

class IngredientsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        assignResources()
    }

    private fun assignResources() {
        val ingredientsFragment: TextView = IngredientsAddDish

        ingredientsFragment.setOnClickListener()
        {
            val intent = Intent(activity, Dishes::class.java)
            startActivity(intent)
        }

    }
}