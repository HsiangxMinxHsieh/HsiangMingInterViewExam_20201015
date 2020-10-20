package com.timmymike.hsiangminginterviewexam_20201015

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.timmymike.hsiangminginterviewexam_20201015.databinding.ActivityMemberDetailBinding
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.MemberDetailViewModel
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.MemberRepository
import com.timmymike.hsiangminginterviewexam_20201015.mvvm.ViewMemberFactory
import com.timmymike.hsiangminginterviewexam_20201015.tools.logi

class MemberDetailActivity : AppCompatActivity() {
    companion object {
        const val KEY_USER_ID = "KEY_USER_ID"

    }

    private val activity = this
    private var userId: String? = null

    //    private var loginStatus = LoginMethod.Login
    private lateinit var viewModel: MemberDetailViewModel
    private lateinit var memberBinding: ActivityMemberDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi("MemberDetailActivity", "啟動了")
        memberBinding = DataBindingUtil.setContentView(activity, R.layout.activity_member_detail)

        initView()

        initData()

        initMvvm()

        initObserver()

    }

    private fun initData() {
        try {
            userId = intent.getStringExtra(KEY_USER_ID) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        if (actionBar != null) {
            actionBar?.hide()
        }
    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // Do nothing
//        }
//    }

    private fun initMvvm() {
        viewModel = ViewModelProvider(activity, ViewMemberFactory(MemberRepository(activity.applicationContext, userId ?: "0"), application, userId ?: "0")).get(MemberDetailViewModel::class.java)

        memberBinding.viewModel = viewModel
        memberBinding.lifecycleOwner = activity
    }

    private fun initObserver() {
        viewModel.liveNeedFinish.observe(activity, Observer {
            if (it)
                activity.finish()
        })

        viewModel.liveBlogUrl.observe(activity, Observer {
            try {
                val url = if (it.startsWith("http") ) it else "https://$it"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                activity.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }
}
