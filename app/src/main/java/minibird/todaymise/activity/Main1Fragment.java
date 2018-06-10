package minibird.todaymise.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import minibird.todaymise.R;

public class Main1Fragment extends Fragment{

    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    private ConstraintLayout locationLo;
    private Button detailBtn;
    private Button shareBtn;
    private MainActivity mainActivity;
    private ViewPager vp;
    private TextView location, curTime, comment;
    private ImageView conditionImg;
    private ImageView pin;
    private String userLocation;
    private Intent intent;
    private int flag;

    //time
    private TimerTask minute;
    private Handler handler = new Handler();

    public static Main1Fragment newInstance(String loc){
        Main1Fragment frag = new Main1Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("location", loc);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocation = getArguments().getString("location");
        flag = getArguments().getInt("flag");

     }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final ConstraintLayout constraintLayout = (ConstraintLayout)inflater.inflate(R.layout.main1, container, false);

        // findView
        detailBtn = (Button)constraintLayout.findViewById(R.id.main_detail_btn);
        shareBtn = (Button)constraintLayout.findViewById(R.id.main_share_btn);
        mainActivity = (MainActivity)getActivity();
        location = (TextView)constraintLayout.findViewById(R.id.main_place_tv);
        curTime = (TextView)constraintLayout.findViewById(R.id.main_time_tv);
        conditionImg = (ImageView)constraintLayout.findViewById(R.id.item_condition_iv);
        comment = (TextView)constraintLayout.findViewById(R.id.main_comment_tv);
        locationLo = (ConstraintLayout)constraintLayout.findViewById(R.id.main_location_lo);
        vp = (ViewPager)mainActivity.findViewById(R.id.main_vp);
        pin = (ImageView)constraintLayout.findViewById(R.id.main_location_pin);

        curTime.setText(getTime());
        timeStart();
        if(userLocation != null)location.setText(userLocation);
        else{
            location.setText("알 수 없는 위치");
            Toast.makeText(getActivity(), "설정에서 GPS를 키거나\n직접 위치를 설정해보세요", Toast.LENGTH_SHORT).show();
        }

        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(1);
            }
        });

        locationLo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), minibird.todaymise.activity.SearchActivity.class);
                startActivity(intent);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareKakao();
            }
        });

        return constraintLayout;
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        return mFormat.format(mDate);
    }
//"http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg"
    private void shareKakao(){

        String templateId = "10108";
        Map<String, String> templateArgs = new HashMap<String, String>();
        templateArgs.put("template_arg1", "value1");

        KakaoLinkService.getInstance().sendCustom(getActivity(), templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다.
            }
        });
    }

    private void timeStart(){
        minute = new TimerTask() {
            @Override
            public void run() {
                Log.i("test", "타이머 시작");
                Update();
            }
        };
        Timer timer = new Timer();
        timer.schedule(minute, 0, 60000);
    }

    protected void Update(){
        Runnable updater = new Runnable(){
            public void run(){
                curTime.setText(getTime());
            }
        };
        handler.post(updater);
    }

}
