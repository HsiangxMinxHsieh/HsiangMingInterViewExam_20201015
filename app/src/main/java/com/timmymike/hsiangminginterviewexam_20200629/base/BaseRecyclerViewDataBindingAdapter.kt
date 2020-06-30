package com.timmymike.hsiangminginterviewexam_20200629.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


public abstract class BaseRecyclerViewDataBindingAdapter<T>(private val context: Context, private val layoutID: Int) :
    androidx.recyclerview.widget.RecyclerView.Adapter<BaseRecyclerViewDataBindingAdapter<T>.ViewHolder>(), View.OnClickListener, View.OnLongClickListener, Filterable {
    private var list = ArrayList<T>()

    private val myInflater: LayoutInflater? = null
    private var sortKey: String? = null

    private val mFilter = ItemFilter()

    /**
     * searched list
     */
    private var filteredList: ArrayList<T>? = ArrayList<T>()

//    private var mOnItemClickListener: RecyclerViewTool.OnRecyclerViewItemClickListener<T>? = null
//    private var mOnItemLongClickListener: RecyclerViewTool.OnRecyclerViewItemLongClickListener<T>? = null

    /**
     * init ViewHolder
     * */
    abstract fun initViewHolder(viewHolder: ViewHolder)

    /**
     * every time ViewHolder change
     * */
    abstract fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: T)

    /**
     * click event
     * @return true are already process
     * */
    abstract fun onItemClick(view: View, position: Int, data: T): Boolean

    /**
     * long click event
     * @return true are already process
     * */
    abstract fun onItemLongClick(view: View, position: Int, data: T): Boolean

    /**
     * search
     * if no need , return list
     * */
    abstract fun search(constraint: CharSequence, list: ArrayList<T>): ArrayList<T>

    override fun getItemViewType(position: Int): Int {
        //            return 0;
        return super.getItemViewType(position)
    }

    inner class ViewHolder(var binding: ViewDataBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener(this@BaseRecyclerViewDataBindingAdapter)
            binding.root.setOnLongClickListener(this@BaseRecyclerViewDataBindingAdapter)
            initViewHolder(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutID, parent, false)
        val viewHolder = ViewHolder(binding)
        if (viewType == 0) {
            //according different viewType change view style
        }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.root.tag = position
        val viewType = viewHolder.itemViewType

        val data = filteredList!![position]

        onBindViewHolder(viewHolder, position, data)
    }

    override fun getItemCount(): Int {
        return filteredList!!.size
    }

    fun getItem(position: Int): T {
        return filteredList!![position]
    }

    fun addItem(list: List<T>) {
        this.list = list as ArrayList<T>
        filter.filter("")
    }

    fun addItem(list: ArrayList<T>) {
        this.list = list
        filter.filter("")
    }

    fun addItem(data: T) {
        list.add(data)
        filter.filter("")
    }

    fun clear(data: T) {
        list.clear()
        filter.filter("")
    }

//    fun setOnLongClickListener(listener: RecyclerViewTool.OnRecyclerViewItemLongClickListener<T>) {
//        mOnItemLongClickListener = listener
//    }
//
//    fun setOnItemClickListener(listener: RecyclerViewTool.OnRecyclerViewItemClickListener<T>) {
//        this.mOnItemClickListener = listener
//    }


    override fun onClick(v: View) {
        onItemClick(v, v.tag as Int, getItem(v.tag as Int))
    }

    override fun onLongClick(v: View): Boolean {
        return onItemLongClick(v, v.tag as Int, getItem(v.tag as Int))
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    private inner class ItemFilter : Filter() {

        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
//            var constraint = constraint
//
//            constraint = constraint.toString().toLowerCase(Locale.getDefault())
//
//            val results = Filter.FilterResults()
//            val count = list.size
//            val nlist = ArrayList<T>()
//            val arr = arrayOf<String>(Product.PRODUCT_NAME, Product.PRODUCT_SHORT_NAME, Product.BARCODE)
//            for (i in 0 until count) {
//                val data = list[i]
//
//                var isAdd = false
//                if (constraint.toString() == "") {
//                    isAdd = true
//                }
//                var j = 0
//                while (isAdd == false && j < arr.size) {
//                    val key = arr[j]
//                    val `val` = data.getString(key, "").toLowerCase(Locale.getDefault())
//                    if (`val`.contains(constraint) && `val`.indexOf(constraint.toString()) == 0) {
//                        isAdd = true
//                        break
//                    }
//                    j++
//                }
//                if (isAdd == true) {
//                    nlist.add(data)
//                }
//            }

            val results = Filter.FilterResults()
            val nlist = search(constraint, list)
            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            val arrayList = results.values as ArrayList<T>
            filteredList = arrayList
            if (arrayList == null) {
                filteredList = ArrayList<T>()
            }

            /** the sort function has no use in temp*/
//            if (sortKey != null && sortKey.equals("") == false) {
//                sort(sortKey);
//            }
            notifyDataSetChanged()
        }
    }
}