package com.timmymike.hsiangminginterviewexam_20200629

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.timmymike.hsiangminginterviewexam_20200629.api.ApiConnect
import com.timmymike.hsiangminginterviewexam_20200629.api.UserModel
import com.timmymike.hsiangminginterviewexam_20200629.base.BaseRecyclerViewViewModelAdapter
import com.timmymike.hsiangminginterviewexam_20200629.databinding.ActivityMainBinding
import com.timmymike.hsiangminginterviewexam_20200629.tools.BaseSharePreference
import com.timmymike.hsiangminginterviewexam_20200629.tools.logi
import com.timmymike.hsiangminginterviewexam_20200629.tools.logiAllData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

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

//        getData()
//        mainBinding.pgLoading.visibility = View.GONE

        initView()

    }

    private fun initView() {


        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(context, 3), context)).get(UserViewModel::class.java)
        mainBinding.viewModel = viewModel
        mainBinding.lifecycleOwner = activity

        adapter = UserAdapter(context)
        mainBinding.rvUserList.adapter = adapter

    }


//    private fun getData() {
//        mBinding.tvNoDataFound.visibility = View.GONE
//        mBinding.pgLoading.visibility = View.VISIBLE
//        GlobalScope.launch {
//            //First need check BaseSharePreference have get this Index,If not , call API
//            logi(TAG, "alreadygetIndexArray is ===>$alreadygetIndexArray")
//            if (!alreadygetIndexArray.contains(startGetIndex))
//                getFromApiUserData(startGetIndex)
//
//            // printData To check
//            val dataSet = BaseSharePreference.getUserListData(context, startGetIndex)
//            logi(TAG, "The Stored Data is Below,total ${dataSet.size} count")
//            dataSet.logiAllData()
//
//            if (dataSet.size < 100)
//                getFromApiUserData(dataSet.last().id)
//
//            Handler(Looper.getMainLooper()).post {
//                mBinding.pgLoading.visibility = View.GONE
//                if(dataSet.isEmpty())
//                    mBinding.tvNoDataFound.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    @Throws(Exception::class)
//    private fun getFromApiUserData(start: Int): ResponseBody? {
//        if (alreadygetIndexArray.contains(start))
//            return null
//        val cell = ApiConnect.getService(context).getUserData(start)
//        logi(TAG, "Start Call API,To Get getFromApiUserData Method")
//
//        val response = cell.execute()
//        logi(TAG, "getFromApiUserData Send Data is===>${response ?: "null"}")
//        if (response.isSuccessful) {
//            logi(TAG, "getFromApiUserData Get Data is Below,total ${response?.body()?.size ?: 0} count")
//            BaseSharePreference.saveUserListData(context, response?.body())
//
//            // store have get Index to BaseSharePreference
//
//            alreadygetIndexArray.add(start)
//            BaseSharePreference.setGetIndexs(context, alreadygetIndexArray)
//
//            response?.body()?.logiAllData()
//            if (limitGetIndex > start)
//                getFromApiUserData(start + 30)
//        }
//        return response.errorBody()
//    }

    /**======== MVVM ========*/

    interface IRepository {

        fun getItems(itemCallback: ItemCallback)

        interface ItemCallback {

            fun onItemsResult(items: List<UserModel>)
        }
    }

    class Repository(val context: Context, val startGetIndex: Int) : IRepository {
        val TAG = javaClass.simpleName
        override fun getItems(itemCallback: IRepository.ItemCallback) {
//            val list = mutableListOf<UserModel>()
            // printData To check
            val dataSet = BaseSharePreference.getUserListData(context, startGetIndex)
//            logi(TAG, "The Stored Data is Below,total ${dataSet.size} count")
//            dataSet.logiAllData()
            itemCallback.onItemsResult(dataSet.toList())
        }
    }

    class UserViewModel(private val repository: IRepository, val context: Context) : ViewModel() {
        val TAG = javaClass.simpleName
        val listLiveData: MutableLiveData<List<UserModel>> by lazy { MutableLiveData<List<UserModel>>() }
        val liveLoadingOver: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() } // According this value To Show now Status

        init {
            getData()
        }

        private val alreadygetIndexArray by lazy { BaseSharePreference.getGetIndexs(context) }
        var limitGetIndex = 120
        var startGetIndex = 0
        val empty: LiveData<Boolean> by lazy {
            Transformations.map(listLiveData) {
                it.isEmpty() || it[0].id == 0
            }
        }

        private fun getData() {
//            logi(TAG, "010 now loadingOver===>${loadingOver?:"nothing to show"}")
            liveLoadingOver.postValue(false)
//            logi(TAG, "020 now loadingOver===>$loadingOver")
            repository.getItems(object : IRepository.ItemCallback {
                override fun onItemsResult(items: List<UserModel>) {
                    GlobalScope.launch {
                        listLiveData.postValue(items)
                        delay(3000)
                        liveLoadingOver.postValue(true)
                    }

//                    logi(TAG, "030 now loadingOver===>$loadingOver")
                }
            })

//            logi(TAG," now loadingOVer is===>${loadingOver?.value?:"it is no data!!!"}")
////            mBinding.tvNoDataFound.visibility = View.GONE
////            mBinding.pgLoading.visibility = View.VISIBLE
//            GlobalScope.launch {
//                //First need check BaseSharePreference have get this Index,If not , call API
//                logi(TAG, "alreadygetIndexArray is ===>$alreadygetIndexArray")
//                if (!alreadygetIndexArray.contains(startGetIndex))
//                    getFromApiUserData(startGetIndex)
//
//                // printData To check
//                val dataSet = BaseSharePreference.getUserListData(context, startGetIndex)
//                logi(TAG, "The Stored Data is Below,total ${dataSet.size} count")
//                dataSet.logiAllData()
//
//                if (dataSet.size < 100)
//                    getFromApiUserData(dataSet.last().id)
//
////                Handler(Looper.getMainLooper()).post {
//                liveLoadingOver.postValue(true)
//                logi(TAG,"此時的loadingOVer是===>${loadingOver.value}")
//////                    mBinding.pgLoading.visibility = View.GONE
//////                    if(dataSet.isEmpty())
//////                        mBinding.tvNoDataFound.visibility = View.VISIBLE
////                }
//            }
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

    class ViewModelFactory(private val repository: Repository, private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                return UserViewModel(repository, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }


    private class UserAdapter(val context: Context) : BaseRecyclerViewViewModelAdapter<UserModel>(context, R.layout.adapter_user_list) {
        override fun initViewHolder(viewHolder: ViewHolder) {

        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: UserModel) {

        }

        override fun onItemClick(view: View, position: Int, data: UserModel): Boolean {
            return false
        }

        override fun onItemLongClick(view: View, position: Int, data: UserModel): Boolean {
            return false
        }

        override fun search(constraint: CharSequence, list: ArrayList<UserModel>): ArrayList<UserModel> {
            return list
        }


    }

}
