package aegina.lacamaronera.Activities

import aegina.lacamaronera.Pagers.PagerExpenses
import aegina.lacamaronera.Pagers.PagerInventory
import aegina.lacamaronera.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem

class DrawerMenu
{
    fun menu(item: MenuItem, contextTmp: Context)
    {
        val i = Intent()
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        when (item.itemId) {
            R.id.drawerMenuSale -> {
                i.setClass(contextTmp, Sale::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuSales -> {
                i.setClass(contextTmp, Sales::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuStock -> {
                i.setClass(contextTmp, PagerInventory::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuAssorment -> {
                i.setClass(contextTmp, Assorment::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuAssorments -> {
                i.setClass(contextTmp, Assorments::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuExpenses -> {
                i.setClass(contextTmp, PagerExpenses::class.java)
                contextTmp.startActivity(i)
            }
        }
    }

    fun menu(item: MenuItem, contextTmp: Context, activityTmp: Activity)
    {
        val i = Intent()
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        when (item.itemId) {
            R.id.drawerMenuSale -> {
                i.setClass(contextTmp, Sale::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuSales -> {
                i.setClass(contextTmp, Sales::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuStock -> {
                i.setClass(contextTmp, PagerInventory::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuAssorment -> {
                i.setClass(contextTmp, Assorment::class.java)
                contextTmp.startActivity(i)
            }
            R.id.drawerMenuAssorments -> {
                i.setClass(contextTmp, Assorments::class.java)
                contextTmp.startActivity(i)
            }
        }

        activityTmp.finish()
    }
}