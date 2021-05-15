package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Activities.IngredientsDetails
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

class RecyclerViewDishesTablet : RecyclerView.Adapter<RecyclerViewDishesTablet.ViewHolder>() {

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
        return ViewHolder(layoutInflater.inflate(R.layout.item_ingredient_tablet,parent,false))
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
        var itemIngredientTablePhoto = view.findViewById(R.id.itemIngredientTablePhoto) as ImageView
        var itemIngredientTableName = view.findViewById(R.id.itemIngredientTableName) as TextView

        fun bind(articulo: DishesObj, globalVariable: GlobalClass) {
            itemIngredientTableName.text = articulo.nombre
            val url = globalVariable.user!!.url + url.endPointsImagenes.endPointObtenerImagen + "pl" + articulo.idPlatillo+ ".jpeg&token="+ globalVariable.user!!.token
            itemIngredientTablePhoto.loadUrl(url)
        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

    }
}