package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodapp.databinding.FragmentLoginBinding
import com.example.foodapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()



        val database = FirebaseDatabase.getInstance().getReference("user")

        val btn = binding.btn
        val email = binding.email
        val password = binding.password
        val name = binding.name
        val phone = binding.phoneNum


        btn.setOnClickListener{
            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser!!.uid
                        Toast.makeText(requireContext(), "Вы успешно зарегистрировались", Toast.LENGTH_LONG).show()

                        database.child(uid).child("name").setValue(name.text.toString())
                        database.child(uid).child("phone").setValue(phone.text.toString())

                        startActivity(Intent(requireContext(), navigation::class.java))

                    } else {
                        Toast.makeText(requireContext(), "Не удалось зарегистрироваться", Toast.LENGTH_LONG).show()
                    }
                }

        }

    }


}