package com.onion.paper.activity;

import java.util.List;

import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.onion.paper.R;
import com.onion.paper.SuperConstants;
import com.onion.paper.activity.student.StudentActivity;
import com.onion.paper.model.web.User;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.tool.QuestionnaireActivityManager;
import com.onion.paper.view.CircleImage;
import com.onion.paper.view.ProgressDialogUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity implements OnClickListener, TextWatcher {

	private Button btn_login;
	private CircleImage ci_qq;
	
	
	private EditText et_name;
	private String mUserName;
	private EditText et_password;
	private String mPassword;
	private ProgressDialogUtils pDlgUtl;
	
	private TextView tv_register;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		if (mTencent == null) {
	        mTencent = Tencent.createInstance(SuperConstants.TENCENT_APP_ID, this);
	    }
		initComponent();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 显示加载中...
	 */
	private void showProgressDialog() {
		if (pDlgUtl == null) {
			pDlgUtl = new ProgressDialogUtils(LoginActivity.this);
		}
		if (!pDlgUtl.isShow()) {
			pDlgUtl.show();
		}
	}
	private void cancelProgress() {
		if (pDlgUtl != null && pDlgUtl.isShow()) {
			pDlgUtl.hide();
		}
	}
	
	/**************************************QQ开始*************************************/
	public static Tencent mTencent;
	private String openId;
	private UserInfo mInfo;
	private boolean isServerSideLogin = false;
	private void onClickLogin() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
            isServerSideLogin = false;
			Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		} else {
            if (isServerSideLogin) { 
            	// Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(this, "all", loginListener);
                isServerSideLogin = false;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
		}
	}
	IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
        	Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };
    private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
            if (null == response) {
                // 返回为空，登录失败
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                // 返回为空，登录失败
                return;
            }
			//登录成功
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {
			
		}

		@Override
		public void onError(UiError e) {
			Log.e("qq", e.errorDetail);
			cancelProgress();
		}

		@Override
		public void onCancel() {
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
            cancelProgress();
		}
	}
    
    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }
    
    private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {
				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					/*new Thread(){

						@Override
						public void run() {
							JSONObject json = (JSONObject)response;
							if(json.has("figureurl")){
								Bitmap bitmap = null;
								try {
									bitmap = TencentUtil.getbitmap(json.getString("figureurl_qq_2"));
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();*/
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);
		} else {
			
		}
	}
	
    Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				loginOrRegister(response);
			}else if(msg.what == 1){
				//Bitmap bitmap = (Bitmap)msg.obj;
				//mUserLogo.setImageBitmap(bitmap);=====================//头像
			}
		}
	};
	/************************************************QQ结束************************************************/
	/**
	 * 到数据库去查，如果没有就注册，有则登录
	 * @param response
	 */
	private void loginOrRegister(final JSONObject response) {
		BmobQuery<User> query = new BmobQuery<>();
		query.addWhereEqualTo("username", openId);
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				//查找错误
				mTencent.logout(LoginActivity.this);
				isServerSideLogin = false;
				Toast.makeText(LoginActivity.this, R.string.msg_update_fail, Toast.LENGTH_SHORT).show();
				cancelProgress();
			}

			@Override
			public void onSuccess(List<User> users) {
				// TODO Auto-generated method stub
				if (users != null && users.size() ==1) {
					// 已经存在该用户了
					BmobUser user = new BmobUser();
					user.setUsername(users.get(0).getUsername());
					user.setPassword(users.get(0).getThePassword());
					user.login(LoginActivity.this, new SaveListener() {
						
						@Override
						public void onSuccess() {
							// 登录成功
							startActivity(new Intent(LoginActivity.this, StudentActivity.class));
							finish();
							cancelProgress();
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// 登录失败
							cancelProgress();
							mTencent.logout(LoginActivity.this);
							Toast.makeText(LoginActivity.this, R.string.msg_login_fail, Toast.LENGTH_SHORT).show();
						}
					});
					
				} else {
					User user = new User();
					user.setUsername(openId);
					String password = String.valueOf(System.currentTimeMillis());
					user.setPassword(password);
					user.setThePassword(password);
					if (response.has("nickname")) {
						try {
							user.setNickName(response.getString("nickname"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (response.has("figureurl_qq_2")) {
						try {
							user.setImageUrl(response.getString("figureurl_qq_2"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (response.has("gender")) {
						try {
							user.setSex(response.getString("gender"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					String address = "";
					if (response.has("province")) {
						try {
							address += response.getString("province");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (response.has("city")) {
						try {
							address += response.getString("city");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					user.setLocation(address);
					user.setAndroidRelease(Build.VERSION.RELEASE);
					user.setPhoneName(Build.MODEL);
					user.setAge(0);
					user.setPhoneNumber(((TelephonyManager) LoginActivity.this.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number());
					user.signUp(LoginActivity.this, new SaveListener() {
						
						@Override
						public void onSuccess() {
							// 成功
							cancelProgress();
							startActivity(new Intent(LoginActivity.this, StudentActivity.class));
							finish();
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// 注册失败
							cancelProgress();
							mTencent.logout(LoginActivity.this);
							isServerSideLogin = false;
							Toast.makeText(LoginActivity.this, R.string.msg_update_fail, Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		showProgressDialog();
	    mTencent.onActivityResultData(requestCode, resultCode, data, loginListener);
	    super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		QuestionnaireActivityManager.getInstance().addActivity(this);
		// TODO Auto-generated method stub
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		
		ci_qq = (CircleImage) findViewById(R.id.ci_qq);
		ci_qq.setOnClickListener(this);
		
		et_name = (EditText) findViewById(R.id.et_phone);
		et_name.addTextChangedListener(this);
		et_password = (EditText) findViewById(R.id.et_password);
		et_password.addTextChangedListener(this);
		
		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_register.setOnClickListener(this);
		
		initPermissin();
	}
	
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,
            Manifest.permission.CAMERA,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_LOGS
    };
    /**
     * 初始化权限
     */
	private void initPermissin() {
		int permission = LoginActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
        	LoginActivity.this.requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.ci_qq:
			onClickLogin();
			break;
		
		case R.id.btn_login:
			if (HttpTool.isNetworkConnected(this)) {
				// 点击登录
				showProgressDialog();
				BmobUser user = new BmobUser();
				user.setPassword(mPassword);
				user.setUsername(mUserName);
				user.login(this, new SaveListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						cancelProgress();
						Toast.makeText(LoginActivity.this, R.string.msg_login_suc, Toast.LENGTH_SHORT).show();
						Intent loginIntent = new Intent(LoginActivity.this, StudentActivity.class);
						startActivity(loginIntent);
						finish();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						//Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
						BmobQuery<User> query = new BmobQuery<User>();
						query.addWhereEqualTo("mobilePhoneNumber", mUserName);
						
						query.findObjects(LoginActivity.this, new FindListener<User>() {

							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub
								cancelProgress();
								Toast.makeText(LoginActivity.this, R.string.msg_username_or_psw_err, Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(List<User> users) {
								// TODO Auto-generated method stub
								if (users != null && users.size() == 1) {
									BmobUser retryUser = new BmobUser();
									retryUser.setPassword(mPassword);
									retryUser.setUsername(users.get(0).getUsername());
									retryUser.login(LoginActivity.this, new SaveListener() {
										
										@Override
										public void onSuccess() {
											// TODO Auto-generated method stub
											//Toast.makeText(LoginActivity.this, "手机号登录成功", Toast.LENGTH_SHORT).show();
											cancelProgress();
											Intent loginIntent = new Intent(LoginActivity.this, StudentActivity.class);
											startActivity(loginIntent);
											finish();
										}
										
										@Override
										public void onFailure(int arg0, String arg1) {
											// TODO Auto-generated method stub
											cancelProgress();
											Toast.makeText(LoginActivity.this, R.string.msg_username_or_psw_err, Toast.LENGTH_SHORT).show();
										}
									});
								} else {
									cancelProgress();
									Toast.makeText(LoginActivity.this, R.string.msg_username_or_psw_err, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
			} else {
				Toast.makeText(LoginActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.tv_register:
			startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 0);
			break;

		default:
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		mUserName = et_name.getText().toString();
		mPassword = et_password.getText().toString();
		if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPassword)) {
			btn_login.setEnabled(true);
		} else {
			btn_login.setEnabled(false);
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}
