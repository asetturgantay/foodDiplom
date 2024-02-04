package com.example.foodapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.adapter.ProductAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import kotlin.random.Random

class MainActivity2 : AppCompatActivity() {

    lateinit var auth: FirebaseAuth


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val i = Intent()

        val text1 = intent.getStringExtra("title")
        val text2 = intent.getStringExtra("price")
        val text3 = intent.getStringExtra("img")
        val id = intent.getStringExtra("id")


        val title = findViewById<TextView>(R.id.title)
        val price = findViewById<TextView>(R.id.price)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val exit = findViewById<ImageView>(R.id.exit)
        val cart_btn = findViewById<Button>(R.id.btn)

        val checkFovarite = findViewById<CheckBox>(R.id.btn_cart)

        auth = FirebaseAuth.getInstance()

        val database = FirebaseDatabase.getInstance().getReference("fovarites")
        val cartr = FirebaseDatabase.getInstance().getReference("cart")


        checkFovarite.setOnCheckedChangeListener { buttonView, isChecked ->
            val randomText = generateRandomText(30)
            database.child(randomText).child("id").setValue(id.toString())
            database.child(randomText).child("id_user").setValue(auth.currentUser!!.uid)

        }



        title.text = text1
        price.text = text2 + " тг"
        Glide.with(this).load(text3).into(imageView)

        exit.setOnClickListener {
            startActivity(Intent(this, navigation::class.java))
            finish()
        }

        cart_btn.setOnClickListener {
            val randomText = generateRandomText(30)
            cartr.child(randomText).child("id").setValue(id.toString())
            cartr.child(randomText).child("id_user").setValue(auth.currentUser!!.uid)
            cartr.child(randomText).child("id_cart").setValue(randomText)

            startActivity(Intent(this, cart::class.java))
        }







    }

    fun generateRandomText(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun main() {
        val randomText = generateRandomText(100) // Change the length as needed
    }
}