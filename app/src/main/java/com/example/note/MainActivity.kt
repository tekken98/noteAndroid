package com.example.note

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class MainActivity : AppCompatActivity() {
    var mFileName:String? = null
    val mFilePath =Environment.getExternalStorageDirectory().toString() + "/dic/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btn = findViewById<Button>(R.id.btnLoad)
        btn.setOnClickListener(View.OnClickListener { v:View->
            loadFileList()
        })
        findViewById<Button>(R.id.btnSave).setOnClickListener(View.OnClickListener { v:View->
            saveFile()
        })
        findViewById<Button>(R.id.btnNew).setOnClickListener(View.OnClickListener { v:View->
            newFile()
        })
    }

    private fun newFile() {
        mFileName = null
        var txt = findViewById<EditText>(R.id.editText)
        txt.setText("")
        setTitle("no title")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var s = ""
        try {
            if (resultCode == Activity.RESULT_OK) {
                s = data!!.getStringExtra("name")
                loadFile(s)
            }
        }catch ( e:Exception){
            Log.d("Debug",e.toString())
        }
        //super.onActivityResult(requestCode, resultCode, data)
    }
    private fun loadFileList(){
        var note = noteSelect()
        var intent = Intent(this,note.javaClass)
        startActivityForResult(intent,1)
    }

    private fun saveFile() {
        var txt = findViewById<EditText>(R.id.editText)
        if (mFileName == null && mFileName != "")
            getFileName()
        else {
            try {
                val row = OutputStreamWriter(FileOutputStream(mFilePath + mFileName))
                row.write(txt.text.toString())
                row.flush()
                row.close()
            } catch (e: Exception) {
                Log.d("Debug", e.toString())
            }
        }
    }

    private fun loadFile(name:String){
       try{
           val isr = InputStreamReader(FileInputStream(mFilePath + name))
           val lines = isr.readLines()
           var s = ""
           for (a in lines)
               s += s + a + "\n"
           val edit = findViewById<EditText>(R.id.editText)
           edit.setText(s)
           mFileName = name
           this.setTitle(name)
       }catch(e:Exception){
           Log.d("Debug",e.toString())
       }
    }

    private fun getFileName() {

        val edit = EditText(this)
        edit.setText("")

        val builder = AlertDialog.Builder(this)
        .setTitle("Input a file name")
        .setView(edit)
        .setPositiveButton("ok",DialogInterface.OnClickListener{ inter,i ->
            mFileName = edit.text.toString()
            this.setTitle(mFileName)
            saveFile()
        })
        .create()
        .show()
    }
}
