package com.example.foodapp.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foodapp.databinding.FragmentNotificationsBinding
import com.example.foodapp.login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationsFragment : Fragment() {

    lateinit var auth:FirebaseAuth

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        auth = FirebaseAuth.getInstance()

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        var name_user = binding.nameUser
        var email_user = binding.emailUser
        var phone_user = binding.phoneNum

        val email = user?.email





        email_user.setText("$email")



        val databaseReference = FirebaseDatabase.getInstance().getReference("user").child(user!!.uid)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val textValue = dataSnapshot.child("name").getValue(String::class.java)
                    val phone = dataSnapshot.child("phone").getValue(String::class.java)
                    if (textValue != null) {
                        name_user.setText("$textValue")
                        phone_user.setText("$phone")
                        println("Текстовое значение: $textValue")
                    } else {
                        println("Значение 'name' в базе данных пусто или не существует.")
                    }


                } else {
                    println("Узел не существует в базе данных.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибки при запросе данных
                println("Ошибка при запросе данных: ${databaseError.message}")
            }
        })



    }
}