package elfak.mosis.ribolov.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import elfak.mosis.ribolov.databinding.FragmentHomeBinding
import elfak.mosis.ribolov.R
import elfak.mosis.ribolov.data.RibolovnoMesto
import elfak.mosis.ribolov.data.User
import elfak.mosis.ribolov.viewmodels.LoggedUserViewModel
import elfak.mosis.ribolov.viewmodels.RibolovnoMestoViewModel

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private var isMapReady = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var location: MutableLiveData<Location>
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()
    private val ribolovnoMestoViewModel: RibolovnoMestoViewModel by activityViewModels()
    private var ribMestaMap: MutableMap<Marker?, RibolovnoMesto> = mutableMapOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences("Ribolov", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", "")
        val databaseUser = FirebaseDatabase.getInstance("https://ribolov-a8c7c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
        databaseUser.child(savedUsername!!).get().addOnCompleteListener { task ->
            val dataSnapshot = task.result
            val ime = dataSnapshot.child("ime").getValue(String::class.java) ?: ""
            val prezime = dataSnapshot.child("prezime").getValue(String::class.java) ?: ""
            val slika = dataSnapshot.child("imageURL").getValue(String::class.java) ?: ""
            val korisnicko = dataSnapshot.child("korisnickoime").getValue(String::class.java) ?: ""
            val telefon = dataSnapshot.child("brojtelefona").getValue(String::class.java) ?: ""
            val poeni = dataSnapshot.child("points").getValue(Int::class.java) ?: 0
            val rang = dataSnapshot.child("rang").getValue(Int::class.java) ?: 0
            val sifra = dataSnapshot.child("sifra").getValue(String::class.java) ?: ""

            if (ime != null && prezime != null && slika != null && korisnicko != null
                && telefon != null && poeni != null && rang != null && sifra != null
            ) {
                // All values are not null, create the User object
                val userr = User(ime, prezime, korisnicko, sifra, telefon, slika, rang, poeni)
                loggedUserViewModel.user = userr
            }
            else {
                Log.e("Firebase", "Some values are null.")
            }
        }
        ribolovnoMestoViewModel.filtriranaRibolovnaMesta.observe(viewLifecycleOwner) { filtriranaMesta ->

            updateMapWithFilteredMarkers(filtriranaMesta)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        location= MutableLiveData()
        mapFragment!!.getMapAsync{ mMap ->
            map =mMap
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear()
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isCompassEnabled = true

            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1001)
                return@getMapAsync
            }
            mMap.isMyLocationEnabled=true
            mMap.setOnInfoWindowClickListener(this)
            fusedLocationClient.lastLocation.addOnCompleteListener {location->
                if(location.result  !=null)
                {
                    lastLocation=location.result
                    val currentLatLong= LatLng(location.result.latitude, location.result.longitude)
                    loggedUserViewModel.location=currentLatLong
                    val googlePlex = CameraPosition.builder()
                        .target(currentLatLong)
                        .zoom(15f)
                        .bearing(0f)
                        .tilt(0f)
                        .build()

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null)
                    if(ribolovnoMestoViewModel.ribolovnaMesta!=null)
                    {
                            ribolovnoMestoViewModel.getRibolovnaMesta(location = LatLng(lastLocation.latitude, lastLocation.longitude),
                                onDataLoaded = {
                                    setUpMarkers()
                                })
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        binding.fabAdd.setOnClickListener{
            val filterFragment = FilterFragment()
            filterFragment.show(requireActivity().supportFragmentManager, "filter_dialog")
        }
        binding.fabTable.setOnClickListener{
            val tableFragment = TableFragment()
            tableFragment.show(requireActivity().supportFragmentManager, "table_dialog")
        }
    }
    private fun updateMapWithFilteredMarkers(filtriranaMesta: List<RibolovnoMesto>) {
        Log.d("UpdateMap", "Ažuriranje mape sa filtriranim mestima")
        map?.clear()
        ribMestaMap.clear()

        filtriranaMesta.forEach { ribolovnoMesto ->
            val marker = map?.addMarker(
                MarkerOptions()
                    .position(LatLng(ribolovnoMesto.latitude, ribolovnoMesto.longitude))
                    .title(ribolovnoMesto.naziv)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ribolov))
            )
            ribMestaMap[marker] = ribolovnoMesto
            marker?.tag = ribolovnoMesto
        }
    }
    private fun setUpMarkers()
    {
        map?.clear()
        ribMestaMap= mutableMapOf()
        val ribMesta = ribolovnoMestoViewModel.ribolovnaMesta.value
        if(ribMesta!=null)
        {
            for(ribMesto in ribMesta)
            {
                val marker = map?.addMarker(
                    MarkerOptions().position(LatLng(ribMesto.latitude, ribMesto.longitude)).title(ribMesto.naziv).icon(
                        BitmapDescriptorFactory.fromResource(R.mipmap.ribolov))
                )
                ribMestaMap[marker] = ribMesto
                marker?.tag = ribMesto
            }
        }

    }
    override fun onInfoWindowClick(marker: Marker)
    {
        ribolovnoMestoViewModel.ribMesto=marker.tag as RibolovnoMesto

        val dialogFragment = RibolovnoMestoDetaljiFragment()
        dialogFragment.show(parentFragmentManager, "RibolovnoMestoDetaljiFragment")
    }
    private fun setMyLocationOverlay() {
        if (map != null) {
            try {
                map?.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_show_map)
        item.isVisible=false;
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setMyLocationOverlay()
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout ->{
                val sharedPreferences =
                    requireContext().getSharedPreferences("Ribolov", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                this.findNavController().navigate(R.id.action_HomeFragment_to_LoginFragment)
                true
            }

            R.id.action_add_ribolovnoMesto->{
                    this.findNavController().navigate(R.id.action_HomeFragment_to_AddRibolovnaMestaFragment)
                    true
            }

            R.id.action_show_scoreboard-> {
                this.findNavController().navigate(R.id.action_HomeFragment_to_RangListaFragment)
                true
            }
            R.id.action_show_profile->{
                this.findNavController().navigate(R.id.action_HomeFragment_to_ProfilFragment)
                true
            }
            else->super.onContextItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this.activity, "Map is ready.", Toast.LENGTH_SHORT).show()
        map = googleMap
        isMapReady = true

    }

}