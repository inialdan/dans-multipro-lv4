package com.android.aldan.android.test.lvl4.ui.home

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.aldan.android.test.lvl4.*
import com.android.aldan.android.test.lvl4.adapter.PositionAdapter
import com.android.aldan.android.test.lvl4.databinding.FragmentHomeBinding
import com.android.aldan.android.test.lvl4.network.NetworkConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private var myCompositeDisposable: CompositeDisposable? = null

    private var adapterBrand: PositionAdapter? = null

    private var isLoading = true
    private var page = 0
    private var totalPage = 0

    private var description: String? = null
    private var location: String? = null
    private var fulltime = false

    private var isShowFilter = false

    private val waitingTimeSearch: Long = 500
    private var cdtSaveSearch: CountDownTimer? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.clFilter.gone()

        view()

        return root
    }

    private fun view() {

        binding.srPosition.setOnRefreshListener {
            binding.rlPageLoadingScreen.gone()

            myCompositeDisposable?.clear()
            adapterBrand?.clear()
            fulltime = false
            location = null
            binding.etFilterSearch.text.clear()
            binding.etFilterSearch.clearFocus()
            binding.scFilterFulltime.isChecked = false
            binding.etFilterLocation.text.clear()
            binding.etFilterLocation.clearFocus()

            onLoadData()

            hideKeyboard(binding.root.context as Activity)

            binding.srPosition.isRefreshing = false
        }

        binding.etFilterSearch.doOnTextChanged { text, _, _, _ ->
            if (text?.length!! > 1) {
                description
            }
        }

        binding.etFilterSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(data: Editable) {

            }

            override fun beforeTextChanged(data: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(data: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (data.isNotEmpty()) {
                    description = data.toString()

                    if (cdtSaveSearch != null) {
                        cdtSaveSearch?.cancel()
                    }

                    cdtSaveSearch = object : CountDownTimer(waitingTimeSearch, 500) {
                        override fun onTick(millisUntilFinished: Long) {
                            Log.d(
                                tag,
                                "CDT Search seconds remaining: " + millisUntilFinished / 1000
                            )
                        }

                        override fun onFinish() {
                            onLoadData()
                        }
                    }

                    (cdtSaveSearch as CountDownTimer).start()
                } else {
                    description = null
                    cdtSaveSearch?.cancel()
                    onLoadData()
                }
            }
        })

        binding.nsvLayoutPositionList.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->

                if (v.getChildAt(v.childCount - 1) != null) {
                    if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight && scrollY > oldScrollY) {
                        val thisLayoutManager =
                            binding.rvPositionData.layoutManager as LinearLayoutManager
                        val countItem = thisLayoutManager.itemCount

                        val lastVisiblePosition =
                            thisLayoutManager.findLastCompletelyVisibleItemPosition()
                        val isLastPosition = countItem.minus(1) == lastVisiblePosition

                        if (!isLoading && isLastPosition && page < totalPage) {
                            showLoading(true)
                            page = page.plus(1)
                            doLoadData()
                        }
                    }
                }
            })

        binding.ivShowFilter.setOnClickListener {
            if (isShowFilter) {
                binding.ivShowFilter.setImageResource(R.drawable.ic_caret_down_24dp)
                isShowFilter = false
                binding.clFilter.gone()
            } else {
                binding.ivShowFilter.setImageResource(R.drawable.ic_caret_up_24dp)
                isShowFilter = true
                binding.clFilter.visible()
            }
            hideKeyboard(binding.root.context as Activity)
        }

        Log.d("DataHomeFragment", "Fulltime: $fulltime")
        binding.scFilterFulltime.apply {
            isChecked = fulltime
            setOnCheckedChangeListener { _, checked ->
                fulltime = checked
                Log.d("DataHomeFragment", "Fulltime: $fulltime")
            }
        }

        binding.btnApplyFilter.setOnClickListener {
            location = if (!binding.etFilterLocation.text.isNullOrEmpty()) {
                binding.etFilterLocation.text.toString()
            } else {
                null
            }

            onLoadData()

            binding.etFilterSearch.clearFocus()
            binding.etFilterLocation.clearFocus()
            hideKeyboard(binding.root.context as Activity)
        }

        onLoadData()
    }

    private fun onLoadData() {
        binding.clLayoutPositionList.gone()
        myCompositeDisposable = CompositeDisposable()
        page = 1
        totalPage = 0

        doLoadData()
    }

    private fun doLoadData() {
        Log.d(tag, "page: $page")

        if (page == 1) {
            showLoading(false)
        } else {
            showLoading(true)
        }

        myCompositeDisposable?.add(
            NetworkConfig()
                .getService()
                .getPositionsScroll(
                    page = page,
                    description = description,
                    location = location,
                    full_time = fulltime,
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(tag, "onSuccess")
                        if (page == 1) {
                            binding.clLayoutPositionList.visible()
                            adapterBrand = PositionAdapter(it)
                            binding.rvPositionData.apply {
                                layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                adapter = adapterBrand
                            }
                            binding.rlPageLoadingScreen.gone()
                        } else {
                            adapterBrand?.refreshAdapter(it)
                        }

                        // Static from max page on API, because there is response for pagination max limit, page, etc.
                        totalPage = 2
                    },
                    { t: Throwable ->
                        if (!t.message.isNullOrEmpty()) {
                            toast("${t.message}")
                        }

                        if (t is HttpException) {
                            if (t.code() == 403) {
                                // 403
                            } else if (t.code() == 404) {
                                // 404
                            } else if (t.code() == 500) {
                                // 500
                            } else if (t.code() == 502) {
                                // 502
                            }
                        }
                    },
                    {
                        hideLoading()
                        Log.d(tag, "onComplete")
                    }
                )
        )
    }

    private fun showLoading(isRefresh: Boolean) {
        if (isRefresh) {
            binding.pbOnLoading.visible()
            binding.rlPageLoadingScreen.gone()
        } else {
            binding.pbOnLoading.gone()
            binding.rlPageLoadingScreen.visible()
        }

        binding.rvPositionData.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        binding.pbOnLoading.gone()
        binding.rvPositionData.visible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}