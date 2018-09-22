package com.dolphkon.me.activity;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.dolphkon.me.R;
import com.dolphkon.me.utils.AppUtils;
import com.dolphkon.me.utils.Base64Utils;
import com.dolphkon.me.utils.Config;
import com.dolphkon.me.utils.RegisterAction;
import com.dolphkon.me.utils.SDCardUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import io.realm.Case;

/**
 * Created by langstone on 2018/7/31.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView img_upload;
    private EditText edt_register_account;
    private EditText edt_pwd;
    private Button btn_registered;
    private static final int CAMERA_REQUEST_CODE = 1;//拍照返回码
    private static final int GALLERY_REQUEST_CODE = 2;//相册返回码
    private static final int RESULT_OPEN_IMAGE = 1;
    private ProgressDialog pd;//进度条
    private String picturePath;//头像路径
    private String fileName;//头像名称
    private Bitmap bitmap;//存放裁剪后的头像
    private EditText redt_register_pwd;
    private String username;
    private String pwd;
    private String repwd;
    private Dialog progressDialog;
    private static final int DISMISS = 1000;//进度条消失
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DISMISS) {
                pd.dismiss();
                finish();
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wyt_register);
        initView();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
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
            tintManager.setNavigationBarTintResource(R.color.color_purple);
        }
    }
    private void initView() {
        edt_register_account = (EditText) findViewById(R.id.edt_register_account);
        edt_pwd = (EditText) findViewById(R.id.edt_register_pwd);
        btn_registered = (Button) findViewById(R.id.btn_registered);
        btn_registered.setOnClickListener(this);
        img_upload = (ImageView) findViewById(R.id.img_upload_img);
        img_upload.setOnClickListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null){
            return;
        }else {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            fileName = getBitmapName(picturePath);
            cursor.close();
            //裁剪为圆形头像
            if (SDCardUtils.isSDCardEnable()) {
                bitmap = AppUtils.toRoundBitmap(BitmapFactory.decodeFile(picturePath));
                img_upload.setImageBitmap(bitmap);//设置到图片
            } else {
                return;
            }
        }
    }

    //获取图片的名称
    public String getBitmapName(String picPath) {
        String bitmapName = "";
        String[] s = picPath.split("/");
        bitmapName = s[s.length - 1];
        return bitmapName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      switch (requestCode){
          case  1:
              if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                  //从图库选择照片
                  Intent ii = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  startActivityForResult(ii,RESULT_OPEN_IMAGE);
              }else {
                  Toast.makeText(RegisterActivity.this,"你拒绝了打开相册的相关权限，无法为您开启相机...",Toast.LENGTH_LONG).show();
              }
             break;
      }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_upload_img:
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(RegisterActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }else {
                    //从图库选择照片
                    Intent ii = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(ii,RESULT_OPEN_IMAGE);
                }
                break;
            case R.id.btn_registered:
                //点击的是注册按钮
                final String wyt_account = edt_register_account.getText().toString();//获取账号
                final String wyt_pwd = edt_pwd.getText().toString();//获取密码
                if (TextUtils.isEmpty(wyt_account)) {
                    Toast.makeText(RegisterActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(wyt_pwd)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (wyt_account.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "您输入的账号过长", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (wyt_pwd.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "您输入的密码过长", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd = ProgressDialog.show(this, "温馨提示", "正在注册...", false, true);
                if (null != bitmap) {
                    //截取图片后缀
                    String base64img = Base64Utils.bitmaptoString(bitmap);
                    //进行用户注册
                    new RegisterAction(Config.URL, Config.KEY_REGISTER, wyt_account, wyt_pwd, base64img, new RegisterAction.ISuccessCallback() {
                        @Override
                        public void onSuccess(String response, int id) {
                            pd.setMessage("注册成功");
                            handler.sendEmptyMessageDelayed(DISMISS, 1000);
                        }
                    }, new RegisterAction.IFailCallback() {
                        @Override
                        public void onFail(String failMsg) {
                            pd.setMessage("注册失败" + failMsg);
                            handler.sendEmptyMessageDelayed(DISMISS, 1000);
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
