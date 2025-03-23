package com.example.soilclassifier

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class SoilInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil_info)

        val listView: ListView = findViewById(R.id.soilListView)

        // Get the predicted soil type from intent
        val predictedSoilType = intent.getStringExtra("PREDICTED_SOIL")
        val soilList = listOf(
            Soil("Yellow soil ", "Best for maize, pulses, and groundnut during monsoon.", "Organic compost, NPK fertilizers.", "Frequent watering needed."),
            Soil("Normal Soil ", "Suitable for wheat, rice, and vegetables year-round.", "Balanced NPK fertilizers.", "Moderate watering; avoid over-irrigation."),
            Soil("Cinder Soil ", "Ideal for root vegetables in spring & monsoon.", "High compost, potassium, phosphorus fertilizers.", "Frequent irrigation needed."),
            Soil("Clayey Soil", "Ideal for rice, wheat, and pulses in monsoon & winter.", "Compost, manure, gypsum.", "Needs careful irrigation to avoid waterlogging."),
            Soil("Peat Soil", "Ideal for vegetables (cabbage, carrots) & berries.", "Lime to reduce acidity, nitrogen fertilizers.", "Controlled watering required."),
            Soil("Loamy Soil", "Best for vegetables, grains, and fruits year-round.", "Organic manure, balanced NPK fertilizers.", "Irrigation based on crop needs."),
            Soil("Soil Insects Soil", "Usable after soil treatment.", "Organic pest-repellent compost (neem cake, biofertilizers).", "Avoid overwatering to reduce pests."),
            Soil("Sandy Loam", "Ideal for root crops like peanuts, carrots, and watermelon.", "Organic compost, potassium-rich fertilizers.", "Frequent but light irrigation."),
            Soil("Sandy Soil", "Good for drought-resistant crops like millets & coconut.", "Organic compost, nitrogen-rich fertilizers.", "Frequent but small irrigation."),
            Soil("Black Soil", "Best for cotton, wheat, soybeans, and sugarcane.", "Phosphorus & potassium fertilizers.", "Holds water well; moderate irrigation."),
            Soil("Laterite Soil", "Ideal for tea, coffee, cashew, and rubber.", "Lime treatment, organic manure.", "Retains moisture; irrigation depends on crops.")
        )
        val filteredSoilList = soilList.filter { it.name == predictedSoilType }

        val adapter = SoilAdapter(this, filteredSoilList)
        listView.adapter = adapter
    }
}
