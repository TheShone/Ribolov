package elfak.mosis.ribolov.fragments

import RibolovnoMestoAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TableFragment : DialogFragment() {

    private val ribolovnoMestoViewModel: RibolovnoMestoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val ribolovnaMestaList = ribolovnoMestoViewModel.filtriranaRibolovnaMesta.value ?: emptyList()
        val adapter = RibolovnoMestoAdapter(ribolovnaMestaList)
        recyclerView.adapter = adapter
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

}