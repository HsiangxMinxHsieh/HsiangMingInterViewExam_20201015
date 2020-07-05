package com.timmymike.hsiangminginterviewexam_20200629.mvvm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.timmymike.hsiangminginterviewexam_20200629.R
import com.timmymike.hsiangminginterviewexam_20200629.api.ApiConnect
import com.timmymike.hsiangminginterviewexam_20200629.api.UserModel
import com.timmymike.hsiangminginterviewexam_20200629.databinding.AdapterUserListBinding
import com.timmymike.hsiangminginterviewexam_20200629.tools.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**======== MVVM ========*/

interface IRepository {

    fun getItems(itemCallback: ItemCallback)

    interface ItemCallback {

        fun onItemsResult(items: TreeSet<UserModel>)
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
        val arr = TreeSet<UserModel>()
        arr.addAll(dataSet)
        itemCallback.onItemsResult(arr)
    }
}

class UserViewModel(private val repository: IRepository, val context: Context) : ViewModel() {
    val TAG = javaClass.simpleName
    val listLiveData: MutableLiveData<ArrayList<UserModel>> by lazy { MutableLiveData<ArrayList<UserModel>>(ArrayList()) }
    val liveLoadingOver: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() } // According this value To Show now Status

    init {
        getData(BaseSharePreference.getNowStartIndex(context))
    }

    private val alreadygetIndexArray by lazy { BaseSharePreference.getGetIndexs(context) }

    private fun getData(nowGetIndex: Int) {
        liveLoadingOver.postValue(false)
        repository.getItems(object : IRepository.ItemCallback {
            override fun onItemsResult(items: TreeSet<UserModel>) {
                GlobalScope.launch {

                    items.getFromApiUserData(nowGetIndex)

                    logi(TAG, "Insert to Post Value before，all List is Bellow,,,list Size is ===>${items.size}")
                    val list = ArrayList<UserModel>()
                    list.addAll(items.toList())
                    logi(TAG,"Before sublist，list size is===>${list.size}")
                    list.logiAllData(TAG)

                    val resultList =   ArrayList<UserModel>()
                    val maxSize = if(list.size <BaseSharePreference.getNowShowSize(context))  list.size else BaseSharePreference.getNowShowSize(context)
                    resultList.addAll(list.subList(0,maxSize))

                    listLiveData.postValue(resultList)
                    liveLoadingOver.postValue(true)
                }
            }
        })
    }

    fun openItem(itemName: String) {
        Toast.makeText(context, "You clicked $itemName", Toast.LENGTH_SHORT).show()
    }

    @Throws(Exception::class)
    private fun TreeSet<UserModel>.getFromApiUserData(start: Int) {
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


            logi(TAG, "now total size is ==>${this.size},need show Size is===>${BaseSharePreference.getNowShowSize(context)}")

            if (this.size < BaseSharePreference.getNowShowSize(context))

                this.getFromApiUserData(start + 30)
            else
                return
        }
        response?.body().toString()
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


class UserAdapter(val viewModel: UserViewModel) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    val TAG = javaClass.simpleName
    var list: ArrayList<UserModel>? = viewModel.listLiveData.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            logi(TAG, "when onBindViewHolder,,,list is===>$list")
        if (list != null && list!!.isNotEmpty()) {
            val item = list!![position]
//                logi(TAG, "when onBindViewHolder,,,item is===>$item")
            holder.bind(viewModel, item)
        }
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    class ViewHolder private constructor(private val binding: AdapterUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val TAG = javaClass.simpleName

        fun bind(viewModel: UserViewModel, item: UserModel) {
//                logi(TAG, "when bind,,,item is===>$item")
            binding.viewModel = viewModel
            binding.userModel = item
            val corner = 100

            binding.tvStaff.setTextColor(viewModel.context.getColor(R.color.staff_color))
            binding.tvStaff.background = getRectangleBg(viewModel.context, corner, corner, corner, corner, R.color.staff_back, 0, 0)
            bindImage(binding.ivAvatar, item.avatarUrl)
//                binding.executePendingBindings()
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