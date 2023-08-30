package elfak.mosis.ribolov.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.databinding.FragmentAddRibolovnaMestaBinding
import elfak.mosis.ribolov.databinding.FragmentProfilBinding
import elfak.mosis.ribolov.viewmodels.LoggedUserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_show_profile)
        item.isVisible=false;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout ->{
                val sharedPreferences =
                    requireContext().getSharedPreferences("Ribolov", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                this.findNavController().navigate(R.id.action_ProfilFragmentFragment_to_LoginFragment)
                true
            }
            R.id.action_show_map->{
                this.findNavController().navigate(R.id.action_ProfilFragmentFragment_to_HomeFragment)
                true
            }
            R.id.action_show_scoreboard->{
                this.findNavController().navigate(R.id.action_ProfilFragmentFragment_to_RangListaFragment)
                true
            }
            R.id.action_add_ribolovnoMesto->{
                this.findNavController().navigate(R.id.action_ProfilFragment_to_AddRibolovnaMestaFragment)
                true
            }
            else->super.onContextItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profilIme.setText(loggedUserViewModel.user?.ime)
        binding.profilPrezime.setText(loggedUserViewModel.user?.prezime)
        binding.profilKorisnicko.setText((loggedUserViewModel.user?.korisnickoime))
        binding.profilTelefon.setText(loggedUserViewModel.user?.brojtelefona)
        binding.profilPoeni.text = loggedUserViewModel.user?.points.toString()
        Glide.with(this)
            .load(loggedUserViewModel.user?.imageURL)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(binding.profilSlika)
    }

}