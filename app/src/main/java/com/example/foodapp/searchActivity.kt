package com.example.foodapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.adapter.ProductAdapter
import com.example.foodapp.fragment.noCart
import com.example.foodapp.fragment.no_Search
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class searchActivity : AppCompatActivity() {
    private lateinit var datalist: ArrayList<Product>
    private var databaseReference: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null
    private lateinit var adapter: ProductAdapter

    private lateinit var searchView: SearchView

    private var nullFragment: no_Search? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener {
            startActivity(Intent(this, navigation::class.java))
            finish()
        }

        searchView = findViewById(R.id.search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerView1(newText)
                return true
            }
        })

        recyclerView1(query = null)


    }

    private fun recyclerView1(query: String?) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val gridLayotManager = GridLayoutManager(this,2)
        recyclerView.layoutManager = gridLayotManager
        datalist = ArrayList()
        adapter = this.let { ProductAdapter(it, datalist) }
        recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().reference



        val size = findViewById<TextView>(R.id.size)





        val progressBar = findViewById<ProgressBar>(R.id.progressBar)



        progressBar.visibility = View.VISIBLE

        val queryText = query ?: ""



        val query = databaseReference!!.child("product").orderByChild("title").startAt(queryText).endAt(queryText + "\uf8ff")



            eventListener = query.addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
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

                    val numberOfProducts = datalist.size.toString()

                    val a:Int = 1

                    if (numberOfProducts != "0") {
                        size.text = "Найдено $numberOfProducts результатов"
                        hideNullFragment() // Закрыть nullFragment, если результаты найдены
                    } else {
                        size.text = "Ничего не найдено"
                        showNullFragment() // Открыть nullFragment, если результаты не найдены
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@searchActivity, error.message.toString(), Toast.LENGTH_LONG).show()
                    Log.e("Firebase Error", error.message)

                    // По завершении загрузки данных скрыть ProgressBar (в случае ошибки)
                    progressBar.visibility = View.GONE
                }
            })



    }

    private fun showNullFragment() {
        if (nullFragment == null) {
            nullFragment = no_Search()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nullFragment!!, "no_Search")
                .commit()
        } else if (!nullFragment!!.isVisible) {
            supportFragmentManager.beginTransaction()
                .show(nullFragment!!)
                .commit()
        }
    }

    private fun hideNullFragment() {
        if (nullFragment != null && nullFragment!!.isVisible) {
            supportFragmentManager.beginTransaction()
                .hide(nullFragment!!)
                .commit()
        }
    }


}