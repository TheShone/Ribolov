package elfak.mosis.ribolov.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.ribolov.data.RibolovnoMesto
import elfak.mosis.ribolov.data.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RibolovnoMestoViewModel: ViewModel() {
    private val storageRef = FirebaseStorage.getInstance().reference

    private val _ribMesto= MutableLiveData<RibolovnoMesto?>(null)
    private val _ribMesta=MutableLiveData<List<RibolovnoMesto>>(emptyList())
    private val _filtriranaRibolovnaMesta = MutableLiveData<List<RibolovnoMesto>>()
    val filtriranaRibolovnaMesta: LiveData<List<RibolovnoMesto>> get() = _filtriranaRibolovnaMesta
    private val _ResetRibolovnaMesta = MutableLiveData<List<RibolovnoMesto>>()
    val resetRibolovnaMesta: LiveData<List<RibolovnoMesto>> get() = _ResetRibolovnaMesta

    var ribMesto
        get() = _ribMesto.value
        set(value) { _ribMesto.value=value}

    val ribolovnaMesta: LiveData<List<RibolovnoMesto>> get() = _ribMesta

    fun dodajRibMesto(ribMesto: RibolovnoMesto, user: User)
    {
        val databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
        databaseUser.child(ribMesto.oglasavac).child("points").setValue(user.points+10)
    }
    private fun getDistance(currentLat: Double, currentLon: Double, deviceLat: Double, deviceLon: Double): Double {
        val earthRadius = 6371000.0

        val currentLatRad = Math.toRadians(currentLat)
        val deviceLatRad = Math.toRadians(deviceLat)
        val deltaLat = Math.toRadians(deviceLat - currentLat)
        val deltaLon = Math.toRadians(deviceLon - currentLon)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(currentLatRad) * cos(deviceLatRad) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
    fun getRibolovnaMesta(location: LatLng, radius: Int=1000000, onDataLoaded: () -> Unit)
    {
        val databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("RibolovnaMesta")
        databaseUser.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val ribolovnaMesta= mutableListOf<RibolovnoMesto>()
                    for(dev in snapshot.children){
                        val ribM=dev.getValue(RibolovnoMesto::class.java)
                        ribM?.let{
                            val distance=getDistance(location.latitude, location.longitude, ribM.latitude, ribM.longitude)
                            if(distance<=radius)
                            {
                                ribolovnaMesta.add(ribM)
                            }
                        }
                    }
                    _ribMesta.value=ribolovnaMesta
                    _ResetRibolovnaMesta.value=ribolovnaMesta
                    _filtriranaRibolovnaMesta.value=ribolovnaMesta
                    onDataLoaded()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException());
            }
        })
    }
    fun filtrirajRibolovnaMesta(oglasavac: String,naziv: String, tipRibe: String, pristupacnost: Boolean,
    uredjenost: Boolean, cistocaDna: Boolean, platforma: Boolean, sator: Boolean, datum: Date, radijus: Double, userLat: Double, userLong: Double
    )
    {
        val filterOglasavac = oglasavac.isNotEmpty()
        val filterNaziv = naziv.isNotEmpty()
        val filterTipRibe = tipRibe != "Izaberi"
        val filterRadijus = radijus != 0.0

        val originalnaRibolovnaMesta = _ResetRibolovnaMesta.value ?: emptyList()

        val filtriranaRibolovnaMesta = originalnaRibolovnaMesta.filter { ribMesto ->
            val matchOglasavac = !filterOglasavac || ribMesto.oglasavac.contains(oglasavac, ignoreCase = true)
            val matchNaziv = !filterNaziv || ribMesto.naziv.contains(naziv, ignoreCase = true)
            val matchTipRibe = !filterTipRibe || ribMesto.vrstaRibe.contains(tipRibe, ignoreCase = true)
            val matchPristupacnost = !pristupacnost || ribMesto.pristupacnost
            val matchUredjenost = !uredjenost || ribMesto.uredjenost
            val matchCistocaDna = !cistocaDna || ribMesto.cistocaDna
            val matchPlatforma = !platforma || ribMesto.platforma
            val matchSator = !sator || ribMesto.sator
            val matchDatum = ribMesto.datumPostavljanja.after(datum)
            val matchRadijus = !filterRadijus || getDistance(userLat, userLong, ribMesto.latitude, ribMesto.longitude) < radijus

            matchOglasavac && matchNaziv && matchTipRibe && matchPristupacnost && matchUredjenost &&
                    matchCistocaDna && matchPlatforma && matchSator && matchDatum && matchRadijus
        }
               _ribMesta.value = filtriranaRibolovnaMesta
               _filtriranaRibolovnaMesta.value = filtriranaRibolovnaMesta



    }
    fun resetFilter()
    {
        _filtriranaRibolovnaMesta.value=_ResetRibolovnaMesta.value
    }

}