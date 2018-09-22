package com.dolphkon.me.view;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import com.dolphkon.me.R;
import com.dolphkon.me.activity.FaceBackActivity;
import com.dolphkon.me.activity.LoginActivity;
import com.dolphkon.me.activity.SettingActivity;
import com.dolphkon.me.activity.base.BaseActivity;
import com.dolphkon.me.fragment.DoorSwitchFragment;
import com.dolphkon.me.fragment.GPSFragment;
import com.dolphkon.me.fragment.SensirionFragment;
import com.dolphkon.me.fragment.TemperatureFragment;
import com.dolphkon.me.fragment.VideoFragment;
import com.dolphkon.me.mqtt.MQTTService;
import com.dolphkon.me.utils.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends BaseActivity {
    private Toolbar mtoolbar;
    //tablayout
    private TabLayout mTabLayout;
    //    viewpager
    private ViewPager mViewPager;
    //title
    private List<String > mTitle;
    //fragment
    private List<Fragment>mFragment=new ArrayList<>();
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //      开启Mqtt的服务
        startService(new Intent(this, MQTTService.class));
        LogUtil.d("onCreate: "+Thread.currentThread());
        LogUtil.d("mqtt服务已启动！");
        initData();
//        初始化Voew
        initView();

        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
//        去掉阴影
        getSupportActionBar().setElevation(0);
        //        初始化数据
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        mtoolbar.setTitle("");
//        设置默认状态
        navigationView.setCheckedItem(R.id.nav_head);
//   Navigation的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_head:
                        mDrawerLayout.closeDrawers();
//                        finish();
                        break;
                    case R.id.nav_setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));

                        break;
                    case R.id.feedback:
//                        startActivity(new Intent(MainActivity.this, FaceBackActivity.class));
                        Toast.makeText(MainActivity.this,"主人暂时还没给我开通反馈服务，敬请期待...",Toast.LENGTH_LONG).show();
//finish();
                        break;
                    case R.id.logout:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();

                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint 激活导航栏
            tintManager.setNavigationBarTintEnabled(true);
            //设置系统栏设置颜色
            //tintManager.setTintColor(R.color.red);
            //给状态栏设置颜色
            tintManager.setStatusBarTintResource(R.color.white_15);
            //Apply the specified drawable or color resource to the system navigation bar.
            //给导航栏设置资源
            tintManager.setNavigationBarTintResource(R.color.white_15);
        }
    }
    private void initData() {
//        初始化 mTitle 集合并添加数据
        mTitle=new ArrayList<>();
        mTitle.add("温度");
        mTitle.add("温湿度");
        mTitle.add("门开关");
//        mTitle.add("GPS");
        mTitle.add("监控");
//        初始化 Fragment 并添加 fragment
        mFragment=new ArrayList<>();
        mFragment.add(new TemperatureFragment());
        mFragment.add(new SensirionFragment());
        mFragment.add(new DoorSwitchFragment());
//        mFragment.add(new GPSFragment());
        mFragment.add(new VideoFragment());
    }
    //初始化View
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        //预加载
        mViewPager.setOffscreenPageLimit(mFragment.size());
        //mViewPager滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                Log.i("TAG", "position:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //设置适配器
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的item
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            //返回item的个数
            @Override
            public int getCount() {
                return mFragment.size();
            }

            //设置标题
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        //绑定
        mTabLayout.setupWithViewPager(mViewPager);
    }
    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 1500) {
            Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}