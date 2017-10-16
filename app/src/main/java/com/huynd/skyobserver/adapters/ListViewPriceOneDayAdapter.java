package com.huynd.skyobserver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huynd.skyobserver.R;
import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.utils.RequestHelper;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static com.huynd.skyobserver.utils.Constants.CONVENIENCE_FEE_IN_K;

/**
 * Created by HuyND on 8/15/2017.
 */

public class ListViewPriceOneDayAdapter extends ArrayAdapter<PricePerDay> implements View.OnClickListener {
    private LayoutInflater mInflater;
    private boolean mShouldShowTotalPrice;

    public ListViewPriceOneDayAdapter(Context context) {
        super(context, R.layout.list_view_price_one_day_item);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgvAirline;
        View view = convertView;
        TextView tvDepart, tvArrive;
        Button btnSelect;

        if (view == null) {
            view = mInflater.inflate(R.layout.list_view_price_one_day_item, parent, false);
            imgvAirline = (ImageView) view.findViewById(R.id.image_view_airline);
            tvDepart = (TextView) view.findViewById(R.id.text_view_depart_time);
            tvArrive = (TextView) view.findViewById(R.id.text_arrive_time);
            btnSelect = (Button) view.findViewById(R.id.btn_select_price);
            ViewHolder viewHolder = new ViewHolder(imgvAirline, tvDepart, tvArrive, btnSelect);
            view.setTag(viewHolder);
        } else {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            imgvAirline = viewHolder.getImageViewAirline();
            tvDepart = viewHolder.getTextViewDepartTime();
            tvArrive = viewHolder.getTextViewArriveTime();
            btnSelect = viewHolder.getButtonSelectPrice();
        }

        PricePerDay item = getItem(position);

        btnSelect.setOnClickListener(null);
        if (item != null) {
            SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
            localDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));

            tvDepart.setText(localDateFormat.format(item.getDepartureTime()));
            tvArrive.setText(localDateFormat.format(item.getArrivalTime()));
            btnSelect.setOnClickListener(this);
            int price = mShouldShowTotalPrice ?
                    item.getPriceTotal() / 1000 + CONVENIENCE_FEE_IN_K : item.getPrice() / 1000;
            btnSelect.setText(String.valueOf(price));

            Glide.with(getContext())
                    .load(RequestHelper.airlinesIconUrlBuilder(item.getCarrier()))
                    .into(imgvAirline);
        } else {
            tvDepart.setText("");
            tvArrive.setText("");
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO
        // implement in another story
        // use BaseActivity.getCurrentFragment() to send message to PriceOneDay fragment
    }

    public void setShouldShowTotalPrice(boolean shouldShowTotalPrice) {
        mShouldShowTotalPrice = shouldShowTotalPrice;
    }

    public static class ViewHolder {
        private ImageView mImageViewAirline;
        private TextView mTextViewDepartTime;
        private TextView mTextViewArriveTime;
        private Button mButtonSelectPrice;

        public ViewHolder(ImageView imgvAirline, TextView tvDepart, TextView tvArrive, Button btnSelect) {
            mImageViewAirline = imgvAirline;
            mTextViewDepartTime = tvDepart;
            mTextViewArriveTime = tvArrive;
            mButtonSelectPrice = btnSelect;
        }

        public TextView getTextViewDepartTime() {
            return mTextViewDepartTime;
        }

        public TextView getTextViewArriveTime() {
            return mTextViewArriveTime;
        }

        public Button getButtonSelectPrice() {
            return mButtonSelectPrice;
        }

        public ImageView getImageViewAirline() {
            return mImageViewAirline;
        }
    }
}
