package com.kedvick.ai.ui.contactus.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ItemContactUsProblemBinding

class ContactUsCategoriesAdapter(
    var context: Context,
    private val itemClickedInterface: ItemClickedInterface
) :
    RecyclerView.Adapter<ContactUsCategoriesAdapter.MyViewHolder>() {
    private var categories: MutableList<String> = arrayListOf()
    private var selectedPosition = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setCategories(categories: MutableList<String>) {
        this.categories = categories
        selectedPosition = 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemContactUsProblemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvCategory.text = categories[position]
        if (selectedPosition == position) {
            holder.binding.rlCategory.background =
                context.getDrawable(R.drawable.bg_main_color_without_stroke_corner_5sdp)
            holder.binding.tvCategory.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.binding.rlCategory.background =
                context.getDrawable(R.drawable.bg_unselected_category)
            holder.binding.tvCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white_60
                )
            )
        }
        holder.itemView.setOnClickListener {
            setSelected(position)
            itemClickedInterface.onItemClicked(categories!![position])
        }
    }

    override fun getItemCount(): Int {
        return if (categories.isNotEmpty()) {
            categories.size
        } else 0
    }

    private fun setSelected(position: Int) {
        val oldPos = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPos)
        notifyItemChanged(position)
    }

    fun selectedCategory(category: String) {
        for (i in categories.indices) {
            if (categories[i] == category) {
                setSelected(i)
                break
            }
        }
    }

    inner class MyViewHolder(var binding: ItemContactUsProblemBinding) : RecyclerView.ViewHolder(
        binding.getRoot()
    )

    interface ItemClickedInterface {
        fun onItemClicked(category: String)
    }
}
