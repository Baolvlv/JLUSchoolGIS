package pers.bwx.sample.jluschoolgis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bwx on 2017/7/22.
 */

public class BusFragmnet extends Fragment {

    private View busView;

    //公交功能图片数组
    private int busImage[];
    //公交功能名数组
    private String busName[];
    //公交选择格网
    private GridView busGV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        busImage = new int[]{R.mipmap.ic_bus,R.mipmap.ic_subway};
        busName =new String[]{"查公交","地铁图"};
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        busView = inflater.inflate(R.layout.bus_fragment,container,false);

        busGV = (GridView) busView.findViewById(R.id.busGirdView);

        //地图类型图片文字list
        ArrayList<HashMap<String,Object>> lstImageName = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("busImage", busImage[i]);
            map.put("busName", busName[i]);
            lstImageName.add(map);
        }

        //地图类型选择adapter
        SimpleAdapter busAdapter = new SimpleAdapter(getContext(),lstImageName,
                R.layout.route_item,new String[]{"busImage","busName"},
                new int[]{R.id.itemIcon,R.id.itemText});
        //设置地图类型gridView的adapter
        busGV.setAdapter(busAdapter);
        return busView;
    }
}
