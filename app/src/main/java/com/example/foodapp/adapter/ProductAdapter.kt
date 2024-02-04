package com.example.foodapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.MainActivity2
import com.example.foodapp.Product
import com.example.foodapp.R
import java.util.ArrayList

class ProductAdapter(private var context: Context, private val productsList: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = productsList[position].title.toString()
        holder.price.text = productsList[position].price.toString() + " тг"

        Glide.with(context).load(productsList[position].img).into(holder.image)



        holder.item.setOnClickListener {
            val i = Intent(context, MainActivity2::class.java)
            i.putExtra("title", productsList[position].title)
            i.putExtra("price", productsList[position].price)
            i.putExtra("img", productsList[position].img)
            i.putExtra("id", productsList[position].id)

            context.startActivity(i)


        }
    }





    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var title:TextView = itemView.findViewById(R.id.title)
        var price: TextView = itemView.findViewById(R.id.price)
        var item: RelativeLayout = itemView.findViewById(R.id.relativeLayout)
        var image: ImageView = itemView.findViewById(R.id.img)
    }


    override fun getItemCount(): Int {

        val list:Int = productsList.size

        return productsList.size
    }
}
