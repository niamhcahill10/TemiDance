/**
 * Displays songs in a RecyclerView and manages single selection.
 *
 * - Binds icon/genre and RadioButton state
 * - Uses payloads for selection-only updates (no flicker)
 * - Keeps UI in sync with `selectedSong`
 */

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

    // Hold id references for a given row to enable single call, make available to other class functions using inner
    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: RadioButton = view.findViewById(R.id.rb_song)
    }

    // Creates empty view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    // full bind (attaches data to view) on first run or page refresh
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList()) // calls partial bind with empty payload to render
    }

    /**
     * Syncs the RadioButton with the current model (selectedSong) and reattaches a fresh listener.
     * Detaches the listener before programmatic isChecked changes to avoid feedback loops.
     * Called on both full binds (no payload) and partial binds ("selection_changed").
     */
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

    /**
     * Binds (attaches data to the view) the song row.
     * - Full bind: when payloads are empty, sets text, icon, colors, and selection state.
     * - Partial bind: when payloads contain "selection_changed", only updates the RadioButton.
     * Using payloads avoids rebinding the entire row, which prevents flickering. Called via notifyItemChanged.
    */
    override fun onBindViewHolder(holder: SongViewHolder, position: Int, payloads: List<Any>) {
        val song = songs[position]

        // update bind selection only to prevent flickering
        if (payloads.contains("selection_changed")) {
            bindSelection(holder, song, position)
            return
        }

        // render all songs
        holder.radioButton.text = song.genre
        holder.radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
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
