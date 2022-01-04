package com.jmilham.scrollingfeed.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jmilham.scrollingfeed.models.JwVideo
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val liveVideos: MutableLiveData<ArrayList<JwVideo>> = MutableLiveData()

    fun loadSomeVideoList() {
        // TODO: Bring in some list of videos? Probably from playlists

        val client = OkHttpClient()

        // a list of channels
        val request: Request = Request.Builder()
            .url("https://cdn.jwplayer.com/v2/playlists/zAdW5unD")
            .get()
            .addHeader("Accept", "application/json; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                liveVideos.postValue(ArrayList())
            }

            override fun onResponse(call: Call, response: Response) {
                // response.body should have json
                val bodyString: String =
                    if (response.body() != null) response.body()!!.string() else ""
                val bodyObject = JSONObject(bodyString)

                try {
                    val typeToken = object : TypeToken<java.util.ArrayList<JwVideo>>() {}.type
                    val list = bodyObject.getJSONArray("playlist")
                    liveVideos.postValue(Gson().fromJson(list.toString(), typeToken))
                } catch (exception: Exception) {
                    // bad json
                    liveVideos.postValue(ArrayList())
                }
            }
        })
    }
}
