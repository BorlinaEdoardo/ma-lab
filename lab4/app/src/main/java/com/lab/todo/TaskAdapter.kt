package com.lab.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskVH>() {

    var onItemClick: ((Task) -> Unit)? = null    // click lambda ~GK
    private val all = mutableListOf<Task>()
    private val visible = mutableListOf<Task>()

    fun submit(list: List<Task>) {
        all.clear()
        all.addAll(list)
        filter("")
    }

    fun filter(query: String) {
        val q = query.trim().lowercase()
        visible.clear()
        if (q.isEmpty()) {
            visible.addAll(all)
        } else {
            visible.addAll(all.filter { it.title.lowercase().contains(q) })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskVH(v)
    }

    override fun getItemCount(): Int = visible.size

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        holder.bind(visible[position])
    }

    inner class TaskVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView as CardView
        private val title: TextView = itemView.findViewById(R.id.taskTitleText)
        private val desc: TextView = itemView.findViewById(R.id.taskDescriptionText)
        private val image: ImageView = itemView.findViewById<ImageView>(R.id.taskImageThumb) //added image ~GK

        fun bind(task: Task) {
            title.text = task.title
            desc.text = task.description ?: ""
            card.setCardBackgroundColor(task.color)
            title.setTextColor(android.graphics.Color.WHITE)
            desc.setTextColor(android.graphics.Color.WHITE)

            //to handle image ~GK
            if (task.imageUri.isNullOrBlank()) {
                image.visibility = View.GONE
            } else {
                image.visibility = View.VISIBLE
                image.setImageURI(android.net.Uri.parse(task.imageUri))
            }
            itemView.setOnClickListener { this@TaskAdapter.onItemClick?.invoke(task) }
        }
    }
}
