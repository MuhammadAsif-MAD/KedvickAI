package com.kedvick.ai.ui.settings.inviteFriends.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.ui.settings.inviteFriends.model.ContactsModel
import com.kedvick.ai.R

class ContactsAdapter(
    private val context: Context,
    private var list: ArrayList<ContactsModel>,
    private val callBack: InviteFriendContactAdapterCallBacks
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_invite_contact_friends, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val singleContact = list[position]

        singleContact.let {
            if (it.name.isNotEmpty()) {
                holder.textFirstTwoLetters.text = if (it.name.length > 1) {
                    it.name.substring(0, 1)
                } else {
                    it.name
                }
            } else {
                holder.textFirstTwoLetters.text = if (it.phoneNumber.length > 1) {
                    it.phoneNumber.substring(0, 1)
                } else {
                    it.phoneNumber
                }
            }

            holder.tvName.text = it.name
            holder.tvPhoneNumber.text = it.phoneNumber

            holder.btnInvite.setOnClickListener {
                callBack.onInviteFriendContact(singleContact)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnInvite: RelativeLayout = itemView.findViewById(R.id.rlInvite)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        val textFirstTwoLetters: TextView = itemView.findViewById(R.id.tvFirstLetter)
    }

    interface InviteFriendContactAdapterCallBacks {
        fun onInviteFriendContact(contactsModel: ContactsModel)
    }

    fun filerList(modelList: ArrayList<ContactsModel>) {
        list = modelList
        notifyDataSetChanged()
    }
}
