import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.data.RibolovnoMesto

class RibolovnoMestoAdapter(private val ribolovnaMestaList: List<RibolovnoMesto>) :
    RecyclerView.Adapter<RibolovnoMestoAdapter.RibolovnoMestoViewHolder>() {

    class RibolovnoMestoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nazivTextView: TextView = itemView.findViewById(R.id.textViewNaziv)
        val oglasivacTextView: TextView = itemView.findViewById(R.id.textViewOglasivac)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RibolovnoMestoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return RibolovnoMestoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RibolovnoMestoViewHolder, position: Int) {
        val currentRibolovnoMesto = ribolovnaMestaList[position]
        holder.nazivTextView.text = currentRibolovnoMesto.naziv
        holder.oglasivacTextView.text = currentRibolovnoMesto.getOglasavaca()
    }

    override fun getItemCount(): Int {
        return ribolovnaMestaList.size
    }
}