package com.example.note

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    private data class header(var l:Int, var p:String){
        val sign = "VIMENCRY"
        var len = 0
        var pass=""
        init {
            len = p.length
            pass = p
        }
    }
    private var mHeader :header = header(0,"good")
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
                val row = OutputStreamWriter(FileOutputStream(mFilePath + mFileName + ".note"))
                var s= txt.text.toString()
                var fill = 32 - "VIMENCRY".length
                var charBuff = CharArray(fill,{'a'})
                var fillStr = ""
                for (a in charBuff )
                {
                   fillStr += a
                }
                if (mHeader.sign == "VIMENCRY")
                    row.write(mHeader.sign + fillStr + encryString(s))
                else
                    row.write(s)
                row.flush()
                row.close()
            } catch (e: Exception) {
                Log.d("Debug", e.toString())
            }
        }
    }
    private fun loadFile(name:String){
       try{
           var fis = FileInputStream(mFilePath + name)
           val isr = InputStreamReader(fis)
           var buff = CharArray(fis.available())
           isr.read(buff)
           val s = String(buff,32,buff.size - 32)
           var r = decryString(s)
           val edit = findViewById<EditText>(R.id.editText)
           edit.setText(r.toString())
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
    private fun encryString(s:String): String? {
        var buff = s.toByteArray(Charsets.UTF_8)
        var buffPass = mHeader.pass.toByteArray(Charsets.UTF_8)
        var i = 0
        var j = 0

        for (a in buff){
            buff[i] = (buff[i] + buffPass[j]).toByte()
            i++
            j++
            if (j >= buffPass.size)
                j = 0
        }
        return Base64.encodeToString(buff,Base64.DEFAULT)
    }
    private fun decryString(s:String): String {
        var buff = Base64.decode(s,Base64.DEFAULT)
        var buffPass = mHeader.pass.toByteArray(Charsets.UTF_8)
        var i = 0
        var j = 0

        for (a in buff){
            buff[i] = (buff[i] - buffPass[j] + 0x100 ).toByte()
            i++
            j++
            if (j >= buffPass.size)
                j = 0
        }
        return String(buff)
    }
}
