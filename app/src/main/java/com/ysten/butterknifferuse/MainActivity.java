package com.ysten.butterknifferuse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ysten.butterknife_annotations.BindView;


/**
 * @author wangjitao
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv_name)
    TextView tv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tv.setText("绑定了啊");
    }


}
