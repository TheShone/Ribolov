package elfak.mosis.ribolov.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.ribolov.data.User

class UsersViewModel : ViewModel() {
    private val _users= MutableLiveData<List<User>>(emptyList())

    val users: LiveData<List<User>> get() = _users

    fun getUsers(onDataLoaded: () -> Unit)
    {
        var databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val userList= mutableListOf<User>()
                    for(user in snapshot.children)
                    {
                        val u=user.getValue(User::class.java)
                        userList.add(u!!)
                    }
                    Log.d("UsersViewModel", "UserList: $userList") // Log the user list to see if it's loaded correctly
                    if(users==null)
                    {
                        _users.postValue(userList.sortedByDescending { it.points })
                        onDataLoaded()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException());
            }
        })
    }
}