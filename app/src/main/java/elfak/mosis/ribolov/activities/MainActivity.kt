package elfak.mosis.ribolov.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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