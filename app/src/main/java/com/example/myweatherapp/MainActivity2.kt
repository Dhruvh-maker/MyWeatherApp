package com.example.myweatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView  // Use the correct import!
import com.example.myweatherapp.databinding.ActivityMain2Binding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("Delhi")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchview  // Ensure your XML ID is correctly named
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun fetchWeatherData(cityname: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val response = apiService.getWeatherData(cityname, "1f10240b08ee4dbb466ce68a3135fb51", "metric")

        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful && response.body() != null) {
                    val temperature = response.body()?.main?.temp ?: "N/A"
                    val humidity = response.body()?.main?.humidity ?: "N/A"
                    val windSpeed = response.body()?.wind?.speed ?: "N/A"
                    val sunrise = response.body()?.main?.sunrise ?: "N/A"
                    val condition = response.body()?.weather?.firstOrNull()?.main ?: "Unknown"

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "$sunrise"
                    binding.cityname.text = cityname
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()

                    Log.d("WeatherApp", "Temperature: $temperature °C")
                } else {
                    Log.e("WeatherApp", "API Call Failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("WeatherApp", "Network Error: ${t.localizedMessage}")
            }
        })
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MM YYYY", Locale.getDefault())
        return sdf.format(Date())
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}

