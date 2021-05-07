package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Objetos.AssormentIngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerViewAssormentIngredient : RecyclerView.Adapter<RecyclerViewAssormentIngredient.ViewHolder>() {

    var groups: MutableList<AssormentIngredientObj> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(listGroups: MutableList<AssormentIngredientObj>, context: Context) {
        this.groups = listGroups
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groups[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_ingredients_historial,parent,false))
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
        var itemAssormentIngredientPhoto = view.findViewById(R.id.itemIngredientHistorialPhoto) as ImageView
        var itemAssormentIngredientPrice = view.findViewById(R.id.itemIngredientHistorialPrice) as TextView
        var itemAssormentIngredientName = view.findViewById(R.id.itemIngredientHistorialName) as TextView
        var itemAssormentIngredientTotal = view.findViewById(R.id.itemIngredientHistorialTotal) as TextView

        fun bind(articulo: AssormentIngredientObj) {
            itemAssormentIngredientName.text = articulo.nombre
            val textTmp = articulo.cantidad.toString() + " x $" + articulo.precio
            itemAssormentIngredientPrice.text = textTmp
            itemAssormentIngredientTotal.text = (articulo.cantidad * articulo.precio).toString()
            val url = url.url + url.endPointsImagenes.endPointObtenerImagen + "in" + articulo.idIngrediente+".jpeg"
            itemAssormentIngredientPhoto.loadUrl(url)
        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

    }
}