package com.example.foodapp.fragment

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.example.foodapp.GeocodingHelper
import com.example.foodapp.R
import com.example.foodapp.cart
import com.example.foodapp.databinding.FragmentMapsBinding
import com.example.foodapp.interfacee.FragmentInteractionListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Locale

class maps : Fragment(), OnMapReadyCallback /*OnMapReadyCallback*/ {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private lateinit var geocoder: Geocoder

    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    lateinit var address:String

    private var fragmentInteractionListener: FragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация карты
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Инициализация Places API
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())

        // Обработчик кнопки для выбора адреса
        binding.find.setOnClickListener {
            val autocompleteSessionToken = AutocompleteSessionToken.newInstance()

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, getPlaceFields())
                .setLocationBias(RectangularBounds.newInstance(
                    LatLng(43.1746, 76.8646), // Юго-западная граница Алматы
                    LatLng(43.3836, 77.0438)  // Северо-восточная граница Алматы
                ))
                .build(requireContext())

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }


        binding.minus.setOnClickListener {
            // Decrease the zoom level by 1
            val currentZoom = googleMap.cameraPosition.zoom
            val newZoom = currentZoom - 1.0f

            // Limit the minimum zoom level (optional)
            val minZoomLevel = 4.0f
            if (newZoom >= minZoomLevel) {
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(newZoom))
            }
        }

        binding.plus.setOnClickListener {
            // Increase the zoom level by 1
            val currentZoom = googleMap.cameraPosition.zoom
            val newZoom = currentZoom + 1.0f

            // Limit the maximum zoom level (optional)
            val maxZoomLevel = 20.0f
            if (newZoom <= maxZoomLevel) {
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(newZoom))
            }
        }

        binding.btn.setOnClickListener {

            val textMarker = binding.textAd.text.toString()
            val textAd = binding.numberKv.text.toString()
            val number = binding.numberPh.text.toString()



            if (textMarker != "Адрес доставки"){
                if (textAd.isEmpty()){
                    binding.numberKv.error = "Введите номер квартиры"
                }else{
                    if (number.isEmpty()){
                        binding.numberPh.error = "Введите номер телефона"
                    }else{
                        Log.d("tag", textMarker)
                        Log.d("tag", textAd)
                        Log.d("tag", number)

                        if (context is FragmentInteractionListener) {
                            (context as FragmentInteractionListener).onTextReceivedFromFragment(textMarker, textAd, number)
                            fragmentInteractionListener = context as FragmentInteractionListener
                            someFunctionInYourFragment()
                        } else {
                            throw RuntimeException("$context must implement OnFragmentInteractionListener")
                        }

                    }
                }
            }else{
                Toast.makeText(requireContext(), "Выберите адрес доставки", Toast.LENGTH_LONG).show()
            }


        }


    }

    private fun someFunctionInYourFragment() {
        fragmentInteractionListener?.hideNullFragment()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Установка обработчика клика по карте
        googleMap.setOnMapClickListener { latLng ->
            // Добавление маркера на карту
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            Toast.makeText(requireContext(), "$latLng", Toast.LENGTH_SHORT).show()

            val lng = latLng.latitude
            val long = latLng.longitude

            Log.d("adres", "$lng , $long")

            binding.textAd.text = latLng.toString()
        }

        // Координаты города Алматы
        val almatyLatLng = LatLng(43.238949, 76.889709)

        // Установка камеры на город Алматы
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(almatyLatLng, 12f))


    }

    // Обработка результата выбора адреса
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                // Обработка выбранного места
                handleSelectedPlace(place)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                // Обработка ошибки
                handleError(status)
            }
        }
    }

    // Метод для обработки выбранного места
    private fun handleSelectedPlace(place: Place) {
        val selectedLatLng = place.latLng
        address = place.address ?: "No address" // Получение строки адреса
        val latitude = place.latLng?.latitude // Получение широты
        val longitude = place.latLng?.longitude // Получение долготы

        if (selectedLatLng != null) {
            Log.e("adres", "SelectedLatLng: $selectedLatLng, Address: $address, Latitude: $latitude, Longitude: $longitude")

            // Добавление маркера на карту
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(selectedLatLng))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLatLng))

            // Дополнительные действия с выбранным местом, например, сохранение в базе данных

            binding.textAd.text = "$address\n${selectedLatLng.toString()}"

            Toast.makeText(requireContext(), "Address: $address", Toast.LENGTH_LONG).show()

        } else {
            Log.e("adres", "SelectedLatLng is null")
            Toast.makeText(requireContext(), "Null", Toast.LENGTH_LONG).show()
        }
    }



    // Метод для обработки ошибки Places API
    private fun handleError(status: Status) {
        // Обработка ошибки, например, вывод сообщения об ошибке
        val errorMessage = status.statusMessage
        println("Error: $errorMessage")
    }

    // Метод для определения полей, которые нужно получить от Places API
    private fun getPlaceFields(): List<Place.Field> {
        return listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }




    /*override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val minus: CardView = binding.minus
        val plus: CardView = binding.plus

        var latitude = "43.218345"
        var longitude = "76.872664"

        val point = LatLng(latitude.toDouble(), longitude.toDouble())
        mMap.addMarker(MarkerOptions().position(point).title("Я здесь"))
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point))

        minus.setOnClickListener {
            // Decrease the zoom level by 1
            val currentZoom = mMap.cameraPosition.zoom
            val newZoom = currentZoom - 1.0f

            // Limit the minimum zoom level (optional)
            val minZoomLevel = 4.0f
            if (newZoom >= minZoomLevel) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(newZoom))
            }
        }

        plus.setOnClickListener {
            // Increase the zoom level by 1
            val currentZoom = mMap.cameraPosition.zoom
            val newZoom = currentZoom + 1.0f

            // Limit the maximum zoom level (optional)
            val maxZoomLevel = 20.0f
            if (newZoom <= maxZoomLevel) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(newZoom))
            }
        }
    }*/
}
