package zeljko.dejan.rpginventorymanager

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringMap(value: String): MutableMap<String, String> {
        val mapType = object : TypeToken<MutableMap<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: MutableMap<String, String>): String {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun fromStringSet(value: String): MutableSet<String> {
        val setType = object : TypeToken<MutableSet<String>>() {}.type
        return Gson().fromJson(value, setType)
    }

    @TypeConverter
    fun fromMutableSet(set: MutableSet<String>): String {
        return Gson().toJson(set)
    }
}

