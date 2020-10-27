package com.app.graffiti.view_holder

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.dialog.PdfDialog
import com.app.graffiti.model.webresponse.MyTeamResponse
import kotlinx.android.synthetic.main.item_team.view.*
import android.view.WindowManager
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import com.app.graffiti.R


class MyTeamHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    var id:String?=null

    fun bindTeam(data: Any?) {
        if (data != null && data is MyTeamResponse) {
            itemView?.txt_team_member?.text = data.firstName+" "+data.lastName
            id=data.id
        }
        itemView.setOnClickListener {
//            PdfDialog(itemView.context).show()
            val dialog=PdfDialog(itemView.context,id)
            Log.d("IDDDDDDDDD",""+id)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_pdf)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(PdfDialog(itemView.context, id).getWindow().getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.show()
            dialog.getWindow().setAttributes(lp)
            dialog.setCanceledOnTouchOutside(true)
        }
    }
}
