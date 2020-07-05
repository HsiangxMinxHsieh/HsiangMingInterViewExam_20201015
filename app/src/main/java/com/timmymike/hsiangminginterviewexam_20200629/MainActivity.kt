package com.timmymike.hsiangminginterviewexam_20200629

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timmymike.hsiangminginterviewexam_20200629.api.ApiConnect
import com.timmymike.hsiangminginterviewexam_20200629.api.UserModel
import com.timmymike.hsiangminginterviewexam_20200629.databinding.ActivityMainBinding
import com.timmymike.hsiangminginterviewexam_20200629.databinding.AdapterUserListBinding
import com.timmymike.hsiangminginterviewexam_20200629.tools.BaseSharePreference
import com.timmymike.hsiangminginterviewexam_20200629.tools.bindImage
import com.timmymike.hsiangminginterviewexam_20200629.tools.logi
import com.timmymike.hsiangminginterviewexam_20200629.tools.logiAllData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        BaseSharePreference.setNowGetIndex(context, 0)
        val nowGetIndex = BaseSharePreference.getNowStartIndex(context)
        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(context, nowGetIndex), context)).get(UserViewModel::class.java)

        mainBinding.viewModel = viewModel
        mainBinding.lifecycleOwner = activity

        adapter = UserAdapter(viewModel)
        mainBinding.rvUserList.adapter = adapter
        mainBinding.rvUserList.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
        }

        viewModel.listLiveData.observe(this,
            Observer<List<UserModel>> {
                logi(TAG,"now Data is===>$it")
                adapter.notifyDataSetChanged()
            })

    }

    /**======== MVVM ========*/

    interface IRepository {

        fun getItems(itemCallback: ItemCallback)

        interface ItemCallback {

            fun onItemsResult(items: ArrayList<UserModel>)
        }
    }

    class Repository(val context: Context, val startGetIndex: Int) : IRepository {
        val TAG = javaClass.simpleName
        override fun getItems(itemCallback: IRepository.ItemCallback) {
//            val list = mutableListOf<UserModel>()
            // printData To check
//            logi(TAG, "startGetIndex ===>$startGetIndex")
            val dataSet = BaseSharePreference.getUserSetData(context, startGetIndex)
//            logi(TAG, "The Stored Data is Below,total ${dataSet.size} count")
//            dataSet.logiAllData()
            val arr = ArrayList<UserModel>()
            arr.addAll(dataSet)
            itemCallback.onItemsResult(arr)
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

        private fun getData() {
            liveLoadingOver.postValue(false)
            repository.getItems(object : IRepository.ItemCallback {
                override fun onItemsResult(items: ArrayList<UserModel>) {
                    GlobalScope.launch {
                        val nowGetIndex = BaseSharePreference.getNowStartIndex(context)
                        items.getFromApiUserData(nowGetIndex)
//                        logi(TAG, "Insert to Post Value before，all Items is Bellow,,,Item Size is ===>${items.size}")
//                        items.logiAllData(TAG)
                        listLiveData.postValue(items)

                        liveLoadingOver.postValue(true)
                    }
                }
            })
        }

        @Throws(Exception::class)
        private fun ArrayList<UserModel>.getFromApiUserData(start: Int) {
            if (alreadygetIndexArray.contains(start))
                return
            val cell = ApiConnect.getService(context).getUserData(start)
            logi(TAG, "Start Call API,To Get getFromApiUserData Method")

            val response = cell.execute()
            logi(TAG, "getFromApiUserData Send Data is===>${response ?: "null"}")
            if (response.isSuccessful) {
                logi(TAG, "getFromApiUserData Get Data is Below,total ${response?.body()?.size ?: 0} count")
                BaseSharePreference.saveUserSetData(context, response?.body())

                // store have get Index to BaseSharePreference

                alreadygetIndexArray.add(start)
                BaseSharePreference.setGetIndexs(context, alreadygetIndexArray)

                this.addAll(response.body() ?: mutableListOf())
                response?.body()?.logiAllData()

                if (limitGetIndex > start)
                    this.getFromApiUserData(start + 30)
                else
                    return
            }
            return
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


    private class UserAdapter(val viewModel: UserViewModel) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
        val TAG = javaClass.simpleName
        var list: List<UserModel>? = viewModel.listLiveData.value

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder.from(parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list!![position]
            logi(TAG, "when onBindViewHolder,,,item is===>$item")
            holder.bind(viewModel, item)
        }

        override fun getItemCount(): Int {
            return list?.count() ?: 0
        }

        class ViewHolder private constructor(private val binding: AdapterUserListBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val TAG = javaClass.simpleName

            fun bind(viewModel: UserViewModel, item: UserModel) {
                logi(TAG, "when bind,,,item is===>$item")
                binding.viewModel = viewModel
                binding.userModel = item
                bindImage(binding.ivAvatar, item.avatarUrl)
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = AdapterUserListBinding.inflate(layoutInflater, parent, false)

                    return ViewHolder(binding)
                }
            }
        }

    }

}
