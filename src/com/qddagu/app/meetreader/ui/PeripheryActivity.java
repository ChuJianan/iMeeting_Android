package com.qddagu.app.meetreader.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.qddagu.app.meetreader.AppContext;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.MyPoiOverlay;
/**
 * 周边搜索
 * 
 *
 */
public class PeripheryActivity extends BaseActivity {
	//UI相关
	Button mBtnReverseGeoCode = null;	// 将坐标反编码为地址
	Button mBtnGeoCode = null;	// 将地址编码为坐标
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	private ArrayAdapter<String> sugAdapter = null;
	private String add=null;
//	private int LatitudeE6,LongitudeE6;
	//地图相关
	MapView mMapView = null;	// 地图View
	private TextView dir,hotel,amusement,life,traffic;
	private 	Meeting meeting;
	//搜索相关
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用

	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_periphery);
		super.onCreate(savedInstanceState);
//		AppContext app = (AppContext)this.getApplication();
		dir=(TextView)findViewById(R.id.dir);
		hotel=(TextView)findViewById(R.id.hotel);
      amusement=(TextView)findViewById(R.id.amusement);
      life=(TextView)findViewById(R.id.life);
      traffic=(TextView)findViewById(R.id.traffic);
        meeting=appContext.Meeting();
        setTitle("会议周边");
        dir.setOnClickListener(this);
        hotel.setOnClickListener(this);
        amusement.setOnClickListener(this);
        life.setOnClickListener(this);
        traffic.setOnClickListener(this);
       
        maps();
	}
	private void maps(){
		 //地图初始化
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(16);
        mMapView.setBuiltInZoomControls(true);
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(16);
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.geocode(meeting.getPlace(),meeting.getPlace());
        mSearch.init(appContext.mBMapManager, new MKSearchListener() {
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            	if (error != 0) {
                    Toast.makeText(PeripheryActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                else {
//                    Toast.makeText(GeoCoderDemo.this,"", Toast.LENGTH_SHORT).show();
                }
            }
            
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("错误号：%d", error);
					Toast.makeText(PeripheryActivity.this, str, Toast.LENGTH_LONG).show();
					return;
				}
				//地图移动到该点
				mMapView.getController().animateTo(res.geoPt);	
				if (res.type == MKAddrInfo.MK_GEOCODE){
					//地理编码：通过地址检索坐标点
					String strInfo = String.format("纬度：%f 经度：%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
					
				}
				//生成ItemizedOverlay图层用来标注结果点
				ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mMapView);
				//生成Item
				OverlayItem item = new OverlayItem(res.geoPt, "", null);
				//得到需要标在地图上的资源
				Drawable marker = getResources().getDrawable(R.drawable.nav_turn_via_1);  
				//为maker定义位置和边界
				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
				//给item设置marker
				item.setMarker(marker);
				//在图层上添加item
				itemOverlay.addItem(item);
				
				//清除地图其他图层
				mMapView.getOverlays().clear();
				//添加一个标注ItemizedOverlay图层
				mMapView.getOverlays().add(itemOverlay);
				
				//执行刷新使生效
				mMapView.refresh();
//				LatitudeE6=res.geoPt.getLatitudeE6();
//				LongitudeE6=res.geoPt.getLongitudeE6();
				if(add!=null){
				mSearch.poiSearchNearBy(add, new GeoPoint((int) (res.geoPt.getLatitudeE6()), (int) (res.geoPt.getLongitudeE6())), 2000);
				}
			}
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(PeripheryActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                // 将地图移动到第一个POI中心点
                if (res.getCurrentNumPois() > 0) {
                    // 将poi结果显示到地图上
                    MyPoiOverlay poiOverlay = new MyPoiOverlay(PeripheryActivity.this, mMapView, mSearch);
                    poiOverlay.setData(res.getAllPoi());
                   
                    mMapView.getOverlays().add(poiOverlay);
                    mMapView.refresh();
                    //当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		 mMapController.animateTo(info.pt);
                    		break;
                    	}
                    }
                } else if (res.getCityListNum() > 0) {
                	//当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                    String strInfo = "在";
                    for (int i = 0; i < res.getCityListNum(); i++) {
                        strInfo += res.getCityListInfo(i).city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(PeripheryActivity.this, strInfo, Toast.LENGTH_LONG).show();
                }
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			}
			public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			}
			public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				if ( res == null || res.getAllSuggestions() == null){
            		return ;
            	}
            	sugAdapter.clear();
            	for ( MKSuggestionInfo info : res.getAllSuggestions()){
            		if ( info.key != null)
            		    sugAdapter.add(info.key);
            	}
            	sugAdapter.notifyDataSetChanged();
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub
				
			}

        });
	}
	@Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        mMapView.destroy();
        mSearch.destory();
        super.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    private void initMapView() {
        mMapView.setLongClickable(true);
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
    }
	  @Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dir:
//				mSearch = new MKSearch();
				add=dir.getText().toString();
				maps();
//			mSearch.geocode(meeting.getPlace(),meeting.getPlace());
				break;
			case R.id.hotel:
				add=hotel.getText().toString();
				mMapView.getOverlays().clear();
				maps();
//				mSearch = new MKSearch();
//			mSearch.geocode(meeting.getPlace(),meeting.getPlace());
				break;
			case R.id.amusement:
				add=amusement.getText().toString();
//				mSearch = new MKSearch();
				maps();
//				mSearch.geocode(meeting.getPlace(),meeting.getPlace());
//				mSearch.poiSearchNearBy(add, new GeoPoint((int) (LatitudeE6), (int) (LongitudeE6)), 2000);
				break;
			case R.id.life:
				add=life.getText().toString();
//				mSearch = new MKSearch();
//				mSearch.geocode(meeting.getPlace(),meeting.getPlace());
//				mSearch.poiSearchNearBy(add, new GeoPoint((int) (LatitudeE6), (int) (LongitudeE6)), 2000);
			maps();
				break;
			case R.id.traffic:
				add=traffic.getText().toString();
//				mSearch = new MKSearch();
//				mSearch.geocode(meeting.getPlace(),meeting.getPlace());
//				mSearch.poiSearchNearBy(add, new GeoPoint((int) (LatitudeE6), (int) (LongitudeE6)), 2000);
				maps();
				break;
			default:
				super.onClick(v);
				break;
			}
		}
}
