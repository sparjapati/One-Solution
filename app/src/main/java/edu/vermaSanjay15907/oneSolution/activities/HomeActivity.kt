package edu.vermaSanjay15907.oneSolution.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import edu.vermaSanjay15907.oneSolution.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost =
            supportFragmentManager.findFragmentById(binding.homeActivityFragmentContainerView.id) as NavHostFragment
        navController = navHost.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

//        binding.navView.setNavigationItemSelectedListener { item: MenuItem ->
//            val fragmentClass: Class
//            when (item.itemId) {
//                R.id.aboutFragment -> {
//                    fragmentClass = AboutUsFragment::class
//                }
//            }
//            true
//        }

    }

}