package aegina.lacamaronera.Pagers

import aegina.lacamaronera.Fragments.DishesFragment
import aegina.lacamaronera.Fragments.ExpensesFragment
import aegina.lacamaronera.Fragments.ExpensesHisFragment
import aegina.lacamaronera.Fragments.IngredientsFragment
import aegina.lacamaronera.R
import aegina.lacamaronera.RecyclerView.RecyclerViewSale
import aegina.lacamaronera.RecyclerView.RecyclerViewServices
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_pager_inventory.*


class PagerExpenses : AppCompatActivity(){

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
        adapter.addFragment(ExpensesFragment(), getString(R.string.expenses))
        adapter.addFragment(ExpensesHisFragment(), getString(R.string.expenses_his))
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