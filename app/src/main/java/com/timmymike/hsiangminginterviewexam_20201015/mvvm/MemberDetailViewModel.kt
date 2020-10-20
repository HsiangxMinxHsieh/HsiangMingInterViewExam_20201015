package com.timmymike.hsiangminginterviewexam_20201015.mvvm

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timmymike.hsiangminginterviewexam_20201015.api.ApiConnect
import com.timmymike.hsiangminginterviewexam_20201015.api.UserDetailModel
import com.timmymike.hsiangminginterviewexam_20201015.tools.BaseSharePreference
import com.timmymike.hsiangminginterviewexam_20201015.tools.logi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

/**======== View Model ========*/

interface MemberInterfaceRepository {
    fun getItems(itemCallback: ItemCallback)
    interface ItemCallback {
        fun onItemsResult(items: UserDetailModel)
    }
}

class MemberRepository(val context: Context, val UserId: String) : MemberInterfaceRepository {
    override fun getItems(itemCallback: MemberInterfaceRepository.ItemCallback) {
        val userData = BaseSharePreference.getUserDetail(context, UserId)
        itemCallback.onItemsResult(userData)
    }

}

class ViewMemberFactory(private val repository: MemberRepository, private val context: Application, private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberDetailViewModel::class.java)) {
            return MemberDetailViewModel(repository, context, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MemberDetailViewModel(val repository: MemberRepository, val context: Application, val userId: String) : AndroidViewModel(context) {
    val TAG = javaClass.simpleName
    var personData: UserDetailModel? = null

    val liveLoadingOver: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() } // According this value To Show now Status
    val liveNeedFinish by lazy { MutableLiveData<Boolean>() }
    val liveUserData by lazy { MutableLiveData<UserDetailModel>() }
    val liveBlogUrl by lazy { MutableLiveData<String>() }

    init {
        logi(TAG, "userId===>$userId")
        initView()
    }

    private fun initView() {
        repository.getItems(object : MemberInterfaceRepository.ItemCallback {

            override fun onItemsResult(items: UserDetailModel) {
                GlobalScope.launch {
                    getPersonDetailFromApi(userId)
                    liveUserData.postValue(BaseSharePreference.getUserDetail(context, userId))
                    liveLoadingOver.postValue(true)
                }
            }
        })
    }

    private fun getPersonDetailFromApi(userId: String) {
        if (BaseSharePreference.getUserDetail(context, userId).id != 0)
            return

        var response: Response<UserDetailModel>? = null
        val cell = ApiConnect.getService().getUserDetail(userId)
        logi(TAG, "Start Call API,To Get getPersonDetailFromApi Method")

        try {
            response = cell.execute()
            logi(TAG, "getPersonDetailFromApi Send Data is===>${response ?: "null"}")
            if (response.isSuccessful) {
                logi(TAG, "getPersonDetailFromApi Get Data is ${response?.body()} ")
                BaseSharePreference.setUserDetail(context, userId, response?.body() ?: UserDetailModel())
                liveLoadingOver.postValue(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        response?.body()


        return
    }

    fun toBlog(blog: String) {
        if (blog.isNotBlank())
            liveBlogUrl.postValue(blog)
    }

    fun back() {
        liveNeedFinish.postValue(true)
    }
}
