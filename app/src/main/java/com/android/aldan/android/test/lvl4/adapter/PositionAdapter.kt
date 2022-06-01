package com.android.aldan.android.test.lvl4.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.aldan.android.test.lvl4.PositionDetailActivity
import com.android.aldan.android.test.lvl4.R
import com.android.aldan.android.test.lvl4.databinding.FragmentHomePositionItemBinding
import com.android.aldan.android.test.lvl4.gone
import com.android.aldan.android.test.lvl4.model.PositionResponseItem
import com.android.aldan.android.test.lvl4.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PositionAdapter(private val data: ArrayList<PositionResponseItem>) :
    RecyclerView.Adapter<PositionAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = FragmentHomePositionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    fun refreshAdapter(data: ArrayList<PositionResponseItem>) {
        data.let {
            this.data.addAll(it)
        }

        notifyDataSetChanged()
    }

    fun clear() {
        this.data.clear()
        notifyItemRangeRemoved(0, data.size ?: 0)
    }

    override fun getItemCount(): Int = data.size ?: 0

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }

    inner class Holder(private val binding: FragmentHomePositionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(get: PositionResponseItem?) {

            if (get == null) {
                binding.root.gone()
            } else {
                binding.root.visible()

                Glide.with(binding.root.context).load(get.companyLogo)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.ivPositionCompanyLogo)

                binding.tvPositionTitle.text = get.title
                binding.tvPositionCompany.text = get.company
                binding.tvPositionLocation.text = get.location

                binding.root.setOnClickListener {
                    val intent = Intent(binding.root.context, PositionDetailActivity::class.java)
                    intent.putExtra("position_id", get.id)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }
}