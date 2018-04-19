
package com.uactiv.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.model.ChatDo;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.TimeUtils;
import com.uactiv.widgets.CustomTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class QBChatAdapter extends RecyclerView.Adapter<QBChatAdapter.ViewHolder> implements AppConstants.SharedConstants {

    private final ArrayList<ChatDo> chatMessages;
    private Activity context;



    public QBChatAdapter(Activity context, ArrayList<ChatDo> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    /*@Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatDo getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }*/



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatDo chatMessage = chatMessages.get(position);
        //boolean isOutgoing = chatMessage.isLeft();


        boolean isOutgoing = false;

        if(chatMessage.getIduser().equals(SharedPref.getInstance().getStringVlue(context,userId))){
            isOutgoing =true;
        }

        setAlignment(holder, isOutgoing);
        if(holder.txtMessage != null) {
            holder.txtMessage.setText(chatMessage.getMessage());
        }

        holder.tvusername.setText("" + chatMessage.getUsername());

        holder.tvTime.setText(timeFormat24(chatMessage.getCreated_on()));


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }



    public void add(ChatDo message) {
        chatMessages.add(message);
    }

    public void add(ArrayList<ChatDo> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        if (isOutgoing) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
            if (holder.txtMessage != null) {
                //holder.contentWithBG.setBackgroundResource(R.drawable.text_box);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);

            if (holder.txtMessage != null) {
                //holder.contentWithBG.setBackgroundResource(R.drawable.text_box);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    public static String timeFormat24(String mtime) {

        Date time_date = null;
        String newString = "";
        try {
            time_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mtime);
            //  newString = new SimpleDateFormat("HH:mm").format(time_date);
            newString = new SimpleDateFormat("hh:mm a").format(time_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("Time", newString);
        return newString;
    }

    private String getTimeText(ChatDo message) {
        return TimeUtils.millisToLongDHMS(getMillisec(message.getCreated_on()) * 1000);
    }

    private long getMillisec(String str_date){

        try{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(str_date);
            return  date.getTime();

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView txtMessage;
        public CustomTextView tvusername;

        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public TextView tvTime ;



        public ViewHolder(View v) {
            super(v);
            txtMessage = (CustomTextView) v.findViewById(R.id.tvmessage);
            tvusername = (CustomTextView) v.findViewById(R.id.tvusername);
            content = (LinearLayout) v.findViewById(R.id.content);
            contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
            txtInfo = (TextView) v.findViewById(R.id.txtInfo);
            tvTime = (TextView) v.findViewById(R.id.tvTime);

        }
    }
}

