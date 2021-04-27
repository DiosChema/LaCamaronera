package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Objetos.GroupObj
import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerViewIngredientes : RecyclerView.Adapter<RecyclerViewIngredientes.ViewHolder>() {

    var groups: MutableList<IngredientObj> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(listGroups: MutableList<IngredientObj>, context: Context) {
        this.groups = listGroups
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groups[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_ingredients,parent,false))
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
        var itemIngredientImage = view.findViewById(R.id.itemIngredientImage) as ImageView
        var itemIngredientName = view.findViewById(R.id.itemIngredientName) as TextView
        var itemIngredientDescription = view.findViewById(R.id.itemIngredientDescription) as TextView
        var itemIngredientAmount = view.findViewById(R.id.itemIngredientAmount) as TextView
        var itemIngredientPrice = view.findViewById(R.id.itemIngredientPrice) as TextView

        fun bind(articulo: IngredientObj) {
            itemIngredientName.text = articulo.nombre
            itemIngredientDescription.text = articulo.descripcion
            itemIngredientAmount.text = articulo.existencia.toString()
            itemIngredientPrice.text = articulo.costo.toString()
            val url = url.url + url.endPointsImagenes.endPointObtenerImagen + "in" + articulo.idIngrediente+".jpeg"
            itemIngredientImage.loadUrl(url)
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