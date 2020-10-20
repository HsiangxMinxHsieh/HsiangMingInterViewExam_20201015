package com.timmymike.hsiangminginterviewexam_20201015

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timmymike.hsiangminginterviewexam_20201015.api.UserModel
import com.timmymike.hsiangminginterviewexam_20201015.databinding.ActivityMainBinding
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.MainRepository
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.MainViewModel
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.UserAdapter
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.ViewModelFactory
import com.timmymike.hsiangminginterviewexam_20201015.tools.BaseSharePreference
import com.timmymike.hsiangminginterviewexam_20201015.tools.logi
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    private val context: Context = this
    private val activity = this

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)
//        setContentView(R.layout.activity_main)

        initMvvm()

        initRecyclerView()

        initObserver()

    }

    private fun initMvvm() {
        //default Setting：
        BaseSharePreference.setNowShowSize(context, 100)

        BaseSharePreference.setNowGetIndex(context, 0)

        val nowGetIndex = BaseSharePreference.getNowStartIndex(context)
        viewModel = ViewModelProvider(this, ViewModelFactory(MainRepository(context.applicationContext, nowGetIndex), context.applicationContext)).get(MainViewModel::class.java)

        mainBinding.viewModel = viewModel
        mainBinding.lifecycleOwner = activity

    }

    private fun initRecyclerView() {

        mainBinding.rvUserList.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
        }

        adapter = UserAdapter(viewModel)
        mainBinding.rvUserList.adapter = adapter

    }

    private fun initObserver() {
        viewModel.listLiveData.observe(this,
            Observer<ArrayList<UserModel>> {
                logi(TAG, "now Data size is===>${it.size}")
                adapter.list = viewModel.listLiveData.value
                adapter.notifyDataSetChanged()
//                activity.title = "${context.getString(R.string.app_name)} Number of items：${it.size}"
                activity.title = "GitHub Users"
            })

        viewModel.liveToDetail.observe(activity,
            Observer {
                if (it != "") {
                    val intent = Intent(context, MemberDetailActivity::class.java)
                    intent.putExtra(MemberDetailActivity.KEY_USER_ID, it)
                    activity.startActivity(intent)
                }
                viewModel.liveToDetail.postValue("")
            }
        )
    }

}
