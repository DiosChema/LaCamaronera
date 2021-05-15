package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Activities.ServicesDetails
import aegina.lacamaronera.Activities.ServicesExpense
import aegina.lacamaronera.General.GlobalClass
import aegina.lacamaronera.Objetos.ServiceObj
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
import java.text.SimpleDateFormat

class RecyclerViewServices : RecyclerView.Adapter<RecyclerViewServices.ViewHolder>() {

    var groups: MutableList<ServiceObj> = ArrayList()
    lateinit var context: Context
    lateinit var globalClass: GlobalClass

    fun RecyclerAdapter(listGroups: MutableList<ServiceObj>, context: Context, globalClass: GlobalClass) {
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
        return ViewHolder(layoutInflater.inflate(R.layout.item_expense,parent,false))
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

        fun bind(articulo: ServiceObj, globalVariable: GlobalClass) {

            itemIngredientName.text = articulo.nombre
            itemIngredientDescription.text = articulo.descripcion
            val url = globalVariable.user!!.url + url.endPointsImagenes.endPointObtenerImagen + "se" + articulo.idServicio+ ".jpeg&token="+ globalVariable.user!!.token
            itemIngredientImage.loadUrl(url)

            itemView.setOnClickListener()
            {
                val intent = Intent(itemView.context, ServicesDetails::class.java).apply {
                    putExtra("idServicio", articulo.idServicio)
                }
                itemView.context.startActivity(intent)
            }

            itemView.setOnLongClickListener()
            {
                val intent = Intent(itemView.context, ServicesExpense::class.java).apply {
                    putExtra("service", articulo)
                }
                itemView.context.startActivity(intent)
                true
            }
        }

        fun ImageView.loadUrl(url: String) {
            try {
                Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

    }
}