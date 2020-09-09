package com.example.instagrammedia

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.media_item.view.*
import java.io.IOException

class MainActivity : AppCompatActivity(), AuthenticationListener {
    var token: String? = null
    var appPreferences: AppPreferences? = null
    var authenticationDialog: AuthenticationDialog? = null
    var button: Button? = null
    var info: View? = null
    var adapter: MediaAdapter? = null
    var imageList = ArrayList<MediaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.btn_login)
        info = findViewById(R.id.info)
        appPreferences = AppPreferences()

        //check already have access token
        token = appPreferences?.getString(AppPreferences.TOKEN)
        token?.let { getUserInfoByAccessToken(it) }

        imageList.add(MediaItem("https://i.ibb.co/wBYDxLq/beach.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/gM5NNJX/butterfly.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/10fFGkZ/car-race.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/ygqHsHV/coffee-milk.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/7XqwsLw/fox.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/L1m1NxP/girl.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/wc9rSgw/desserts.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/wdrdpKC/kitten.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/dBCHzXQ/paris.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/JKB0KPk/pizza.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/VYYPZGk/salmon.jpg"))
        imageList.add(MediaItem("https://i.ibb.co/JvWpzYC/sunset.jpg"))

        adapter = MediaAdapter(this, imageList)
        gvMedia.adapter = adapter
    }

    fun login() {
        button!!.text = "LOGOUT"
        info!!.visibility = View.VISIBLE
        val pic: ImageView = findViewById(R.id.pic)
        Picasso.get().load(appPreferences?.getString(AppPreferences.PROFILE_PIC)).into(pic)
        val id: TextView = findViewById(R.id.id)
        id.text = appPreferences?.getString(AppPreferences.USER_ID)
        val name: TextView = findViewById(R.id.name)
        name.text = appPreferences?.getString(AppPreferences.USER_NAME)
    }

    fun logout() {
        button!!.text = "INSTAGRAM LOGIN"
        token = null
        info!!.visibility = View.GONE
        appPreferences?.clear()
    }

    override fun onTokenReceived(auth_token: String?) {
        if (auth_token == null) return
        appPreferences?.putString(AppPreferences.TOKEN, auth_token)
        token = auth_token
        getUserInfoByAccessToken(token!!)
    }

    fun onClick(view: View) {
        if (token != null) {
            logout()
        } else {
            authenticationDialog = AuthenticationDialog(this)
            authenticationDialog?.setCancelable(true)
            authenticationDialog?.show()
        }
    }

    private fun getUserInfoByAccessToken(token: String) {
        RequestInstagramAPI().execute()
    }

    inner class RequestInstagramAPI : AsyncTask<Void?, String?, String?>() {
        override fun doInBackground(vararg params: Void?): String? {
            /*val httpClient: HttpClient = DefaultHttpClient()
            val httpGet = HttpGet(getResources().getString(R.string.get_user_info_url).toString() + token)
            try {
                val response: HttpResponse = httpClient.execute(httpGet)
                val httpEntity: HttpEntity = response.getEntity()
                return EntityUtils.toString(httpEntity)
            } catch (e: IOException) {
                e.printStackTrace()
            }*/
            return null
        }

        override fun onPostExecute(response: String?) {
            super.onPostExecute(response)
            if (response != null) {
                try {
                    val jsonObject = JSONObject(response)
                    Log.e("response", jsonObject.toString())
                    val jsonData = jsonObject.getJSONObject("data")
                    if (jsonData.has("id")) {
                        //сохранение данных пользователя
                        appPreferences?.putString(AppPreferences.USER_ID, jsonData.getString("id"))
                        appPreferences?.putString(
                            AppPreferences.USER_NAME,
                            jsonData.getString("username")
                        )
                        appPreferences?.putString(
                            AppPreferences.PROFILE_PIC,
                            jsonData.getString("profile_picture")
                        )

                        //TODO: сохранить еще данные
                        login()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                val toast = Toast.makeText(getApplicationContext(), "Ошибка входа!", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    inner class MediaAdapter(context: Context, private var itemsList: ArrayList<MediaItem>) : BaseAdapter() {
        var context: Context? = context

        override fun getCount(): Int {
            return itemsList.size
        }

        override fun getItem(position: Int): Any {
            return itemsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val item = this.itemsList[position]

            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val mediaView = inflater.inflate(R.layout.media_item, null)

            mediaView.img.setImageURI(Uri.parse(item.imageUrl))

            return mediaView
        }
    }
}