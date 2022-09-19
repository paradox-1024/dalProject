package com.app.test_nav.adapters
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.test_nav.R
import com.app.test_nav.models.PostModel;
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat

class RecyclerItemAdapter(private val posts : ArrayList<PostModel>) : RecyclerView.Adapter<RecyclerItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.activity_item_view, parent, false)

        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy") //"dd/MM/yyyy hh:mm:ss.SSS"
        holder.postTitle.text       = posts[position].postTitle
        holder.postDescription.text = posts[position].postDescription
        holder.postLocation.text    = posts[position].postLocation
        val url = URL(posts[position].imageUrl)
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        holder.postImageUrl.setImageBitmap(bmp)
        holder.postUserName.text    = posts[position].user?.userName

    }

    override fun getItemCount(): Int = posts.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val postTitle       : TextView  = itemView.findViewById(R.id.postTitleTextView)
        val postDescription : TextView  = itemView.findViewById(R.id.postDescriptionTextView)
        val postLocation    : TextView  = itemView.findViewById(R.id.postLocationTextView)
        val postImageUrl    : ImageView = itemView.findViewById(R.id.postImageUrl)
        val postUserName    : TextView = itemView.findViewById(R.id.postUserName)

    }

}