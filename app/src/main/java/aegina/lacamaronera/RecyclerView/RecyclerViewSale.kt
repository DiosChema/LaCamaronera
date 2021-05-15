package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.DishesObj
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
import java.lang.Double.parseDouble

class RecyclerViewSale : RecyclerView.Adapter<RecyclerViewSale.ViewHolder>() {

    var groups: MutableList<DishesObj> = ArrayList()
    lateinit var context: Context
    lateinit var globalClass: GlobalClass

    fun RecyclerAdapter(listGroups: MutableList<DishesObj>, context: Context, globalClass: GlobalClass) {
        this.groups = listGroups
        this.context = context
        this.globalClass = globalClass
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groups[position]
        holder.bind(item, globalClass)
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

        val saleInt = view.context as SaleInt
        val url = Urls()
        var itemAssormentIngredientPhoto = view.findViewById(R.id.itemAssormentIngredientPhoto) as ImageView
        var itemAssormentIngredientName = view.findViewById(R.id.itemAssormentIngredientName) as TextView
        var itemAssormentIngredientPrice = view.findViewById(R.id.itemAssormentIngredientPrice) as TextView
        var itemAssormentIngredientTotal = view.findViewById(R.id.itemAssormentIngredientTotal) as TextView
        var itemAssormentIngredientAmount = view.findViewById(R.id.itemAssormentIngredientAmount) as TextView
        var buttonMore = view.findViewById(R.id.buttonMore) as Button
        var buttonLess = view.findViewById(R.id.buttonLess) as Button

        fun bind(articulo: DishesObj, globalVariable: GlobalClass) {
            itemAssormentIngredientName.text = articulo.nombre
            itemAssormentIngredientPrice.text = articulo.descripcion + " X $" + articulo.precio.toString()
            itemAssormentIngredientTotal.text = (parseDouble(articulo.descripcion) * articulo.precio).toString()
            val url = globalVariable.user!!.url + url.endPointsImagenes.endPointObtenerImagen + "pl" + articulo.idPlatillo+ ".jpeg&token="+ globalVariable.user!!.token
            itemAssormentIngredientAmount.text = articulo.descripcion
            itemAssormentIngredientPhoto.loadUrl(url)

            buttonMore.setOnClickListener()
            {
                saleInt.actualizarNumero(1, adapterPosition)
            }

            buttonLess.setOnClickListener()
            {
                if(parseDouble(articulo.descripcion) >= 1)
                saleInt.actualizarNumero(-1, adapterPosition)
            }
        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

    }

    interface SaleInt {
        fun actualizarNumero(value: Int, position: Int)
    }
}