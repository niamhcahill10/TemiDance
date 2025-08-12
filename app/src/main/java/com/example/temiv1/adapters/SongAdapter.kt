package com.example.temiv1.adapters

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.temiv1.R
import com.example.temiv1.dance.data.Song

class SongAdapter(private val songs: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var selectedSong: Song? = null

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: RadioButton = view.findViewById(R.id.rb_song)
    }

    // Creates empty view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    // required to compile bind view initially when no payloads are present
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    private fun bindSelection(holder: SongViewHolder, song: Song, position: Int) {
        holder.radioButton.setOnCheckedChangeListener(null)
        holder.radioButton.isChecked = (song == selectedSong)
        holder.radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && song != selectedSong) {
                val previousSelectedPosition = songs.indexOf(selectedSong)
                selectedSong = song
                notifyItemChanged(previousSelectedPosition, "selection_changed")
                notifyItemChanged(position, "selection_changed")
            }
        }
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int, payloads: List<Any>) {
        val song = songs[position]

        if (payloads.contains("selection_changed")) {
            bindSelection(holder, song, position)
            return
        }

        holder.radioButton.text = song.genre
        holder.radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        bindSelection(holder, song, position)

        val imageView: ImageView = holder.itemView.findViewById(R.id.iv_song)
        imageView.setImageResource(R.drawable.ic_music_note)

        val colors = listOf(
            Color.BLUE,
            Color.GREEN,
        )
        val color = colors[position % colors.size]
        imageView.setColorFilter(color)
    }

    override fun getItemCount(): Int = songs.size

    fun getSelectedSong(): Song? = selectedSong
}
