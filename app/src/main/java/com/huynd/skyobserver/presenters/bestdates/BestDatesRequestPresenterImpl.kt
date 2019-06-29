package com.huynd.skyobserver.presenters.bestdates

import com.huynd.skyobserver.models.bestdates.BestDatesInfo
import com.huynd.skyobserver.models.bestdates.BestDatesModel
import com.huynd.skyobserver.models.cheapestflight.month.CheapestPricePerMonthResponse
import com.huynd.skyobserver.services.PricesAPI
import com.huynd.skyobserver.views.bestdates.BestDatesRequestView

/**
 * Created by HuyND on 6/17/2019.
 */

class BestDatesRequestPresenterImpl(private val mView: BestDatesRequestView, pricesAPI: PricesAPI) :
        BestDatesRequestPresenter,
        BestDatesModel.EventListener {

    private val mModel = BestDatesModel(pricesAPI)

    init {
        mModel.setEventListener(this)
    }

    override fun initSpinnersValues() {
        mView.updateAirports(mModel.getAirports())
        mView.updatePossibleTripLength()
    }

    override fun getPrices(srcPort: String, destPort: String, isReturnTrip: Boolean, tripLength: Int) {
        mView.showLoadingDialog()
        mModel.getPrices(srcPort, destPort, isReturnTrip, tripLength)
    }

    override fun onGetPricesResponse(result: List<BestDatesInfo>) {
        mView.run {
            updateListView(result)
            dismissLoadingDialog()
        }
    }

    override fun onGetPricesError(throwable: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
