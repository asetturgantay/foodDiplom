package com.example.foodapp.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.Product
import com.example.foodapp.Product2
import com.example.foodapp.R
import com.example.foodapp.adapter.ProductAdapter
import com.example.foodapp.adapter.ProductAdapter2
import com.example.foodapp.cart
import com.example.foodapp.databinding.FragmentHomeBinding
import com.example.foodapp.navigation
import com.example.foodapp.searchActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {


    private lateinit var datalist: ArrayList<Product>
    private var databaseReference: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null
    private lateinit var adapter: ProductAdapter

    private lateinit var datalist2: ArrayList<Product2>
    private var databaseReference2: DatabaseReference? = null
    private var eventListener2: ValueEventListener? = null
    private lateinit var adapter2: ProductAdapter2

    var type:String = "pizza"




    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val openMenuButton: ImageView = view.findViewById(R.id.menu)
        openMenuButton.setOnClickListener {
            openMenu()
        }



        val tabLayout = binding.tabLayout



        tabLayout.addTab(tabLayout.newTab().setText("Пицца"))
        tabLayout.addTab(tabLayout.newTab().setText("Плов"))
        tabLayout.addTab(tabLayout.newTab().setText("Напитки"))
        tabLayout.addTab(tabLayout.newTab().setText("Sauce"))


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> {
                            type = "pizza"
                            recyclerView1()
                        }
                        1 -> {
                            type = "plov"
                            recyclerView1()
                        }
                        2 -> {
                            type = "wather"
                            recyclerView1()
                        }

                        /*
                        1 -> tab.text = "Новый текст для Вкладки 2"
                        2 -> tab.text = "Новый текст для Вкладки 3"*/
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Этот код будет выполнен, когда вкладка перестает быть выбранной
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Этот код будет выполнен, если выбрана уже выбранная вкладка
            }
        })

        binding.search.setOnClickListener {
            startActivity(Intent(requireContext(), searchActivity::class.java))
        }

        binding.btnCart.setOnClickListener {
            startActivity(Intent(requireContext(), cart::class.java))
        }






        recyclerView1()
        recyclerView2()



    }

    private fun openMenu() {
        val activity = requireActivity() as navigation
        activity.openDrawer()
    }

    private fun recyclerView2() {
        val gridLayotManager = GridLayoutManager(requireContext(),2)
        binding.recyclerView2.layoutManager = gridLayotManager
        datalist2 = ArrayList()
        adapter2 = requireContext()?.let { ProductAdapter2(it, datalist2) }!!
        binding.recyclerView2.adapter = adapter2
        databaseReference2 = FirebaseDatabase.getInstance().getReference("product")



        val progressBar = binding.progressBar

        progressBar.visibility = View.VISIBLE

        eventListener2 = databaseReference2!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                datalist2.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Product2::class.java)
                    if (dataClass != null) {
                        datalist2.add(dataClass as Product2)
                    }
                }

                // По завершении загрузки данных скрыть ProgressBar
                progressBar.visibility = View.GONE

                // Уведомить адаптер об изменениях
                adapter2.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("Firebase Error", error.message)

                // По завершении загрузки данных скрыть ProgressBar (в случае ошибки)
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun recyclerView1() {

        val gridLayotManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = gridLayotManager
        datalist = ArrayList()
        adapter = requireContext()?.let { ProductAdapter(it, datalist) }!!
        binding.recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("product")




        val progressBar = binding.progressBar

        progressBar.visibility = View.VISIBLE

        eventListener = databaseReference!!.orderByChild("type").equalTo("$type").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                datalist.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(Product::class.java)
                    if (dataClass != null) {
                        datalist.add(dataClass as Product)
                    }
                }

                // По завершении загрузки данных скрыть ProgressBar
                progressBar.visibility = View.GONE

                // Уведомить адаптер об изменениях
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("Firebase Error", error.message)

                // По завершении загрузки данных скрыть ProgressBar (в случае ошибки)
                progressBar.visibility = View.GONE

            }
        })
        }
}