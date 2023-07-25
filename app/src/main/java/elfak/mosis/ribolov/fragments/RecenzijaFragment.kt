package elfak.mosis.ribolov.fragments
import android.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.data.Recenzija
import elfak.mosis.ribolov.databinding.FragmentRecenzijaBinding
import elfak.mosis.ribolov.viewmodels.LoggedUserViewModel
import elfak.mosis.ribolov.viewmodels.RecenzijaViewModel
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel
import kotlinx.coroutines.NonCancellable
import java.util.*

class RecenzijaFragment : DialogFragment() {

    private var _binding: FragmentRecenzijaBinding? = null
    private val binding get() = _binding!!
    private val recenzijaViewModel: RecenzijaViewModel by activityViewModels()
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()
    private val ribolovnoMestoViewModel: RibolovnoMestoViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecenzijaBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onStart() {
        super.onStart()

        // Set the dialog width and height here (optional)
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recenzijaDodajKomentar.setOnClickListener {
            AddRecenziju()
            dismiss()
        }
        recenzijaViewModel.toastMessage.observe(viewLifecycleOwner, androidx.lifecycle.Observer { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun AddRecenziju() {
        val editKom = requireView().findViewById<EditText>(R.id.recenzija_kom)
        val editocena = requireView().findViewById<RatingBar>(R.id.recenzija_ratingBar)
        val komentar = editKom.text.toString()
        val ocena = editocena.rating.toInt()
        if (komentar.isNotEmpty() && ocena != 0) {
            val user = loggedUserViewModel.user
            val databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Recenzije")
            val id = databaseUser.push().key
            Toast.makeText(activity, ocena.toString(), Toast.LENGTH_SHORT).show()
            if(user?.korisnickoime!=ribolovnoMestoViewModel.ribMesto?.oglasavac) {
                var recenzija = Recenzija(
                    id!!,
                    user?.korisnickoime!!,
                    ribolovnoMestoViewModel.ribMesto?.naziv!!,
                    ribolovnoMestoViewModel.ribMesto?.id!!,
                    ocena,
                    komentar
                )
                recenzijaViewModel.dodajRecenziju(recenzija, loggedUserViewModel?.user!!)
            }
            else
            {
                Toast.makeText(activity, "Ne mozete sami sebi oceniti oglas", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "Nisu uneseni komentar ili ocena", Toast.LENGTH_SHORT).show()
        }
    }
}
