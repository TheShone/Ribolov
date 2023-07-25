package elfak.mosis.ribolov.viewmodels

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.data.Recenzija
import elfak.mosis.ribolov.data.RibolovnoMesto
import elfak.mosis.ribolov.data.User

class RecenzijaViewModel: ViewModel() {
    private val storageRef = FirebaseStorage.getInstance().reference
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
    get() = _toastMessage
    private val _recenzija= MutableLiveData<Recenzija?>(null)
    private val _recenzije = MutableLiveData<List<Recenzija>>(emptyList())


    var recenzija
        get() = _recenzija.value
        set(value) { _recenzija.value=value}

    val recenzije: MutableLiveData<List<Recenzija>> get() = _recenzije

    fun setRecenzije(value: List<Recenzija>) {
        _recenzije.value = value
    }

    fun dodajRecenziju(recenzijaa: Recenzija, user: User)
    {
        var databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Recenzije")
        databaseUser?.child(recenzijaa.id)?.setValue(recenzijaa)
        databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("RibolovnaMesta")
        databaseUser.child(recenzijaa.idRibMesto).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                if (dataSnapshot.exists()) {
                    var dataSnapshot = task.result
                    val username: String =
                        dataSnapshot.child("oglasavac").getValue(String::class.java)!!
                    databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                    var poin=0
                    databaseUser.child(recenzijaa.recezent).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dataSnapshot = task.result
                            if (dataSnapshot.exists()) {
                                val dataSnapshot = task.result
                                poin = dataSnapshot.child("points").getValue(Int::class.java) as? Int ?: 0
                                databaseUser.child(recenzijaa.recezent).child("points").setValue(poin+2)
                            }
                        }
                    }
                    var points=0
                    databaseUser.child(username).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dataSnapshot = task.result
                            if (dataSnapshot.exists()) {
                                val dataSnapshot = task.result
                                points = dataSnapshot.child("points").getValue(Int::class.java) as? Int ?: 0
                                if (recenzijaa.ocena == 1) {
                                    databaseUser.child(username).child("points").setValue(points - 2)
                                } else if (recenzijaa.ocena == 2) {
                                    databaseUser.child(username).child("points").setValue(points - 1)
                                } else if (recenzijaa.ocena == 4) {
                                    databaseUser.child(username).child("points").setValue(points + 1)
                                } else if (recenzijaa.ocena == 5) {
                                    databaseUser.child(username).child("points").setValue(points + 2)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun showToastMessage(message: String) {
        _toastMessage.postValue(message)
    }
    fun getRecenzijezaRibMesto(ribMesto: RibolovnoMesto)
    {
        val databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Recenzije")
        databaseUser.orderByChild("ribMesto").equalTo(ribMesto.naziv)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val recenzijee = mutableListOf<Recenzija>()

                    for (recenzijaSnapshot in snapshot.children) {
                        val rec = recenzijaSnapshot.getValue(Recenzija::class.java)
                        rec?.let { recenzijee.add(it) }
                    }

                    _recenzije.value = if (recenzijee.isEmpty()) {
                        emptyList()
                    } else {
                        recenzijee
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to load comments: ${error.message}")
                }
            })

    }
}