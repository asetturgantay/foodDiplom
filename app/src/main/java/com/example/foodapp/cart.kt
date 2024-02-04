package com.example.foodapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.adapter.CartAdapter
import com.example.foodapp.adapter.ProductAdapter2
import com.example.foodapp.data.Purchase
import com.example.foodapp.databinding.FragmentDashboardBinding
import com.example.foodapp.fragment.com_order
import com.example.foodapp.fragment.maps
import com.example.foodapp.fragment.noCart
import com.example.foodapp.fragment.no_Search
import com.example.foodapp.interfacee.FragmentInteractionListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context

class cart : AppCompatActivity(), FragmentInteractionListener {

    private lateinit var datalist: ArrayList<Product2>
    private lateinit var Purchase: ArrayList<Purchase>
    private var databaseReference: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null
    private lateinit var adapter: CartAdapter


    private lateinit var recyclerView: RecyclerView

    private lateinit var archiveList:ArrayList<String>


    var favoriteProductIds = mutableListOf<String>()
    var cartData = mutableListOf<String>()

    private var nullFragment: maps? = null
    private var comFragment: com_order? = null




    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)




        val exit:ImageView = findViewById(R.id.exit)

        exit.setOnClickListener {
            startActivity(Intent(this, navigation::class.java))
            finish()
        }




        archiveList= ArrayList()

        recyclerView = findViewById(R.id.recyclerView)

        val gridLayotManager = GridLayoutManager(this,1)
        recyclerView.layoutManager = gridLayotManager
        datalist = ArrayList()
        adapter = this?.let { CartAdapter(it, datalist) }!!
        recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("product")

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val database = FirebaseDatabase.getInstance()





        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.VISIBLE



        if (uid != null) {
            val favoritesRef = FirebaseDatabase.getInstance().reference.child("cart")

            favoritesRef.orderByChild("id_user").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {


                        for (favoriteSnapshot in dataSnapshot.children) {
                            val id_product = favoriteSnapshot.child("id").value
                            val id_cart = favoriteSnapshot.child("id_cart").value
                            if (id_product is String) {
                                favoriteProductIds.add(id_product)
                            }

                            if (id_cart is String){
                                cartData.add(id_cart)
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
                                    Toast.makeText(this@cart, databaseError.message, Toast.LENGTH_LONG).show()
                                    Log.e("Firebase", databaseError.message)

                                    // Hide ProgressBar (in case of an error)
                                    progressBar.visibility = View.GONE
                                }
                            })
                        }




                    }else{
                        Log.e("Firebase", "dataClass is null")
                        val fragment = noCart()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    Toast.makeText(this@cart, databaseError.message, Toast.LENGTH_LONG).show()
                    Log.e("Firebase", databaseError.message)

                    // Hide ProgressBar (in case of an error)
                    progressBar.visibility = View.GONE
                }
            })
        }

        swipeToGesture(recyclerView)

        val buyButton = findViewById<Button>(R.id.buyButton)

        buyButton.setOnClickListener {

            showNullFragment()




        }





    }





    private fun swipeToGesture(recyclerView: RecyclerView?) {
        val swipeGesture=object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val database = FirebaseDatabase.getInstance().getReference("cart")
                database.child(cartData[position]).removeValue()
                var actionBtnTapped = false
                try {
                    when(direction){
                        ItemTouchHelper.LEFT->{
                            val deleteItem= datalist[position]
                            datalist.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            val snackBar = Snackbar.make(
                                this@cart.recyclerView, "Удалено", Snackbar.LENGTH_LONG
                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    super.onDismissed(transientBottomBar, event)
                                }

                            }).apply {
                                animationMode = Snackbar.ANIMATION_MODE_FADE

                            }
                            snackBar.setActionTextColor(
                                ContextCompat.getColor(
                                    this@cart,
                                    R.color.orenge
                                )
                            )
                            snackBar.show()
                        }




                    }
                }
                catch (e:Exception){

                    Toast.makeText(this@cart, e.message, Toast.LENGTH_SHORT).show()

                }
            }
        }
        val touchHelper= ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerView)

    }


    private fun showNullFragment() {
        if (nullFragment == null) {
            nullFragment = maps()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nullFragment!!, "no_Search")
                .commit()
        } else if (!nullFragment!!.isVisible) {
            supportFragmentManager.beginTransaction()
                .show(nullFragment!!)
                .commit()
        }
    }

    override fun hideNullFragment() {
        if (nullFragment != null && nullFragment!!.isVisible) {
            supportFragmentManager.beginTransaction()
                .hide(nullFragment!!)
                .commit()
        }

    }

    override fun onTextReceivedFromFragment(textMarker: String, textAd: String, number: String) {
        // Обработайте полученный текст в активности
        Log.d("YourActivity", "Received text from fragment: $textMarker, $textAd, $number")
        // Теперь у вас есть доступ к данным из фрагмента, и вы можете выполнить необходимые действия

        val purchasesList = ArrayList<Purchase>()

        for (i in 0 until datalist.size) {
            // Получите идентификатор товара и количество
            val productId = datalist[i].id
            val holder =
                recyclerView.findViewHolderForAdapterPosition(i) as CartAdapter.MyViewHolder
            val quantity = holder.number.text.toString()

            val maker = textMarker
            val address = textAd
            val numberPh = number

            // Создайте объект Purchase и добавьте его в список
            val purchase = Purchase(productId, quantity, maker, address, numberPh)
            purchasesList.add(purchase)
        }

        // Отправьте данные о покупках на базу данных
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid != null) {
            val purchasesRef =
                FirebaseDatabase.getInstance().reference.child("purchases").child(uid)

            for (purchase in purchasesList) {
                // Создайте уникальный ключ для каждой покупки
                val purchaseKey = purchasesRef.push().key
                if (purchaseKey != null) {
                    // Добавьте данные о покупке на базу данных
                    purchasesRef.child(purchaseKey).setValue(purchase)

                }
            }

            // Очистите корзину после отправки данных
            datalist.clear()
            adapter.notifyDataSetChanged()

            // Добавьте код для отображения подтверждения покупки или другой логики
            // после успешного оформления заказа

            comFragment()
        }
    }

    private fun comFragment() {
        if (comFragment == null) {
            comFragment = com_order()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, comFragment!!, "no_Search")
                .commit()
        } else if (!comFragment!!.isVisible) {
            supportFragmentManager.beginTransaction()
                .show(comFragment!!)
                .commit()
        }
    }





}