package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.ExpenseServiceObj
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
import java.text.SimpleDateFormat

class RecyclerViewExpenseServices : RecyclerView.Adapter<RecyclerViewExpenseServices.ViewHolder>() {

    var groups: MutableList<ExpenseServiceObj> = ArrayList()
    lateinit var context: Context
    lateinit var globalClass: GlobalClass

    fun RecyclerAdapter(listGroups: MutableList<ExpenseServiceObj>, context: Context, globalClass: GlobalClass) {
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

        var simpleDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var simpleDateHours: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

        fun bind(articulo: ExpenseServiceObj, globalVariable: GlobalClass) {

            itemAssormentIngredientName.text = articulo.nombre
            itemAssormentIngredientPrice.text = simpleDate.format(articulo.fecha) + "  " + simpleDateHours.format(articulo.fecha)
            itemAssormentIngredientTotal.text = articulo.gasto.toString()
            val url = globalVariable.user!!.url + url.endPointsImagenes.endPointObtenerImagen + "se" + articulo.idServicio+ ".jpeg&token="+ globalVariable.user!!.token
            itemAssormentIngredientPhoto.loadUrl(url)
        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

    }
}