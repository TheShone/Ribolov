package elfak.mosis.ribolov.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.data.RibolovnoMesto
import elfak.mosis.ribolov.databinding.FragmentAddRibolovnaMestaBinding
import elfak.mosis.ribolov.viewmodels.LoggedUserViewModel
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel
import java.util.*


class AddRibolovnaMestaFragment : Fragment() {

    private var _binding: FragmentAddRibolovnaMestaBinding? = null
    private val binding get() = _binding!!
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()
    private val ribolovnoMestoViewModel: RibolovnoMestoViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRibolovnaMestaBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_add_ribolovnoMesto)
        item.isVisible=false;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout ->{
                val sharedPreferences =
                    requireContext().getSharedPreferences("Ribolov", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                this.findNavController().navigate(R.id.action_AddRibolovnaMestaFragment_to_LoginFragment)
                true
            }
            R.id.action_show_map->{
                this.findNavController().navigate(R.id.action_AddRibolovnaMestaFragment_to_HomeFragment)
                true
            }
            R.id.action_show_scoreboard->{
                this.findNavController().navigate(R.id.action_AddRibolovnaMestaFragment_to_RangListaFragment)
                true
            }
            R.id.action_show_profile->{
                this.findNavController().navigate(R.id.action_AddRibolovnaMestaFragment_to_ProfilFragment)
                true
            }
            else->super.onContextItemSelected(item)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addRibMestoDugme.setOnClickListener{
            AddRibMesto();
        }
    }
    private fun AddRibMesto() {
        val editName=requireView().findViewById<EditText>(R.id.addRibMesto_naziv)
        val editTipRibe = requireView().findViewById<Spinner>(R.id.addRibMesto_tipRibe)
        val editPristupacnost = requireView().findViewById<CheckBox>(R.id.addRibMesto_pristupacnost)
        val editUredjenost = requireView().findViewById<CheckBox>(R.id.addRibMesto_uredjenost)
        val editCistocaDna = requireView().findViewById<CheckBox>(R.id.addRibMesto_cistocaDna)
        val editPlatforma = requireView().findViewById<CheckBox>(R.id.addRibMesto_platforma)
        val editSator = requireView().findViewById<CheckBox>(R.id.addRibMesto_sator)
        val name = editName.text.toString()
        val tipRibe = editTipRibe.selectedItem.toString()
        val pristupacnost=editPristupacnost.isChecked()
        val uredjenost = editUredjenost.isChecked()
        val cistocaDna = editCistocaDna.isChecked()
        val platforma = editPlatforma.isChecked()
        val sator=editSator.isChecked()
        if(name!= "" && tipRibe!= null && pristupacnost!= null && uredjenost!= null && cistocaDna!= null && platforma!= null && sator!= null)
        {
            val user = loggedUserViewModel.user
            val date = Calendar.getInstance().time
            val databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("RibolovnaMesta")
            val id = databaseUser.push().key
            val RibMesto = RibolovnoMesto(id!!,name,user?.korisnickoime!!,tipRibe,pristupacnost,uredjenost,cistocaDna,platforma,
            sator,loggedUserViewModel.location!!.latitude,loggedUserViewModel.location!!.longitude,date)
            databaseUser?.child(RibMesto.id)?.setValue(RibMesto)
                ?.addOnSuccessListener {
                    ribolovnoMestoViewModel.dodajRibMesto(RibMesto,user)
                    Navigation.findNavController(binding.root).navigate(R.id.action_AddRibolovnaMestaFragment_to_HomeFragment)
                    Toast.makeText(this.activity, "Uspesno dodato RibolovnoMesto", Toast.LENGTH_LONG).show()
                }
        }
        else
        {
            Toast.makeText(this.activity, "Ne moze se dodati RibolovnoMesto bez naziva", Toast.LENGTH_LONG).show()
        }
    }

}