package elfak.mosis.ribolov.fragments

import java.util.Calendar
import java.util.Date
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import elfak.mosis.ribolov.R
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import elfak.mosis.ribolov.databinding.FragmentFilterBinding
import elfak.mosis.ribolov.databinding.FragmentRibolovnoMestoDetaljiBinding
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel
import java.util.*
import android.widget.DatePicker
import elfak.mosis.ribolov.viewmodels.LoggedUserViewModel

class FilterFragment :  DialogFragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private val ribolovnoMestoViewModel: RibolovnoMestoViewModel by activityViewModels()
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
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
        val editDatee: DatePicker = view.findViewById(R.id.datePicker)
        editDatee.updateDate(2023,6,1)
        binding.filterRibMesto.setOnClickListener{
            val editName=requireView().findViewById<EditText>(R.id.editTextNaziv)
            val editTipRibe = requireView().findViewById<Spinner>(R.id.spinner_tipRibe)
            val editOglasavac = requireView().findViewById<EditText>(R.id.editTextOglasivac)
            val editPristupacnost = requireView().findViewById<CheckBox>(R.id.checkBoxPristupacnost)
            val editUredjenost = requireView().findViewById<CheckBox>(R.id.checkBoxUredjenost)
            val editCistocaDna = requireView().findViewById<CheckBox>(R.id.checkBoxCistocaDna)
            val editPlatforma = requireView().findViewById<CheckBox>(R.id.checkBoxPlatforma)
            val editSator = requireView().findViewById<CheckBox>(R.id.checkBoxSator)
            val editRadijus = requireView().findViewById<EditText>(R.id.radijus)
            val oglasavac= editOglasavac.text.toString()
            val name = editName.text.toString()
            val tipRibe = editTipRibe.selectedItem.toString()
            val pristupacnost=editPristupacnost.isChecked()
            val uredjenost = editUredjenost.isChecked()
            val cistocaDna = editCistocaDna.isChecked()
            val platforma = editPlatforma.isChecked()
            val sator=editSator.isChecked()
            val editDate: DatePicker = view.findViewById(R.id.datePicker)
            val calendar = Calendar.getInstance()
            calendar.set(editDate.year, editDate.month, editDate.dayOfMonth)
            val selectedDate: Date = calendar.time
            val rad = editRadijus.text.toString()
            var radijus = 0.0
            if(rad!="")
                radijus = rad?.toDoubleOrNull()!!
            ribolovnoMestoViewModel.filtrirajRibolovnaMesta(oglasavac,name,tipRibe,pristupacnost,uredjenost,cistocaDna,platforma,sator,selectedDate,radijus!!,loggedUserViewModel.location?.latitude!!,loggedUserViewModel.location?.longitude!!)
            dismiss()
        }
        binding.filterReset.setOnClickListener{
            ribolovnoMestoViewModel.resetFilter()
            dismiss()
        }
    }

}