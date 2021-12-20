package com.baiganov.fintech.presentation.ui.people.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.model.UserFingerPrint
import com.baiganov.fintech.presentation.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.presentation.ui.chat.recyclerview.ItemClickListener
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class PersonAdapter(private val clickListener: ItemClickListener) :
    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    private val differ = AsyncListDiffer(this, PersonDiffUtil())

    var listOfUser: List<UserFingerPrint>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonViewHolder {
        return PersonViewHolder(
            clickListener,
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(listOfUser[position])
    }

    override fun getItemCount(): Int {
        return listOfUser.size
    }

    class PersonViewHolder(private val clickListener: ItemClickListener, itemView: View) :
        BaseViewHolder<UserFingerPrint>(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.user_name)
        private val tvEmail: TextView = itemView.findViewById(R.id.user_email)
        private val isOnline: View = itemView.findViewById(R.id.status_icon)
        private val ivAvatar: ShapeableImageView = itemView.findViewById(R.id.user_avatar)

        override fun bind(item: UserFingerPrint) {
            tvName.text = item.user.fullName
            tvEmail.text = item.user.email
            when (item.user.status) {
                STATUS_IDLE -> {
                    isOnline.setBackgroundResource(R.drawable.bg_idle_view)
                }
                STATUS_OFFLINE -> {
                    isOnline.setBackgroundResource(R.drawable.bg_offline_view)
                }
                STATUS_ONLINE -> {
                    isOnline.setBackgroundResource(R.drawable.bg_online_view)
                }
            }
            Glide.with(itemView.context).load(item.user.avatarUrl).into(ivAvatar)
        }
    }

    companion object {

        private const val STATUS_IDLE = "idle"
        private const val STATUS_OFFLINE = "offline"
        private const val STATUS_ONLINE = "online"
    }
}