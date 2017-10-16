package com.huynd.skyobserver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.huynd.skyobserver.R;
import com.huynd.skyobserver.SkyObserverApp;
import com.huynd.skyobserver.adapters.ListViewPriceOneDayAdapter;
import com.huynd.skyobserver.databinding.FragmentPriceOneDayBinding;
import com.huynd.skyobserver.models.PricePerDay;
import com.huynd.skyobserver.presenters.PriceOneDayPresenter;
import com.huynd.skyobserver.presenters.PriceOneDayPresenterImpl;
import com.huynd.skyobserver.services.PricesAPI;
import com.huynd.skyobserver.utils.PriceComparator;
import com.huynd.skyobserver.views.PriceOneDayView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by HuyND on 8/14/2017.
 */

public class PriceOneDayFragment extends BaseFragment implements
        PriceOneDayView,
        CompoundButton.OnCheckedChangeListener {

    @Inject
    PricesAPI mPricesAPI;

    public static final String TAG = PriceOneDayFragment.class.getSimpleName();

    FragmentPriceOneDayBinding mBinding;

    ListViewPriceOneDayAdapter mListViewOutboundAdapter;
    ListViewPriceOneDayAdapter mListViewInboundAdapter;

    private PriceOneDayPresenter mPresenter;

    public static Fragment newInstance() {
        return new PriceOneDayFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inject
        SkyObserverApp app = (SkyObserverApp) getActivity().getApplication();
        app.getSkyObserverComponent().inject(this);

        // get data from intent
        Bundle args = getArguments();
        int yearOutbound = args.getInt("yearOutbound");
        int monthOutbound = args.getInt("monthOutbound");
        int dayOutbound = args.getInt("dayOutbound");
        String srcPort = args.getString("srcPort");
        String dstPort = args.getString("dstPort");
        boolean returnTrip = args.getBoolean("returnTrip");
        int yearInbound = 0, monthInbound = 0, dayInbound = 0;
        if (returnTrip) {
            yearInbound = args.getInt("yearInbound");
            monthInbound = args.getInt("monthInbound");
            dayInbound = args.getInt("dayInbound");
        }

        // initialize UI widgets
        mBinding = FragmentPriceOneDayBinding.inflate(inflater, container, false);
        mBinding.txtRoutineOutbound.setText(srcPort + " - " + dstPort);
        mBinding.txtFlightDateOutbound.setText(dayOutbound + "/" + monthOutbound + "/" + yearOutbound);
        mListViewOutboundAdapter = new ListViewPriceOneDayAdapter(this.getContext());
        mListViewOutboundAdapter.setShouldShowTotalPrice(mBinding.chkShowTotalPriceOutbound.isChecked());
        mBinding.lstPricesOutbound.setAdapter(mListViewOutboundAdapter);
        if (returnTrip) {
            mBinding.layoutInbound.setVisibility(View.VISIBLE);
            mBinding.txtRoutineInbound.setText(dstPort + " - " + srcPort);
            mBinding.txtFlightDateInbound.setText(dayInbound + "/" + monthInbound + "/" + yearInbound);
            mListViewInboundAdapter = new ListViewPriceOneDayAdapter(this.getContext());
            mListViewInboundAdapter.setShouldShowTotalPrice(mBinding.chkShowTotalPriceInbound.isChecked());
            mBinding.lstPricesInbound.setAdapter(mListViewInboundAdapter);
            mBinding.chkShowTotalPriceInbound.setOnCheckedChangeListener(this);
        } else {
            mBinding.layoutInbound.setVisibility(View.GONE);
        }
        mBinding.chkShowTotalPriceOutbound.setOnCheckedChangeListener(this);

        // initialize MPV pattern
        mPresenter = new PriceOneDayPresenterImpl(this, mPricesAPI);

        if (!TextUtils.isEmpty(srcPort) && !TextUtils.isEmpty(dstPort)) {
            mPresenter.getPrices(yearOutbound, monthOutbound, dayOutbound, srcPort, dstPort, true);

            if (returnTrip) {
                mPresenter.getPrices(yearInbound, monthInbound, dayInbound, dstPort, srcPort, false);
            }
        }

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_price_one_day, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sorting_order_total_price_lowest:
                mPresenter.setSortOrder(PriceComparator.SortOrder.TOTAL_PRICE_LOWEST);
                break;
            case R.id.sorting_order_total_price_highest:
                mPresenter.setSortOrder(PriceComparator.SortOrder.TOTAL_PRICE_HIGHEST);
                break;
            case R.id.sorting_order_depart_earliest:
                mPresenter.setSortOrder(PriceComparator.SortOrder.DEPART_EARLIEST);
                break;
            case R.id.sorting_order_depart_latest:
                mPresenter.setSortOrder(PriceComparator.SortOrder.DEPART_LATEST);
                break;
            case R.id.sorting_order_airlines:
                mPresenter.setSortOrder(PriceComparator.SortOrder.AIRLINES);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void updateListViewOutboundPrices(List<PricePerDay> prices) {
        mListViewOutboundAdapter.clear();
        mListViewOutboundAdapter.addAll(prices);
        mListViewOutboundAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateListViewInboundPrices(List<PricePerDay> prices) {
        mListViewInboundAdapter.clear();
        mListViewInboundAdapter.addAll(prices);
        mListViewInboundAdapter.notifyDataSetChanged();
    }

    @Override
    public void showInvalidDateDialog() {
        showFailedDialog(getString(R.string.invalid_date_message));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chk_show_total_price_outbound:
                mListViewOutboundAdapter.setShouldShowTotalPrice(mBinding.chkShowTotalPriceOutbound.isChecked());
                mListViewOutboundAdapter.notifyDataSetChanged();
                break;
            case R.id.chk_show_total_price_inbound:
                mListViewInboundAdapter.setShouldShowTotalPrice(mBinding.chkShowTotalPriceInbound.isChecked());
                mListViewInboundAdapter.notifyDataSetChanged();
                break;
        }
    }
}
