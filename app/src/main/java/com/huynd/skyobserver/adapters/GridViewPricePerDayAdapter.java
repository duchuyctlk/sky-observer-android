package com.huynd.skyobserver.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huynd.skyobserver.R;
import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.utils.RequestHelper;

import static com.huynd.skyobserver.utils.Constants.CONVENIENCE_FEE_IN_K;

/**
 * Created by HuyND on 8/12/2017.
 */

public class GridViewPricePerDayAdapter extends ArrayAdapter<PricePerDay> {
    private LayoutInflater mInflater;

    public GridViewPricePerDayAdapter(Context context) {
        super(context, R.layout.grid_view_price_per_day_item);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TextView textViewDay, textViewPrice;
        ImageView imgvAirline;

        if (view == null) {
            view = mInflater.inflate(R.layout.grid_view_price_per_day_item, parent, false);
            imgvAirline = (ImageView) view.findViewById(R.id.image_view_airline);
            textViewDay = (TextView) view.findViewById(R.id.text_view_day);
            textViewPrice = (TextView) view.findViewById(R.id.text_view_price);
            ViewHolder viewHolder = new ViewHolder(imgvAirline, textViewDay, textViewPrice);
            view.setTag(viewHolder);
        } else {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            imgvAirline = viewHolder.getImageViewAirline();
            textViewDay = viewHolder.getTextViewDay();
            textViewPrice = viewHolder.getTextViewPrice();
        }

        PricePerDay item = getItem(position);

        if (item != null) {
            textViewDay.setText(String.valueOf(item.getDay()));
            textViewPrice.setText(String.valueOf(item.getPriceTotal() / 1000 + CONVENIENCE_FEE_IN_K));

            String carrier = item.getCarrier();
            if (!TextUtils.isEmpty(carrier)) {
                Glide.with(getContext())
                        .load(RequestHelper.airlinesIconUrlBuilder(carrier))
                        .into(imgvAirline);
            } else {
                imgvAirline.setImageDrawable(null);
            }
        } else {
            textViewDay.setText("");
            textViewPrice.setText("");
        }
        return view;
    }

    public static class ViewHolder {
        private ImageView mImageViewAirline;
        private TextView mTextViewDay;
        private TextView mTextViewPrice;

        public ViewHolder(ImageView imgvAirline, TextView tvDay, TextView tvPrice) {
            mImageViewAirline = imgvAirline;
            mTextViewDay = tvDay;
            mTextViewPrice = tvPrice;
        }

        public TextView getTextViewDay() {
            return mTextViewDay;
        }

        public TextView getTextViewPrice() {
            return mTextViewPrice;
        }

        public ImageView getImageViewAirline() {
            return mImageViewAirline;
        }
    }
}
