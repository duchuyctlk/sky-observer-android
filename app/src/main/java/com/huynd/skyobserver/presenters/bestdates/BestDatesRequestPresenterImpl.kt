package com.huynd.skyobserver.presenters.bestdates

import com.huynd.skyobserver.models.bestdates.BestDatesModel
import com.huynd.skyobserver.views.bestdates.BestDatesRequestView

/**
 * Created by HuyND on 6/17/2019.
 */

class BestDatesRequestPresenterImpl(private val mView: BestDatesRequestView):
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
}
