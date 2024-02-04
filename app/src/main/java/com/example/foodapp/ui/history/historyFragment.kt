package com.example.foodapp.ui.history

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodapp.Product2
import com.example.foodapp.R
import com.example.foodapp.adapter.ProductAdapter2
import com.example.foodapp.databinding.FragmentDashboardBinding
import com.example.foodapp.databinding.FragmentHistoryBinding
import com.example.foodapp.databinding.FragmentNoCartBinding
import com.example.foodapp.fragment.no_fovarite
import com.example.foodapp.fragment.no_history
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class historyFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding

    private lateinit var datalist: ArrayList<Product2>
    private var databaseReference: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null
    private lateinit var adapter: ProductAdapter2

    companion object {
        fun newInstance() = historyFragment()
    }

    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayotManager = GridLayoutManager(requireContext(),2)
        binding.recyclerView.layoutManager = gridLayotManager
        datalist = ArrayList()
        adapter = requireContext()?.let { ProductAdapter2(it, datalist) }!!
        binding.recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("product")

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val database = FirebaseDatabase.getInstance()




        val progressBar = binding.progressBar

        progressBar.visibility = View.VISIBLE


        // Assuming you have authenticated the user and have their UID

        if (uid != null) {
            val favoritesRef = FirebaseDatabase.getInstance().reference.child("purchases").child(uid)

            favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val favoriteProductIds = mutableListOf<String>()

                        for (favoriteSnapshot in dataSnapshot.children) {
                            val id_product = favoriteSnapshot.child("productId").value
                            if (id_product is String) {
                                favoriteProductIds.add(id_product)
                            }
                        }

                        val productsRef = FirebaseDatabase.getInstance().reference.child("product")

                        // Clear the list before populating
                        datalist.clear()

                        for (productId in favoriteProductIds) {
                            // Get product details based on the product ID
                            productsRef.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val dataClass = snapshot.getValue(Product2::class.java)
                                    if (dataClass != null) {
                                        // Add the product data to your list
                                        datalist.add(dataClass)

                                        // Notify the adapter of changes
                                        adapter.notifyDataSetChanged()
                                        progressBar.visibility = View.GONE
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(requireContext(), databaseError.message, Toast.LENGTH_LONG).show()
                                    Log.e("Firebase", databaseError.message)

                                    // Hide ProgressBar (in case of an error)
                                    progressBar.visibility = View.GONE
                                }
                            })
                        }
                    }else{
                        fragmentDialog()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    Toast.makeText(requireContext(), databaseError.message, Toast.LENGTH_LONG).show()
                    Log.e("Firebase", databaseError.message)

                    // Hide ProgressBar (in case of an error)
                    progressBar.visibility = View.GONE
                }
            })
        }

    }

    private fun fragmentDialog() {
        Log.e("Firebase", "dataClass is null")
        val fragmentManager = childFragmentManager

        val transaction = fragmentManager.beginTransaction()


        val newFragment = no_history()


        transaction.replace(R.id.fragment_container, newFragment)


        transaction.addToBackStack(null)


        transaction.commit()

    }

}