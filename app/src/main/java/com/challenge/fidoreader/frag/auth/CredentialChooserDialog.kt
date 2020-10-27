package com.challenge.fidoreader.frag.auth

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.challenge.fidoreader.R
import com.challenge.fidoreader.context.WebAuthnCredential
import kotlinx.android.synthetic.main.credential_chooser_dialog.*
import kotlinx.coroutines.*


private const val TAG = "CredentialChooserDialog"

@ExperimentalUnsignedTypes
class CredentialChooserDialog(
        val credentials: Array<WebAuthnCredential>,
        context: Context,
        val callback: (WebAuthnCredential?) -> Unit
) : Dialog(context), CoroutineScope {

    private val TIMEOUT_MS = 30_000L

    override val coroutineContext = Dispatchers.Main + SupervisorJob()

    private var chosenCredential: WebAuthnCredential? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.credential_chooser_dialog)
        credentialList.apply {
            val header = layoutInflater.inflate(R.layout.credential_list_header, this, false)
            addHeaderView(header)
            addFooterView(TextView(context).apply { height = header.height })
            adapter = credentialListAdapter
            setOnItemClickListener { _, _, position, _ ->
                val adapterPosition = position - headerViewsCount
                if (adapterPosition in 0 until adapter.count) {
                    if (chosenCredential == null)
                        chosenCredential = credentialListAdapter.getItem(adapterPosition)
                    dismiss()
                }
            }
        }
        setOnDismissListener { callback(chosenCredential) }
    }

    override fun onStart() {
        super.onStart()
        launch {
            delay(TIMEOUT_MS)
            this@CredentialChooserDialog.cancel()
        }
    }

    override fun onStop() {
        super.onStop()
        coroutineContext.cancelChildren()
    }

    private val credentialListAdapter = object : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val credentialView =
                    convertView ?: layoutInflater.inflate(R.layout.credential_view, parent, false)

            val titleView = credentialView.findViewById<TextView>(R.id.title)
            val summaryView = credentialView.findViewById<TextView>(R.id.summary)
            val credential = getItem(position)
            val credentialInfo = credential.getTwoLineInfo(position + 1)
            titleView.text = credentialInfo.first
            summaryView.text = credentialInfo.second
            summaryView.visibility = if (summaryView.text == null) View.GONE else View.VISIBLE
            return credentialView
        }

        override fun getItem(position: Int) = credentials[position]

        override fun getItemId(position: Int) = getItem(position).hashCode().toLong()

        override fun getCount() = credentials.size

    }

}