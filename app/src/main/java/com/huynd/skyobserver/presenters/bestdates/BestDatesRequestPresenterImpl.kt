package com.huynd.skyobserver.presenters.bestdates

import com.huynd.skyobserver.models.bestdates.BestDatesModel
import com.huynd.skyobserver.views.bestdates.BestDatesRequestView

/**
 * Created by HuyND on 6/17/2019.
 */

class BestDatesRequestPresenterImpl(private val mView: BestDatesRequestView) :
        BestDatesRequestPresenter,
        BestDatesModel.EventListener {

    private val mModel = BestDatesModel()

    init {
        mModel.setEventListener(this)
    }

    override fun initSpinnersValues() {
        mView.updateAirports(mModel.getAirports())
        mView.updatePossibleTripLength()
    }

    override fun getPrices(srcPort: String, destPort: String) {
        mView.showLoadingDialog()
        mModel.getPrices(srcPort, destPort)
    }

    override fun onGetPricesResponse(result: Any) {
        mView.run {
            updateListView(result)
            dismissLoadingDialog()
        }
    }
}
