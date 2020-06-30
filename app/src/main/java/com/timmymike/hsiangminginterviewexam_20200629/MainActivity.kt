package com.timmymike.hsiangminginterviewexam_20200629

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.timmymike.hsiangminginterviewexam_20200629.api.ApiConnect
import com.timmymike.hsiangminginterviewexam_20200629.databinding.ActivityMainBinding
import com.timmymike.hsiangminginterviewexam_20200629.tools.BaseSharePreference
import com.timmymike.hsiangminginterviewexam_20200629.tools.logi
import com.timmymike.hsiangminginterviewexam_20200629.tools.logiAllData
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    private val context: Context = this
    private val activity = this
    private lateinit var mBinding: ActivityMainBinding
    private val alreadygetIndexArray by lazy { BaseSharePreference.getGetIndexs(context) }
    val otherHandlerThread = HandlerThread(javaClass.simpleName + "_otherHandlerThread")
    val otherHandler by lazy {
        otherHandlerThread.start()
        Handler(otherHandlerThread.looper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)
//        setContentView(R.layout.activity_main)

        initData()

    }

    var limitGetIndex = 120
    var startGetIndex = 1

    private fun initData() {
        otherHandler.post {
            Handler(Looper.getMainLooper()).post {
                mBinding.pgLoading.visibility = View.VISIBLE
            }
            //First need check BaseSharePreference have get this Index,If not , call API
            logi(TAG, "alreadygetIndexArray is ===>$alreadygetIndexArray")
            if (!alreadygetIndexArray.contains(startGetIndex))
                getFromApiUserData(startGetIndex)

            // printData To check
            val dataSet = BaseSharePreference.getUserListData(context, startGetIndex)
            logi(TAG, "The Stored Data is Below,total ${dataSet.size} count")
            dataSet.logiAllData()

            if (dataSet.size < 100)
                getFromApiUserData(dataSet.last().id)

            Handler(Looper.getMainLooper()).post {
                mBinding.pgLoading.visibility = View.GONE
            }
        }
    }

    class UserViewModel() {
        var avatar: ObservableField<String> = ObservableField("")
        var name: ObservableField<String> = ObservableField("")
        var status: ObservableField<Int> = ObservableField(0) // According this value To Show now Status
    }

    @Throws(Exception::class)
    private fun getFromApiUserData(start: Int): ResponseBody? {
        if (alreadygetIndexArray.contains(start))
            return null
        val cell = ApiConnect.getService(context).getUserData(start)
        logi(TAG, "Start Call API,To Get getFromApiUserData Method")

        val response = cell.execute()
        logi(TAG, "getFromApiUserData Send Data is===>${response ?: "null"}")
        if (response.isSuccessful) {
            logi(TAG, "getFromApiUserData Get Data is Below,total ${response?.body()?.size ?: 0} count")
            BaseSharePreference.saveUserListData(context, response?.body())

            // store have get Index to BaseSharePreference
            alreadygetIndexArray.add(start)
            BaseSharePreference.setGetIndexs(context, alreadygetIndexArray)

            response?.body()?.logiAllData()
            if (limitGetIndex > start)
                getFromApiUserData(start + 30)
        }
        return response.errorBody()
    }

}
