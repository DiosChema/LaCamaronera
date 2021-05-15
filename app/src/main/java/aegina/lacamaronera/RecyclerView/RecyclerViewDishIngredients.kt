package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Activities.DishesDetails
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.DishesObj
import aegina.lacamaronera.Objetos.IngredientObj
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

class RecyclerViewDishIngredients : RecyclerView.Adapter<RecyclerViewDishIngredients.ViewHolder>() {

    var groups: MutableList<IngredientObj> = ArrayList()
    lateinit var context: Context
    lateinit var globalClass: GlobalClass

    fun RecyclerAdapter(listGroups: MutableList<IngredientObj>, context: Context, globalClass: GlobalClass) {
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
        return ViewHolder(layoutInflater.inflate(R.layout.item_dish_ingredient,parent,false))
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
        var itemDishIngredientPhoto = view.findViewById(R.id.itemDishIngredientPhoto) as ImageView
        var itemDishIngredientAmount = view.findViewById(R.id.itemDishIngredientAmount) as TextView

        fun bind(articulo: IngredientObj, globalVariable: GlobalClass) {
            val textTmp = articulo.existencia.toString() + " " + articulo.unidad
            itemDishIngredientAmount.text = textTmp
            val url = globalVariable.user!!.url + url.endPointsImagenes.endPointObtenerImagen + "in" + articulo.idIngrediente+ ".jpeg&token="+ globalVariable.user!!.token
            itemDishIngredientPhoto.loadUrl(url)
        }

        private fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

    }
}