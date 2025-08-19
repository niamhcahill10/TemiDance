/**
 * Displays dance moves in a RecyclerView and manages multi-selection.
 *
 * - Binds image/move and CheckBox state
 * - Keeps UI in sync with `selectedMoves`
 */

package com.example.temiv1.adapters

import android.util.TypedValue
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
        val image: ImageView = view.findViewById(R.id.iv_move_thumbnail)
    }

    // Inflates item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dance_move, parent, false)
        return MoveViewHolder(view)
    }

    // Binds the move name, image, and selection state
    override fun onBindViewHolder(holder: MoveViewHolder, position: Int) {
        val move = moves[position]

        holder.checkBox.apply {
            // Detach listener to avoid firing during re-binding / programmatic set e.g. after select all
            setOnCheckedChangeListener(null)

            text = move.name
            isChecked = selectedMoves.contains(move)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedMoves.add(move) else selectedMoves.remove(move)
            }
        }

        holder.image.setImageResource(move.imageResId)
    }

    override fun getItemCount(): Int = moves.size

    fun getSelectedMoves(): List<DanceMove> = selectedMoves.toList()

    fun selectAll() {
        selectedMoves.clear()
        selectedMoves.addAll(moves)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedMoves.clear()
        notifyDataSetChanged()
    }
}
