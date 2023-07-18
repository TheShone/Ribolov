package elfak.mosis.ribolov.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.data.User
import elfak.mosis.ribolov.databinding.FragmentRegisterBinding
import java.io.FileNotFoundException
import java.io.InputStream
import java.security.MessageDigest
import kotlin.random.Random

class RegisterFragment : Fragment() {

    private val random = Random(System.currentTimeMillis())
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private var databaseUser:DatabaseReference?=null
    private var downloadUrl:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.registerImagebutton.setOnClickListener{
            otvoriGaleriju()
        }
        binding.registerRegisterbutton.setOnClickListener{
            register() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            try
            {
                val imageStream: InputStream? = requireActivity().contentResolver.openInputStream(selectedImageUri!!)
                val selectedImageBitmap = BitmapFactory.decodeStream(imageStream)
                binding.registerProfilePhoto.setImageBitmap(selectedImageBitmap)

            }
            catch(e: FileNotFoundException)
            {
                e.printStackTrace();
            }
        }
    }
    private fun register() {
        val editIme = requireView().findViewById<EditText>(R.id.register_name)
        val editPrezime = requireView().findViewById<EditText>(R.id.register_surname)
        val editUsername = requireView().findViewById<EditText>(R.id.register_username)
        val editPassword = requireView().findViewById<EditText>(R.id.register_password)
        val editBroj = requireView().findViewById<EditText>(R.id.register_phonenumber)


        val ime = editIme.text.toString()
        val prezime = editPrezime.text.toString()
        val username = editUsername.text.toString()
        val sifra = hashPassword(editPassword.text.toString())
        val brojTelefona = editBroj.text.toString()

        if (ime != "" && prezime != "" && username != "" && sifra != "" && brojTelefona != "") {
            databaseUser =
                FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference()

            val storageRef= FirebaseStorage.getInstance().getReference();
            val stringBuilder = StringBuilder()
            for (i in 1..50) {
                val randomDigit = random.nextInt(10)
                stringBuilder.append(randomDigit)
            }
            if (selectedImageUri!=null) {
                val fileRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
                fileRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {

                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                            val activityObj: Activity? = this.activity
                            val user = User(ime, prezime, username, sifra, brojTelefona,uri.toString())
                            val userKey = databaseUser?.push()?.key // Generate a unique key for the new object
                            if (userKey != null) {
                                databaseUser?.child(userKey)?.setValue(user)
                                    ?.addOnSuccessListener {
                                        Toast.makeText(activityObj, "Uspesno registrovan korisnik", Toast.LENGTH_LONG).show()
                                    }
                                    ?.addOnFailureListener {
                                        Toast.makeText(activityObj, "Bezuspesno registrovanje", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                val activityObj: Activity? = this.activity
                                Toast.makeText(activityObj, "Unesite sve podatke", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnFailureListener {
                        val activityObj: Activity? = this.activity
                        Toast.makeText(activityObj, "Doslo je do greske prilikom uploadovanja slike", Toast.LENGTH_LONG).show()
                    }
            }

        }
    }

    private fun otvoriGaleriju()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)

    }
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytesToHex(hashedBytes)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v.ushr(4)]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }


    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}