package com.example.temiv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private val songs: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var selectedSong: Song? = null

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cb_song)
        val genreText: TextView = view.findViewById(R.id.genre)
//        val icon: ImageView = view.findViewById(R.id.iv_song)
    }

    // Creates empty view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    // Called to fill the view with items, called again for scrolling if new photo needs to appear in the view in place of a previous
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.checkBox.text = null
        holder.genreText.text = song.genre
        holder.checkBox.isChecked = (song == selectedSong)

        holder.checkBox.setOnCheckedChangeListener(null) // Avoid recycled listeners
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedSong = song
            }
        }
    }

    override fun getItemCount(): Int = songs.size

    fun getSelectedSong(): Song? = selectedSong
}
