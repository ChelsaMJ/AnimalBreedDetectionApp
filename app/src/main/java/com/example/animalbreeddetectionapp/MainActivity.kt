package com.example.animalbreeddetectionapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat.applyTheme
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.fragments.DetectFragment
import com.example.animalbreeddetectionapp.fragments.ExploreFragment
import com.example.animalbreeddetectionapp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    // theme prefs
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Chimera"

        // drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragment(DetectFragment())
                }
                R.id.nav_guidebook -> {
                    // TODO: open AnimalGuidebookActivity later
                    Toast.makeText(this, "Open Guidebook (coming soon)", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_facts -> {
                    // TODO: open Facts screen later
                    Toast.makeText(this, "Open Facts (coming soon)", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_favorites -> {
                    Toast.makeText(this, "Open Favorites (coming soon)", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Open Settings (coming soon)", Toast.LENGTH_SHORT).show()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Bottom nav - keep your existing setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> ExploreFragment()
                R.id.nav_detect -> DetectFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> DetectFragment()
            }
            replaceFragment(fragment)
            true
        }

        // default fragment
        if (savedInstanceState == null) {
            replaceFragment(DetectFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


}
