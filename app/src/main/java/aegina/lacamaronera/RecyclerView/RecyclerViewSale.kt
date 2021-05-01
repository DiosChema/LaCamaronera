package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Objetos.DishesObj
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
import java.lang.Double.parseDouble

class RecyclerViewSale : RecyclerView.Adapter<RecyclerViewSale.ViewHolder>() {

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
        return ViewHolder(layoutInflater.inflate(R.layout.item_assorment,parent,false))
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
        var itemAssormentPhoto = view.findViewById(R.id.itemAssormentPhoto) as ImageView
        var itemAssormentName = view.findViewById(R.id.itemAssormentName) as TextView
        var itemAssormentPrice = view.findViewById(R.id.itemAssormentPrice) as TextView
        var itemAssormentAmount = view.findViewById(R.id.itemAssormentAmount) as TextView
        var itemAssormentTotal = view.findViewById(R.id.itemAssormentTotal) as TextView

        fun bind(articulo: DishesObj) {
            itemAssormentName.text = articulo.nombre
            itemAssormentAmount.text = articulo.descripcion
            itemAssormentPrice.text = articulo.precio.toString()
            itemAssormentTotal.text = (parseDouble(articulo.descripcion) * articulo.precio).toString()
            val url = url.url + url.endPointsImagenes.endPointObtenerImagen + "pl" + articulo.idPlatillo+".jpeg"
            itemAssormentPhoto.loadUrl(url)

        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

    }
}