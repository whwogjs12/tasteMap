package com.example.tastemap;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapEventListener implements MapView.MapViewEventListener
{

    MapPOIItem marker;
    public MapEventListener()
    {
        marker = new MapPOIItem();
        marker.setItemName("저장할 위치");
        marker.setTag(0);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
    }

    public MapPOIItem getMarker()
    {
        return marker;
    }

    public void markerUpdate(MapPoint mapPoint)
    {
        marker.setMapPoint(mapPoint);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint)
    {
        mapView.removeAllPOIItems();
        marker.setMapPoint(mapPoint);
        if(marker!=null) mapView.addPOIItem(marker);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
