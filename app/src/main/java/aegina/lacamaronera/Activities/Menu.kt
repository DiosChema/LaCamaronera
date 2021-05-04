package aegina.lacamaronera.Activities

import aegina.lacamaronera.R
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class Menu : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener{

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        requestedOrientation = if(resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        draweMenu()
        assignResources()
    }

    private fun assignResources() {
        val menuSale = findViewById<ImageView>(R.id.menuSale)
        menuSale.setOnClickListener()
        {
            val intent = Intent(this, Sale::class.java)
            startActivity(intent)
        }

        val menuSales = findViewById<ImageView>(R.id.menuSales)
        menuSales.setOnClickListener()
        {
            val intent = Intent(this, Sales::class.java)
            startActivity(intent)
        }

        val menuInventory = findViewById<ImageView>(R.id.menuInventory)
        menuInventory.setOnClickListener()
        {
            val intent = Intent(this, PagerInventory::class.java)
            startActivity(intent)
        }

        val menuAssorment = findViewById<ImageView>(R.id.menuAssorment)
        menuAssorment.setOnClickListener()
        {
            val intent = Intent(this, Assorment::class.java)
            startActivity(intent)
        }

        val menuAssorments = findViewById<ImageView>(R.id.menuAssorments)
        menuAssorments.setOnClickListener()
        {
            val intent = Intent(this, Assorments::class.java)
            startActivity(intent)
        }


    }

    private fun draweMenu()
    {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.menu)
        setSupportActionBar(toolbar)

        var navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    override fun onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        val drawerMenu = DrawerMenu()

        drawerMenu.menu(item, this)

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}