package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Objetos.IngredientObj
import aegina.lacamaronera.Objetos.Urls
import aegina.lacamaronera.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.lang.Double

class RecyclerViewAssorment : RecyclerView.Adapter<RecyclerViewAssorment.ViewHolder>() {

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
        return ViewHolder(layoutInflater.inflate(R.layout.item_assorment_ingredient,parent,false))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {return groups.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val saleInt = view.context as AssormentInt
        val url = Urls()
        var itemAssormentIngredientPhoto = view.findViewById(R.id.itemAssormentIngredientPhoto) as ImageView
        var itemAssormentIngredientName = view.findViewById(R.id.itemAssormentIngredientName) as TextView
        var itemAssormentIngredientPrice = view.findViewById(R.id.itemAssormentIngredientPrice) as TextView
        var itemAssormentIngredientTotal = view.findViewById(R.id.itemAssormentIngredientTotal) as TextView
        var itemAssormentIngredientAmount = view.findViewById(R.id.itemAssormentIngredientAmount) as TextView
        var buttonMore = view.findViewById(R.id.buttonMore) as Button
        var buttonLess = view.findViewById(R.id.buttonLess) as Button

        fun bind(articulo: IngredientObj) {
            itemAssormentIngredientName.text = articulo.nombre
            itemAssormentIngredientPrice.text = articulo.existencia.toString() + " X $" + articulo.costo.toString()
            itemAssormentIngredientTotal.text = (articulo.existencia * articulo.costo).toString()
            val url = url.url + url.endPointsImagenes.endPointObtenerImagen + "in" + articulo.idIngrediente+".jpeg"
            itemAssormentIngredientAmount.text = articulo.existencia.toString()
            itemAssormentIngredientPhoto.loadUrl(url)

            buttonMore.setOnClickListener()
            {
                saleInt.actualizarNumero(1, adapterPosition)
            }

            buttonLess.setOnClickListener()
            {
                if(articulo.existencia >= 1)
                    saleInt.actualizarNumero(-1, adapterPosition)
            }
        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }
    }

    interface AssormentInt {
        fun actualizarNumero(value: Int, position: Int)
    }
}