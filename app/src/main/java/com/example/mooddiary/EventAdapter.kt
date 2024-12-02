import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mooddiary.EmotionActivity
import com.example.mooddiary.R

class EventAdapter(private val events: MutableList<String>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTextView: TextView = itemView.findViewById(R.id.event_text)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button) // Кнопка удаления
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventTextView.text = event

        // Установка слушателя для кнопки удаления
        holder.deleteButton.setOnClickListener {
            events.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, events.size)
        }

        // Установка слушателя для перехода при нажатии на элемент списка
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EmotionActivity::class.java)
            intent.putExtra("event", event) // Передаем событие в следующую активность
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = events.size
}
