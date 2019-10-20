package com.vedic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vedic.Activity.FormActivity;
import com.vedic.Models.User;
import com.vedic.R;
import com.vedic.Utils.CommonUtils;
import com.vedic.Utils.Constants;

import java.util.List;
import java.util.Locale;

public class FetchListAdapter extends RecyclerView.Adapter<FetchListAdapter.ViewHolder> {

    private static final String TAG = FetchListAdapter.class.getSimpleName();
    private Context mContext;
    private List<User> mList;

    public FetchListAdapter(Context context, List<User> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.adapter_fetch_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User item = mList.get(position);

        holder.name.setText(item.getName());
        holder.username.setText(item.getUsername());
        holder.email.setText(item.getEmail());
        holder.contact.setText(String.format(Locale.getDefault(),"%d",item.getContact()));

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(ResourcesCompat.getColor(mContext.getResources(),R.color.avatarTextColor,null))
                .useFont(ResourcesCompat.getFont(mContext,R.font.montserrat))
                .fontSize(CommonUtils.dpToPx(18,mContext)) //size in px
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(item.getName().substring(0,1), mContext.getResources().getColor(R.color.avatarBackgroundColor));

        Glide.with(mContext)
                .load(item.getImageUrl())
                .placeholder(drawable)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profilePic);

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, FormActivity.class);
                i.putExtra(Constants.USER_DATA,item);
                i.putExtra(Constants.CALL_FROM,Constants.VIEW_USER);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,email,username,contact;
        public ImageView profilePic;
        private ConstraintLayout listItem;
        //TODO Bind views
        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            username = itemView.findViewById(R.id.username);
            contact = itemView.findViewById(R.id.contact);
            profilePic = itemView.findViewById(R.id.profilePic);
            listItem = itemView.findViewById(R.id.listItem);
        }
    }
}