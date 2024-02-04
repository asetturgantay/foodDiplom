package com.example.foodapp.interfacee

interface FragmentInteractionListener {
    fun hideNullFragment()
    fun onTextReceivedFromFragment(textMarker: String, textAd: String, number: String)
}