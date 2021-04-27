package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Objetos.GroupObj
import aegina.lacamaronera.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlin.math.round

class RecyclerViewGrupos : RecyclerView.Adapter<RecyclerViewGrupos.ViewHolder>() {

    var groups: MutableList<GroupObj> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(listGroups: MutableList<GroupObj>, context: Context) {
        this.groups = listGroups
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groups.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_group,parent,false))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateReceiptsList(newlist: ArrayList<GroupObj>) {
        groups.clear()
        groups.addAll(newlist)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {return groups.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemGroupImage = view.findViewById(R.id.itemGroupImage) as ImageView
        var itemGroupName = view.findViewById(R.id.itemGroupName) as TextView

        fun bind(articulo: GroupObj) {
            itemGroupName.text = articulo.nombre
            itemGroupImage.setImageDrawable(itemView.context.getDrawable(R.drawable.logo_camaronera))
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

        fun Double.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return round(this * multiplier) / multiplier
        }
    }
}