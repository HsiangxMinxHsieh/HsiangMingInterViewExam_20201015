package com.timmymike.hsiangminginterviewexam_20200629.tools

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timmymike.hsiangminginterviewexam_20200629.api.UserModel
import java.util.*

/**
 * Description:
 * @author Robert Chou didi31139@gmail.com
 * @date 2015/5/27  1:45:58 pm
 * @version
 */
object BaseSharePreference {
    private val TABLENAME = "share"

    /**have get Index list*/
    private val KEY_HAVE_GET_INDEX = "KEY_HAVE_GET_INDEX"

    /**userData in list*/
    private val KEY_USER_DATA_LIST = "KEY_USER_DATA_LIST"

    /**get data start index*/
    private val KEY_GET_DATA_START_INDEX = "KEY_GET_DATA_START_INDEX"

    fun getString(
        context: Context,
        key: String,
        defValues: String,
        tableName: String = TABLENAME
    ): String {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        return sharedPreferences.getString(key, defValues) ?: defValues
    }

    fun putString(context: Context, key: String, value: String, tableName: String = TABLENAME) {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getInt(context: Context, key: String, defValue: Int, tableName: String = TABLENAME): Int {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        return sharedPreferences.getInt(key, defValue)
    }

    fun putInt(context: Context, key: String, value: Int, tableName: String = TABLENAME) {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getLong(
        context: Context,
        key: String,
        defValue: Long,
        tableName: String = TABLENAME
    ): Long {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        return sharedPreferences.getLong(key, defValue)
    }

    fun putLong(context: Context, key: String, value: Long, tableName: String = TABLENAME) {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun getFloat(
        context: Context,
        key: String,
        defValue: Float,
        tableName: String = TABLENAME
    ): Float {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        return sharedPreferences.getFloat(key, defValue)
    }

    fun putFloat(context: Context, key: String, value: Float, tableName: String = TABLENAME) {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.commit()
    }

    fun getBoolean(
        context: Context,
        key: String,
        defValue: Boolean,
        tableName: String = TABLENAME
    ): Boolean {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun putBoolean(context: Context, key: String, value: Boolean, tableName: String = TABLENAME) {
        val sharedPreferences = context.getSharedPreferences(tableName, 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    /** set Start Index in store*/
    fun setNowGetIndex(context: Context, index: Int) {
        putInt(context, KEY_GET_DATA_START_INDEX, index)
    }

    /**get Start Index by store*/
    fun getNowStartIndex(context: Context): Int {
        return getInt(context, KEY_GET_DATA_START_INDEX, 0)
    }

    /** set indexes */
    fun setGetIndexs(context: Context, array: TreeSet<Int>) {
        putString(context, KEY_HAVE_GET_INDEX, Gson().toJson(array))
    }

    /** get indexes */
    fun getGetIndexs(context: Context): TreeSet<Int> {
        var array = TreeSet<Int>()
        try {
            array = Gson().fromJson(getString(context, KEY_HAVE_GET_INDEX, "[]"), object : TypeToken<TreeSet<Int>>() {}.type) //get List data need write like this
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return array
    }

    /**save data from API*/
    fun saveUserSetData(context: Context, dataList: ArrayList<UserModel>?) {
        if (dataList == null || dataList.isEmpty())
            return
        for (user in dataList) {
            putString(context, "$KEY_USER_DATA_LIST ${user.id}", Gson().toJson(user))
        }
    }

    /**get data from store*/
    fun getUserSetData(context: Context, startId: Int): TreeSet<UserModel> {
        val TAG = "getUserListData"
        val set = TreeSet<UserModel>()
        var index = startId
        var getDataSuccess = true
        var failCount = 0
        while (set.size < 100 && getDataSuccess) {

            try {
                val data = Gson().fromJson(getString(context, "$KEY_USER_DATA_LIST $index", ""), UserModel::class.java) ?: null
                if (data != null) {
                    set.add(data).apply {
                        getDataSuccess = this
                        if (getDataSuccess)
                            failCount = 0
                    }
                } else {
                    getDataSuccess = false
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            index++
            // avoid fail process
            if (!getDataSuccess)
                failCount++
//            logi(TAG, "getDataSuccess ==>$getDataSuccess,,,fail Count ===>$failCount")
            getDataSuccess = failCount <= 1000 //1000 is temp value ,it means that has fail 1000 times, then jump out this loop
        }

        return set
    }

}