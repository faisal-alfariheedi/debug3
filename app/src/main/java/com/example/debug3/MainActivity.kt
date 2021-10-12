package com.example.debug3

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val definitions = arrayListOf<ArrayList<String>>()

    private lateinit var rvMain: RecyclerView
    private lateinit var rvAdapter: RVAdapter
    private lateinit var etWord: EditText
    private lateinit var btSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ci()
        init()

        btSearch.setOnClickListener {
            requestAPI()
        }
    }
    fun init(){
        rvMain = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(definitions)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        etWord = findViewById(R.id.etWord)
        btSearch = findViewById(R.id.btSearch)
    }

    private fun requestAPI(){
        if(etWord.text.isNotEmpty()){
            CoroutineScope(IO).launch {
                val data = async{
                    getDefinition(etWord.text.toString())
                }.await()
                if(data.isNotEmpty()){
                    updateRV(data)
                }else{
                    withContext(Main){
                        Toast.makeText(this@MainActivity, "Unable to get data", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Please enter a word", Toast.LENGTH_LONG).show()
        }
    }

    private fun getDefinition(word: String): String{
        var response = ""
        try {
            response = URL("https://api.dictionaryapi.dev/api/v2/entries/en/$word").readText(Charsets.UTF_8)
        }catch (e: Exception){
            println("Error: $e")
            Toast.makeText(this, "Unable to get data", Toast.LENGTH_LONG).show()
        }
        return response
    }

    private suspend fun updateRV(result: String){
        withContext(Main){
            Log.d("MAIN", "DATA: $result")

            val jsonArray = JSONArray(result)
            val main = jsonArray[0]
            val word = JSONObject(main.toString()).getString("word")
            val inside = jsonArray.getJSONObject(0).getJSONArray("meanings")
                .getJSONObject(0).getJSONArray("definitions").getJSONObject(0)
            val definition = JSONObject(inside.toString()).getString("definition")
            Log.d("MAIN", "WORD: $word $definition")
            definitions.add(arrayListOf(word, definition))
            rvAdapter.notifyDataSetChanged()
            etWord.text.clear()
            etWord.clearFocus()
            rvMain.scrollToPosition(definitions.size - 1)
        }
    }
    private fun ci(){
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        if(!(activeNetwork?.isConnectedOrConnecting==true)){
            AlertDialog.Builder(this)
                .setTitle("Internet Connection Not Found")
                .setPositiveButton("RETRY"){_, _ -> ci()}
                .show()
        }
    }
}