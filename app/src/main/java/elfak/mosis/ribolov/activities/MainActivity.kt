package elfak.mosis.ribolov.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.fragments.RegisterFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            // If the app is launching for the first time, add the RegisterFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .commit()
        }
    }
}