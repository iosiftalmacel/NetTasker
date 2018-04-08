package com.oryoncorp.apps.nettaskersample.activities


import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View

import com.oryoncorp.apps.netload.NetTasker
import com.oryoncorp.apps.netload.requests.read.ModelDownload
import com.oryoncorp.apps.nettaskersample.adapters.ExampleRecyclerAdapter
import com.oryoncorp.apps.nettaskersample.models.ContainerData
import com.oryoncorp.apps.nettaskersample.utils.AutofitRecyclerView
import com.oryoncorp.apps.nettaskersample.R

class ExampleListActivity : AppCompatActivity() {
    lateinit var adapter: ExampleRecyclerAdapter
    var URL = "https://ri.nn4m.net/RI/sv5/api/public/index.php/category/2508/products.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_container)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        toolbar.setTitleTextColor(Color.BLACK)

        adapter = ExampleRecyclerAdapter(this, null)

        NetTasker.request(ModelDownload(this, ContainerData::class.java){
            it.url = URL
            it.onComplete = {
                adapter.setItems(it.Products)
                adapter.notifyDataSetChanged()
            }
        })

        val recyclerView = findViewById<AutofitRecyclerView>(R.id.products_recycler)
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}