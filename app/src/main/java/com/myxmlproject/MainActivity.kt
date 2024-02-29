package com.myxmlproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.myxmlproject.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

//431999b3961515974e78d91c9c7c86c7

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Surat")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchViewBar
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }

        })
    }

    private fun fetchWeatherData(cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityname, "431999b3961515974e78d91c9c7c86c7", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                   if (response.isSuccessful && responseBody != null){
                       val temperature = responseBody.main.temp.toString()
                       val humidity = responseBody.main.humidity
                       val wind = responseBody.wind.speed
                       val sunny = responseBody.sys.sunrise.toLong()
                       val sunset = responseBody.sys.sunset.toLong()
                       val sea = responseBody.main.pressure
                       val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                       val max = responseBody.main.temp_max
                       val min = responseBody.main.temp_min



      //               d("JYOTI",sunny.toString())

                       binding.temp.text="$temperature °C"
                       binding.weather!!.text = condition
                       binding.max!!.text="max :$max °C"
                       binding.min!!.text="min :$min °C"
                       binding.Humidity!!.text="$humidity %"
                       binding.windspeed!!.text= "$wind m/s"
                       binding.sunrise!!.text="${time(sunny)}"
                       binding.sunset!!.text="${time(sunset)}"
                       binding.sea!!.text="$sea hPa"
                       binding.Sunny!!.text="$condition"
                    //  binding.conditions.text=condition
                       binding.day!!.text=dayName(System.currentTimeMillis())
                       binding.date!!.text=date()
                       binding.cityName.text="$cityname"
                       //    Log.d("TAG", "onResponse: $temperature")

                       changeImagesAccordingToWeatherCondition(condition)
                   }
            }

            private fun changeImagesAccordingToWeatherCondition(condition: String) {
                when(condition){
                    "Clear Sky", "Sunny", "Clear", "HAZE"->{
                       binding.root.setBackgroundResource(R.drawable.todayyy_mg)
                        binding.lottieanimationView!!.setAnimation(R.raw.ani_sun_black)
                    }

                    "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                        binding.root.setBackgroundResource(R.drawable.night_img)
                        binding.lottieanimationView!!.setAnimation(R.raw.cloud_sun)
                    }
                    "Dusty", "Cloudy", "Rainy", "Fog", "Foggy", "Windy"->{
                        binding.root.setBackgroundResource(R.drawable.dusky_sky)
                        binding.lottieanimationView!!.setAnimation(R.raw.cloud_sun)
                    }
                    "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain","Rain" ->{
                        binding.root.setBackgroundResource(R.drawable.night_img)
                        binding.lottieanimationView!!.setAnimation(R.raw.rainny_ani)
                    }
                    "Light snow", "Moderate Snow", "Heavy Snow","snow", "Blizzard" ->{
                        binding.root.setBackgroundResource(R.drawable.night_rain)
                        binding.lottieanimationView!!.setAnimation(R.raw.skip_cloud_ani)
                    }
                        else ->{
                            binding.root.setBackgroundResource(R.drawable.fresh_day)
                            binding.lottieanimationView!!.setAnimation(R.raw.zoom_sun_ani)
                        }
                }
                binding.lottieanimationView!!.playAnimation()
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
            private fun date(): String {
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                return sdf.format((Date()))

            }
            private fun time(timestamp:Long): String {
                val sdf = SimpleDateFormat("HH:MM", Locale.getDefault())
                return sdf.format((Date(timestamp*1000)))

            }

        })
    }

        fun dayName(timestamp:Long):String{
            val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
            return sdf.format((Date()))

        }
}