package elfak.mosis.ribolov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import elfak.mosis.ribolov.data.Recenzija
import elfak.mosis.ribolov.databinding.FragmentRecenzijeBinding
import elfak.mosis.ribolov.viewmodels.RecenzijaViewModel
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel

class RecenzijeFragment : DialogFragment() {

    private var _binding: FragmentRecenzijeBinding? = null
    private val binding get() = _binding!!
    private val recenzijaViewModel: RecenzijaViewModel by activityViewModels()
    private val ribolovnoMestoViewModel:RibolovnoMestoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecenzijeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
        recenzijaViewModel.recenzije.observe(viewLifecycleOwner, androidx.lifecycle.Observer { recenzijeList ->
            recenzijeList?.let {
                val recenzijeTextList: List<String> = it.map { recenzija ->
                    "Ocena: ${recenzija.ocena}, Komentar: ${recenzija.komentar}, Recezent: ${recenzija.recezent}"
                }

                val listView: ListView = binding.recenzijeListView

                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    recenzijeTextList
                )
                listView.adapter = arrayAdapter
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recenzijaViewModel.toastMessage.observe(viewLifecycleOwner, androidx.lifecycle.Observer { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
