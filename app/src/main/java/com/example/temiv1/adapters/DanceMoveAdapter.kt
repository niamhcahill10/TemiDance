package com.example.temiv1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.temiv1.dance.data.DanceMove
import com.example.temiv1.R

class DanceMoveAdapter(private val moves: List<DanceMove>) :
    RecyclerView.Adapter<DanceMoveAdapter.MoveViewHolder>() {

    private val selectedMoves = mutableSetOf<DanceMove>()

    inner class MoveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cb_move)
    }

    // Creates empty view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dance_move, parent, false)
        return MoveViewHolder(view)
    }

    // Called to fill the view with items, called again for scrolling if new photo needs to appear in the view in place of a previous
    override fun onBindViewHolder(holder: MoveViewHolder, position: Int) {
        val move = moves[position]
        holder.checkBox.text = move.name
        holder.checkBox.isChecked = selectedMoves.contains(move)

        holder.checkBox.setOnCheckedChangeListener(null) // Avoid recycled listeners
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedMoves.add(move) else selectedMoves.remove(move)
        }

        val imageView: ImageView = holder.itemView.findViewById(R.id.iv_move_thumbnail)
        imageView.setImageResource(move.imageResId)
    }

    override fun getItemCount(): Int = moves.size

    fun getSelectedMoves(): List<DanceMove> = selectedMoves.toList()
}
