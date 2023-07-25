package elfak.mosis.ribolov.fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import elfak.mosis.ribolov.databinding.FragmentRangListaBinding
import elfak.mosis.ribolov.viewmodels.LoggedUserViewModel
import elfak.mosis.ribolov.viewmodels.RecenzijaViewModel
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.data.User
import elfak.mosis.ribolov.viewmodels.UsersViewModel

class RangListaFragment : Fragment() {

    private var _binding: FragmentRangListaBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRangListaBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_show_scoreboard)
        item.isVisible=false;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout ->{
                val sharedPreferences =
                    requireContext().getSharedPreferences("Ribolov", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                this.findNavController().navigate(R.id.action_RangListaFragment_to_LoginFragment)
                true
            }
            R.id.action_add_ribolovnoMesto->{
                this.findNavController().navigate(R.id.action_RangListaFragment_to_AddRibolovnaMestaFragment)
                true
            }
            R.id.action_show_map->{
                this.findNavController().navigate(R.id.action_RangListaFragment_to_HomeFragment)
                true
            }
            R.id.action_show_profile->{
                this.findNavController().navigate(R.id.action_RangListaFragment_to_ProfilFragment)
                true
            }
            else->super.onContextItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView: ListView = binding.rangListaListView

        // Pristup Firebase bazi podataka
        val databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")

        // Dodajte ValueEventListener koji će osluškivati promene na referenci "Users"
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userList = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { userList.add(it) }
                    }

                    // Sortiraj listu korisnika po poenima (pretpostavljamo da poeni predstavljaju rang)
                    userList.sortByDescending { it.points }

                    val userStrings = userList.map { "${it.korisnickoime} - Points: ${it.points}" }

                    if (userStrings.isNullOrEmpty()) {
                        Log.d("RangListaFragment", "UserStrings is null or empty")
                    } else {
                        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userStrings)
                        listView.adapter = arrayAdapter
                        Log.d("RangListaFragment", "ArrayAdapter set")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }


}