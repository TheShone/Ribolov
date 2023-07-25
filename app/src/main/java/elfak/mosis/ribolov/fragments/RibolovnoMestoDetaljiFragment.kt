package elfak.mosis.ribolov.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.databinding.FragmentHomeBinding
import elfak.mosis.ribolov.databinding.FragmentRibolovnoMestoDetaljiBinding
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel
import android.text.Editable
import android.text.SpannableStringBuilder
import android.widget.Toast
import elfak.mosis.ribolov.viewmodels.RecenzijaViewModel

class RibolovnoMestoDetaljiFragment : DialogFragment() {

    private var _binding: FragmentRibolovnoMestoDetaljiBinding? = null
    private val binding get() = _binding!!
    private val ribolovnoMestoViewModel: RibolovnoMestoViewModel by activityViewModels()
    private val recenzijaViewModel: RecenzijaViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRibolovnoMestoDetaljiBinding.inflate(inflater, container, false)
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
        binding.detRibMestoNaziv.setText(ribolovnoMestoViewModel.ribMesto?.naziv)
        binding.detRibMestoTipRibe.setText(ribolovnoMestoViewModel.ribMesto?.vrstaRibe)
        binding.detRibMestoPristupacnost.isChecked=ribolovnoMestoViewModel.ribMesto?.pristupacnost ?: false
        binding.detRibMestoUredjenost.isChecked=ribolovnoMestoViewModel.ribMesto?.uredjenost ?: false
        binding.detRibMestoCistocaDna.isChecked=ribolovnoMestoViewModel.ribMesto?.cistocaDna ?: false
        binding.detRibMestoPlatforma.isChecked=ribolovnoMestoViewModel.ribMesto?.platforma ?: false
        binding.detRibMestoSator.isChecked=ribolovnoMestoViewModel.ribMesto?.sator ?: false
        binding.detRibMestoOglasavac.setText(ribolovnoMestoViewModel.ribMesto?.oglasavac)
        binding.detRibMestoRewiev.setOnClickListener(){
            val recenzijaFragment = RecenzijaFragment()
            recenzijaFragment.show(requireActivity().supportFragmentManager, "recenzija_dialog")
        }
        binding.detRibMestoRecenzije.setOnClickListener(){
            recenzijaViewModel.getRecenzijezaRibMesto(ribolovnoMestoViewModel?.ribMesto!!)
            val recenzijeFragment = RecenzijeFragment()
            recenzijeFragment.show(requireActivity().supportFragmentManager, "recenzije_dialog")
        }


    }

}