package com.example.foodapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.MainActivity2
import com.example.foodapp.Product2
import com.example.foodapp.R
import org.w3c.dom.Text
import kotlin.collections.ArrayList

class CartAdapter(private var context: Context, private val productsList: ArrayList<Product2>) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
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

        holder.plus.setOnClickListener {
            val currentNumberText = holder.number.text.toString()
            if (currentNumberText.isNotEmpty()) {
                val currentNumber: Int = currentNumberText.toInt()
                holder.number.text = (currentNumber + 1).toString()
            }
        }

        holder.min.setOnClickListener {
            val currentNumberText = holder.number.text.toString()
            if (currentNumberText.isNotBlank() && currentNumberText.matches(Regex("-?\\d+"))) {
                val currentNumber: Int = currentNumberText.toInt()
                if (currentNumber > 1) {
                    holder.number.text = (currentNumber - 1).toString()
                }
            }
        }



    }





    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var title:TextView = itemView.findViewById(R.id.title)
        var price: TextView = itemView.findViewById(R.id.price)
        var item: LinearLayout = itemView.findViewById(R.id.relativeLayout)
        var image: ImageView = itemView.findViewById(R.id.img)

        val min: TextView = itemView.findViewById(R.id.min)
        val plus: TextView = itemView.findViewById(R.id.plus)
        val number:TextView = itemView.findViewById(R.id.number)

    }


    override fun getItemCount(): Int {
        return productsList.size
    }


}
