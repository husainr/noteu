package com.example.aliasgarbs.noteu

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket.view.*

class MainActivity : AppCompatActivity() {

    var listNotes=ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()!!.setLogo(R.mipmap.icon_note1);



        LoadQuery("%")
    }

    override  fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    fun LoadQuery(title:String){



        var dbManager=DbManager(this)
        val projections= arrayOf("ID","Title","Description")
        val selectionArgs= arrayOf(title)
        val cursor=dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        if(cursor.moveToFirst()){

            do{
                val ID=cursor.getInt(cursor.getColumnIndex("ID"))
                val Title=cursor.getString(cursor.getColumnIndex("Title"))
                val Description=cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID,Title,Description))

            }while (cursor.moveToNext())
        }

        var myNotesAdapter= MyNotesAdapter(this, listNotes)
        lvNotes.adapter=myNotesAdapter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        val sv= menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val sm= getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                LoadQuery("%"+ query +"%")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNote -> {
                    //go to add page
                    var intent= Intent(this,AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter:BaseAdapter{

        var listNotesAdapter=ArrayList<Note>()
        var context:Context?=null

        constructor(context:Context,listNotesAdapter:ArrayList<Note>):super(){
            this.listNotesAdapter=listNotesAdapter
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var myView=layoutInflater.inflate(R.layout.ticket,null)
            var myNote=listNotesAdapter[p0]
            myView.tvTitle.text=myNote.nodeName
            myView.tvDes.text=myNote.nodeDes
            myView.ivDelete.setOnClickListener( View.OnClickListener {
                var dbManager=DbManager(this.context!!)
                val selectionArgs= arrayOf(myNote.nodeID.toString())
                dbManager.Delete("ID=?",selectionArgs)
                LoadQuery("%")
            })

            myView.ivEdit.setOnClickListener( View.OnClickListener{

                GoToUpdate(myNote)

            })

            return myView
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdapter[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }


    }

    fun GoToUpdate(note:Note){
        var intent=  Intent(this,AddNotes::class.java)
        intent.putExtra("ID",note.nodeID)
        intent.putExtra("name",note.nodeName)
        intent.putExtra("des",note.nodeDes)
        startActivity(intent)
    }
}
