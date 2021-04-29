package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Activities.DishesDetails
import aegina.lacamaronera.Objetos.DishesObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerViewDishes : RecyclerView.Adapter<RecyclerViewDishes.ViewHolder>() {

    var groups: MutableList<DishesObj> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(listGroups: MutableList<DishesObj>, context: Context) {
        this.groups = listGroups
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groups[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_dishes,parent,false))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {return groups.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val url = Urls()
        var itemIngredientImage = view.findViewById(R.id.itemDishImage) as ImageView
        var itemIngredientName = view.findViewById(R.id.itemDishName) as TextView
        var itemIngredientDescription = view.findViewById(R.id.itemDishDescription) as TextView
        var itemIngredientPrice = view.findViewById(R.id.itemDishPrice) as TextView

        fun bind(articulo: DishesObj) {
            itemIngredientName.text = articulo.nombre
            itemIngredientDescription.text = articulo.descripcion
            itemIngredientPrice.text = articulo.precio.toString()
            val url = url.url + url.endPointsImagenes.endPointObtenerImagen + "pl" + articulo.idPlatillo+".jpeg"
            itemIngredientImage.loadUrl(url)

            itemView.setOnClickListener()
            {
                val intent = Intent(itemView.context, DishesDetails::class.java).apply {
                    putExtra("idDish", articulo.idPlatillo)
                }
                itemView.context.startActivity(intent)
            }
        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

        fun Double.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return kotlin.math.round(this * multiplier) / multiplier
        }
    }
}