package pers.bwx.sample.jluschoolgis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bwx on 2017/7/22.
 */

public class WalkFragmnet extends Fragment {

    private View walkView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        walkView  =inflater.inflate(R.layout.walk_fragmnet,container,false);

        return walkView;
    }
}
