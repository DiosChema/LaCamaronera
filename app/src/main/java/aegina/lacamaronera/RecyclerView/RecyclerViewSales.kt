package aegina.lacamaronera.RecyclerView

import aegina.lacamaronera.Objetos.*
import aegina.lacamaronera.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewSales : RecyclerView.Adapter<RecyclerViewSales.ViewHolder>() {

    var groups: MutableList<SaleDishObj> = ArrayList()
    lateinit var context: Context
    var selected_position = -1

    fun RecyclerAdapter(listGroups: MutableList<SaleDishObj>, context: Context) {
        this.groups = listGroups
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = groups[position]
        holder.itemView.setBackgroundColor(if(selected_position == position) holder.itemView.context.resources.getColor(R.color.light_gray) else Color.TRANSPARENT)

        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val inflatedView : View = layoutInflater.inflate(R.layout.item_assorments, parent,false)

        return ViewHolder(inflatedView)

        //return ViewHolder(layoutInflater.inflate(R.layout.item_assorments,parent,false))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {return groups.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemAssormentsDate = view.findViewById(R.id.itemAssormentsDate) as TextView
        var itemAssormentsHour = view.findViewById(R.id.itemAssormentsHour) as TextView
        var itemAssormentTotal = view.findViewById(R.id.itemAssormentTotal) as TextView
        var itemAssormentRecyclerView = view.findViewById(R.id.itemAssormentRecyclerView) as RecyclerView
        var itemAssormentView = view.findViewById(R.id.itemAssormentView) as LinearLayout

        var simpleDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var simpleDateHours: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

        fun bind(articulo: SaleDishObj) {

            if(itemView.resources.getBoolean(R.bool.portrait_only))
            {
                val mViewVentas = RecyclerViewSaleDish()
                val mRecyclerView : RecyclerView = itemAssormentRecyclerView
                mRecyclerView.setHasFixedSize(true)
                mRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                var llenarRecyclerView = true

                itemView.setOnClickListener {
                    if(llenarRecyclerView)
                    {
                        if (itemView.context != null) {
                            mViewVentas.RecyclerAdapter(articulo.platillos.toMutableList(), itemView.context)
                        }
                        mRecyclerView.adapter = mViewVentas
                        llenarRecyclerView = false
                    }

                    if(itemAssormentView.visibility == View.VISIBLE) {
                        hideMenu()
                    } else {
                        showMenu()
                    }
                }
            }

            itemAssormentsDate.text = simpleDate.format(articulo.fecha)
            itemAssormentsHour.text = simpleDateHours.format(articulo.fecha)

            itemAssormentTotal.text = articulo.totalVenta.toString()

        }

        fun showMenu() {
            itemAssormentView.visibility = View.VISIBLE
            itemAssormentView.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null)
        }

        private fun hideMenu() {
            itemAssormentView.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        itemAssormentView.visibility = View.GONE
                    }
                })
            //ventasFragmentRecyclerViewItemsContainer.visibility = View.GONE
        }

    }
}