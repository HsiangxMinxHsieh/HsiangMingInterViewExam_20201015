package com.timmymike.hsiangminginterviewexam_20200629

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timmymike.hsiangminginterviewexam_20200629.mvvm.Repository
import com.timmymike.hsiangminginterviewexam_20200629.mvvm.UserAdapter
import com.timmymike.hsiangminginterviewexam_20200629.mvvm.UserViewModel
import com.timmymike.hsiangminginterviewexam_20200629.mvvm.ViewModelFactory
import com.timmymike.hsiangminginterviewexam_20200629.api.UserModel
import com.timmymike.hsiangminginterviewexam_20200629.databinding.ActivityMainBinding
import com.timmymike.hsiangminginterviewexam_20200629.tools.BaseSharePreference
import com.timmymike.hsiangminginterviewexam_20200629.tools.logi
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    private val context: Context = this
    private val activity = this

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)
//        setContentView(R.layout.activity_main)

        initView()

    }

    private fun initView() {
        //default Setting：
        BaseSharePreference.setNowShowSize(context,100)

        BaseSharePreference.setNowGetIndex(context, 0)

        val nowGetIndex = BaseSharePreference.getNowStartIndex(context)
        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(context, nowGetIndex), context)).get(UserViewModel::class.java)

        mainBinding.viewModel = viewModel
        mainBinding.lifecycleOwner = activity

        mainBinding.rvUserList.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
        }

        adapter = UserAdapter(viewModel)
        mainBinding.rvUserList.adapter = adapter

        viewModel.listLiveData.observe(this,
            Observer<ArrayList<UserModel>> {
                logi(TAG, "now Data size is===>${it.size}")
                adapter.list = viewModel.listLiveData.value
                adapter.notifyDataSetChanged()
                activity.title = "${context.getString(R.string.app_name)} Number of items：${it.size}"
            })
    }
}
