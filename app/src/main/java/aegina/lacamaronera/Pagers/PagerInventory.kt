package aegina.lacamaronera.Pagers

import aegina.lacamaronera.Fragments.DishesFragment
import aegina.lacamaronera.Fragments.IngredientsFragment
import aegina.lacamaronera.R
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_pager_inventory.*

class PagerInventory : AppCompatActivity() {

    lateinit var adapter : MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_inventory)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        asignarFragments()
    }

    fun asignarFragments()
    {
        adapter = MyViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(DishesFragment(), getString(R.string.inventory_dish))
        adapter.addFragment(IngredientsFragment(), getString(R.string.inventory_ingredients))
        pagerInventoryPager.adapter = adapter
        pagerInventoryTabLayout.setupWithViewPager(pagerInventoryPager)
    }

    class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager)
    {
        private val fragmentList : MutableList<Fragment> = ArrayList()
        private val titleList : MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title:String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

    }
}