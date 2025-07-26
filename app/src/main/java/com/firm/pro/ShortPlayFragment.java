package com.firm.pro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.firm.pro.utils.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

public class ShortPlayFragment extends Fragment {

    private static final String TAG = ShortPlayFragment.class.getSimpleName();

    private TextView textView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_short_play, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.btn_get_request);
        textView = view.findViewById(R.id.tv_result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequest();
            }
        });
    }

    private void getRequest() {
        String url="/api/eventHistory/get";
        Map<String,String> params = new HashMap<>();
        params.put("id","3854942653ba11eb90470c42a1415493");
        OkHttpUtil.getInstance().get(url,params, new OkHttpUtil.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                textView.setText(response);
//                System.out.println(response);
            }

            @Override
            public void onFailure(String errorMsg) {
                textView.setText(errorMsg);
//                System.out.println(errorMsg);
            }
        });

    }
}