package br.edu.ifsp.scl.sdm.photos.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import br.edu.ifsp.scl.sdm.photos.R
import br.edu.ifsp.scl.sdm.photos.adapter.PhotoAdapter
import br.edu.ifsp.scl.sdm.photos.adapter.ProductImageAdapter
import br.edu.ifsp.scl.sdm.photos.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.photos.model.DummyJSONAPI
import br.edu.ifsp.scl.sdm.photos.model.PhotoItem
import br.edu.ifsp.scl.sdm.photos.model.Product
import com.android.volley.toolbox.ImageRequest

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val photoList: MutableList<PhotoItem> = mutableListOf()
    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(this, photoList)
    }

    private val productImageList: MutableList<Bitmap> = mutableListOf()
    private val productImageAdapter: ProductImageAdapter by lazy {
        ProductImageAdapter(this, productImageList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = getString(R.string.app_name)
        })

        amb.photosSp.apply {
            adapter = photoAdapter
            onItemSelectedListener =  object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    val size = productImageList.size
                    productImageList.clear()
                    productImageAdapter.notifyItemRangeRemoved(0, size)
                   // retrieveProductImages(photoList[position])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // NSA
                }
            }
        }

//        amb.productImagesRv.apply {
//            layoutManager = LinearLayoutManager(this@MainActivity)
//            adapter = productImageAdapter
//        }

        retrievePhotos()
    }

    private fun retrievePhotos() = DummyJSONAPI.PhotoListRequest(
        { photoList ->
            photoList.also {
                photoAdapter.addAll(it)
            }
        },
        {
            Toast.makeText(this, "Request Problem!", Toast.LENGTH_SHORT).show()
        }).also {
        DummyJSONAPI.getInstance(this).addToRequestQueue(it)
    }

    private fun retrieveProductImages(product: Product) =
        product.images.forEach{ imageUrl ->
            ImageRequest(imageUrl,
                { response ->
                    productImageList.add(response)
                    productImageAdapter.notifyItemInserted(productImageList.lastIndex)

                }, 0, 0,ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, {
                    Toast.makeText(this, "Request Problem!", Toast.LENGTH_SHORT).show()
                }).also {
                    DummyJSONAPI.getInstance(this).addToRequestQueue(it)
            }
        }

}