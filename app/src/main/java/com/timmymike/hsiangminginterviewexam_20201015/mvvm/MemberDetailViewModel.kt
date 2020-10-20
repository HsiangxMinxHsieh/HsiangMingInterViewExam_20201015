package com.timmymike.hsiangminginterviewexam_20201015.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timmymike.hsiangminginterviewexam_20201015.api.ApiConnect
import com.timmymike.hsiangminginterviewexam_20201015.api.UserDetailModel
import com.timmymike.hsiangminginterviewexam_20201015.tools.logi
import retrofit2.Response

/**======== View Model ========*/

class MemberDetailViewModel(private val context: Application, private val userId: Int) : AndroidViewModel(context) {
    val TAG = javaClass.simpleName
    var personData:UserDetailModel? = null
    var avatarUrl = ""
    var name = ""
    var bio = ""
    var login = ""
    var site_admin = false
    var location = ""
    var blog = ""

    val liveNeedFinish by lazy { MutableLiveData<Boolean>() }

    init {
        logi(TAG, "userId===>$userId")
        initView()
    }

    private fun initView() {
        val personData = getPersonDetailFromApi()?:return
        avatarUrl = personData.avatarUrl
        name = personData.name
        bio = personData.bio?:""
        login = personData.login
        site_admin = personData.siteAdmin
        location = personData.location?:""
        blog = personData.blog
    }

    private fun getPersonDetailFromApi(): UserDetailModel? {
        val cell = ApiConnect.getService().getUserDetail(userId.toString())
        logi(TAG, "Start Call API,To Get getPersonDetailFromApi Method")
        var response: Response<UserDetailModel>? = null
        try {
            response = cell.execute()
            logi(TAG, "getPersonDetailFromApi Send Data is===>${response ?: "null"}")
            if (response.isSuccessful) {
                logi(TAG, "getPersonDetailFromApi Get Data is ${response?.body()} ")
                return response?.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response?.body()
    }


    fun back() {
        liveNeedFinish.postValue(true)
    }
}

class ViewMemberFactory(private val application: Application, private val userId: Int) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MemberDetailViewModel(application, userId) as T
    }
}