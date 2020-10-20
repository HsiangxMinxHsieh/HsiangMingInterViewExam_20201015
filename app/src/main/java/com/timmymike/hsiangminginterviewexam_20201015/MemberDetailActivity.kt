package com.timmymike.hsiangminginterviewexam_20201015

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.timmymike.hsiangminginterviewexam_20201015.databinding.ActivityMemberDetailBinding
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.MemberDetailViewModel
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.ViewMemberFactory

class MemberDetailActivity : AppCompatActivity() {
    companion object {
        const val KEY_USER_ID = "KEY_USER_ID"

    }

    private val activity = this
    private var userId: Int? = null

    //    private var loginStatus = LoginMethod.Login
    private lateinit var viewModel: MemberDetailViewModel
    private lateinit var memberBinding: ActivityMemberDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        memberBinding = DataBindingUtil.setContentView(activity, R.layout.activity_member_detail)

        initData()

        initView()

        initMvvm()

        initObserver()

    }

    private fun initData() {
        try {
            userId = intent.getIntExtra(KEY_USER_ID, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {

    }

    private fun initMvvm() {
        viewModel = ViewModelProvider(activity, ViewMemberFactory(application, userId ?: 0)).get(MemberDetailViewModel::class.java)

        memberBinding.viewModel = viewModel
        memberBinding.lifecycleOwner = activity
    }

    private fun initObserver() {
        viewModel.liveNeedFinish.observe(activity, Observer {
            if (it)
                activity.finish()
        })
    }
}
