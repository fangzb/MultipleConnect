package com.elephant.multiple.presenter;

/**
 * 猎人端日历Presenter
 * @description 通过接口协议隔离Model和view的耦合.
 * @author Elephant
 * @date 2015年12月24日下午17:16:33
 */
public class MultipleConnectCalendarDatePresenter {

//    private HunterPriceDateApi mHunterPriceDateApi;
//    private CityHunterCalendarDateView mView;
//
//    public MultipleConnectCalendarDatePresenter(CityHunterCalendarDateView view) {
//        mHunterPriceDateApi = ApiService.createService(HunterPriceDateApi.class);
//        this.mView = view;
//    }
//
//    public void getPriceDates(long productId) {
//        mView.showLoading(true);
//        Call<NetCityHunterBase<NetCityHunterPriceDateList>> call = mHunterPriceDateApi.getPriceDates(productId);
//        call.enqueue(new Callback<NetCityHunterBase<NetCityHunterPriceDateList>>() {
//            @Override
//            public void onResponse(Response<NetCityHunterBase<NetCityHunterPriceDateList>> response, Retrofit retrofit) {
//                if(response != null && response.isSuccess() && response.body() != null) {
//                    if(response.body().status == 0) {
//                        mView.showLoading(false);
//                        mView.showDateList(response.body().data);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//            }
//        });
//    }
//
//    public void deletePriceLable(long productId, ArrayList<CalendarDates> datasList) {
//        mView.showLoading(true);
//        StringBuilder dateStrBuilder = new StringBuilder();
//        for (CalendarDates dates : datasList) {
//            List<String> deleteDates = Utility.getIntervalDate(dates.start, dates.end);
//            for (String dateStr : deleteDates) {
//                dateStrBuilder.append(dateStr).append(",");
//            }
//        }
//        if (dateStrBuilder.length() > 0) {
//            Call<NetCityHunterBase<NetPostReturn>> call = mHunterPriceDateApi.deleteDatesPriceLable(productId
//                    , dateStrBuilder.toString().substring(0, dateStrBuilder.length() - 1));
//            call.enqueue(new Callback<NetCityHunterBase<NetPostReturn>>() {
//                @Override
//                public void onResponse(Response<NetCityHunterBase<NetPostReturn>> response, Retrofit retrofit) {
//                    if (response != null && response.isSuccess() && response.body() != null) {
//                        if (response.body().status == 0) {
//                            if (response.body().data.status.equals(Constant.FIELDS_SUCCESS)) {
//                                mView.showLoading(false);
//                                mView.deleteSuccess();
//                            } else {
//                                mView.toastMsg("删除活动档期失败");
//                                mView.showLoading(false);
//                            }
//                        } else {
//                            mView.toastMsg(response.body().message);
//                            mView.showLoading(false);
//                        }
//                    } else {
//                        mView.toastNoNetWork();
//                        mView.showLoading(false);
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    mView.toastNoNetWork();
//                    mView.showLoading(false);
//                }
//            });
//        }
//    }
}
