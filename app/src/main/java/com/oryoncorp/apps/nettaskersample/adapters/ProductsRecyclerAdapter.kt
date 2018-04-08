package com.oryoncorp.apps.nettaskersample.adapters


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.oryoncorp.apps.netload.NetTasker
import com.oryoncorp.apps.netload.requests.read.ImageDownload
import com.oryoncorp.apps.netload.requests.read.RequestFrom

import com.oryoncorp.apps.nettaskersample.R
import com.oryoncorp.apps.nettaskersample.models.ProductItemData

/**
 * Created by Iosif on 17/03/2018.
 */

class ProductsRecyclerAdapter(private val mContext: Context, private var mItems: Array<ProductItemData>?) : RecyclerView.Adapter<ProductsRecyclerAdapter.ItemViewHolder>() {

    fun setItems(items: Array<ProductItemData>) {
        mItems = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_item, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentData = mItems!![position]
        holder.mTitleView.text = currentData.name
        holder.mCostView.text = "€" + currentData.costEUR

        if (currentData.category != null)
            holder.mDescView.text = currentData.category.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

        holder.request.url = if (currentData.altImage != null && !currentData.altImage.isEmpty()) currentData.altImage else currentData.allImages[0]
        NetTasker.request(holder.request)
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        NetTasker.cancelRequest(holder.request)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return if (mItems == null) 0 else mItems!!.size
    }

    inner class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mTitleView: TextView
        var mDescView: TextView
        var mCostView: TextView
        var mThumbView: ImageView
        var request: ImageDownload

        init {
            mTitleView = v.findViewById(R.id.title)
            mDescView = v.findViewById(R.id.desc)
            mCostView = v.findViewById(R.id.cost)
            mThumbView = v.findViewById(R.id.image)

            request = ImageDownload(mThumbView,{
                it.url = ""
                it.from = RequestFrom.Cache
                it.errorDrawable = ColorDrawable(Color.RED)
                it.placeholderDrawable = ColorDrawable(Color.rgb(232, 232, 232))
                it.fadeDuration = 200
                it.saveInMemory = true
                it.saveOnDisk = true
            })
        }
    }
}