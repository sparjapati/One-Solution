package edu.vermaSanjay15907.oneSolution.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.vermaSanjay15907.oneSolution.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_home)
    }
}