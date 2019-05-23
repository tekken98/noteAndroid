package com.example.note

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener

import kotlinx.android.synthetic.main.activity_note_select.*
import kotlinx.android.synthetic.main.content_note_select.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import com.example.note.R.id.noteListView as noteListView1

class noteSelect : AppCompatActivity() {
    var layoutList = android.R.layout.simple_list_item_1
    var item  =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_select)
        setSupportActionBar(toolbar)
        var noteList = findViewById<ListView>(R.id.noteListView)
        noteList.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            item = noteList.getItemAtPosition(position).toString()
            var intent = Intent()
            intent.putExtra("name",item)
            this.setResult(Activity.RESULT_OK,intent)
            this.finish()

        }
        var notes = getFiles()
        var adapter = ArrayAdapter<String>(this,layoutList,notes)
        noteList.adapter = adapter

    }
    fun getFiles(): List<String> {
        this.checkCallingOrSelfPermission("EXTERNAL_STORAGE_PERMISSION")
        var path = Environment.getExternalStorageDirectory().toString()
        path += "/dic"
        try{
            val file = File(path)
            file.mkdir()
            var files = file.listFiles()
            var s = mutableListOf<String>()
            for ( a in files){
                s.add(a.name )
            }
            return s
        }catch(e:Exception){

            Log.d("Debug",e.toString())
        }
        return listOf("***")
    }
}
