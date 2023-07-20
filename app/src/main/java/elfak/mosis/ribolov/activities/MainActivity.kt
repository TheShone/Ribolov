package elfak.mosis.ribolov.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.fragments.RegisterFragment
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    private var lastBackPressTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.action_logout->logout()
        }

        return super.onOptionsItemSelected(item)
    }
    private fun logout()
    {
        val sharedPreferences = getSharedPreferences("Ribolov", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.LoginFragment)

    }
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastBackPressTime < 2000) {
            finish()
        } else {
            Toast.makeText(this,"Pritisnite back dugme ponovo da biste zatvorili aplikaciju",Toast.LENGTH_SHORT).show()
            lastBackPressTime = currentTime
        }

    }
}