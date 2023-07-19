package elfak.mosis.ribolov.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.databinding.FragmentHomeBinding
import elfak.mosis.ribolov.databinding.FragmentRegisterBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.LogOut.setOnClickListener{
            Odjavise()
            Navigation.findNavController(binding.root).navigate(R.id.action_HomeFragment_to_LoginFragment)
        }

    }
    private fun Odjavise(){
        val sharedPreferences = requireContext().getSharedPreferences("Ribolov", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
    }

}