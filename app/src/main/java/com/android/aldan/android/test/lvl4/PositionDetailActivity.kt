package com.android.aldan.android.test.lvl4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.aldan.android.test.lvl4.databinding.ActivityHomePositionDetailBinding
import com.android.aldan.android.test.lvl4.model.PositionDetailResponse
import com.android.aldan.android.test.lvl4.model.ResponseError
import com.android.aldan.android.test.lvl4.network.NetworkConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PositionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePositionDetailBinding

    private var id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePositionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        id = intent?.getStringExtra("position_id")
        Log.d("DataHomeFragment", "ID $id")

        binding.ivPositionDetail.setOnClickListener {
            onBackPressed()
        }

        binding.srPositionDetail.setOnRefreshListener {
            loadDataPositionDetail()

            binding.srPositionDetail.isRefreshing = false
        }

        loadDataPositionDetail()
    }

    private fun loadDataPositionDetail() {
        NetworkConfig()
            .getService()
            .getPositionsDetail(
                ID = id,
            ).enqueue(object : Callback<PositionDetailResponse> {
                override fun onFailure(call: Call<PositionDetailResponse>, t: Throwable) {
                    toast("${t.message}")
                }

                override fun onResponse(
                    call: Call<PositionDetailResponse>,
                    response: Response<PositionDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        // Company Section

                        val data = response.body()

                        Glide.with(binding.root.context).load(data?.companyLogo)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(binding.ivPositionCompanyLogo)

                        binding.tvPositionDetailCompanyTitle.text = data?.company
                        binding.tvPositionDetailLocation.text = data?.location

                        val website = data?.companyUrl
                        if (website.isNullOrEmpty()) {
                            binding.tvPositionCompanyWebsite.gone()
                        } else {
                            binding.tvPositionCompanyWebsite.apply {
                                visible()
                                setOnClickListener {
                                    val i = Intent(Intent.ACTION_VIEW)
                                    i.data = Uri.parse(website)
                                    startActivity(i)
                                }
                            }
                        }

                        // Job Specification Section
                        binding.tvPositionDetailJobSpecificationTitle.text = data?.title

                        if (data?.type == "Full Time") {
                            binding.tvPositionDetailJobSpecificationFulltime.text = "Yes"
                        } else {
                            binding.tvPositionDetailJobSpecificationFulltime.text = "No"
                        }

                        binding.wvPositionDetailJobSpecificationDescription.loadDataWithBaseURL(
                            null,
                            data?.description.toString(),
                            "text/html",
                            "utf-8",
                            null
                        )
                    } else {
                        val responseErrorUtils = Gson().fromJson(
                            response.errorBody()!!.charStream(), ResponseError::class.java
                        )

                        toast("${responseErrorUtils.error}")

                        when {
                            response.code() == 403 -> {
                                // 403
                            }
                            response.code() == 404 -> {
                                // 404
                            }
                            response.code() == 500 -> {
                                // 500
                            }

                            response.code() == 502 -> {
                                // 502
                            }
                            else -> {

                            }
                        }
                    }
                }
            })
    }
}