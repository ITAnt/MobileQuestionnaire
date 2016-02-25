package com.onion.paper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.onion.paper.R;
import com.onion.paper.activity.student.StudentActivity;
import com.onion.paper.model.web.User;
import com.onion.paper.tool.HttpTool;
import com.onion.paper.tool.QuestionnaireActivityManager;
import com.onion.paper.view.CustomedActionBar;
import com.onion.paper.view.CustomedActionBar.OnLeftIconClickListener;
import com.onion.paper.view.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;

public class RegisterActivity extends Activity implements OnClickListener, TextWatcher {

	private CustomedActionBar ab_register;
	
	private EditText et_phone;
	private EditText et_name;
	private EditText et_password;
	//private EditText et_chaptcha;
	
	private String phone;
	private String userName;
	private String password;
//	private String chaptcha;
	
	private Button btn_register;
	//private Button btn_get_chaptcha;
	
	private ProgressDialogUtils pDlgUtl;

	
	/*private boolean continueSend = true;
	private Handler mTimeHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int obj = (int) msg.obj;
			if (obj == 0) {
				btn_get_chaptcha.setText("获取验证码");
				btn_get_chaptcha.setEnabled(true);
			} else {
				btn_get_chaptcha.setText(obj + "S");
			}
			return true;
		}
	});*/
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		initComponent();
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		ab_register = (CustomedActionBar) findViewById(R.id.ab_register);
		ab_register.setOnLeftIconClickListener(new OnLeftIconClickListener() {
			@Override
			public void onLeftIconClick() {
				onBackPressed();
			}
		});
		
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_phone.addTextChangedListener(this);
		et_name = (EditText) findViewById(R.id.et_name);
		et_name.addTextChangedListener(this);
		et_password = (EditText) findViewById(R.id.et_password);
		et_password.addTextChangedListener(this);
//		et_chaptcha = (EditText) findViewById(R.id.et_chaptcha);
//		et_chaptcha.addTextChangedListener(this);
		
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);
//		btn_get_chaptcha = (Button) findViewById(R.id.btn_get_chaptcha);
//		btn_get_chaptcha.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btn_register:
			if (HttpTool.isNetworkConnected(this)) {
				if (pDlgUtl == null) {
					pDlgUtl = new ProgressDialogUtils(RegisterActivity.this);
					pDlgUtl.show();
				}
				
				User user = new User();
				user.setPassword(password);
				user.setUsername(userName);
				user.setMobilePhoneNumber(phone);
				user.setHasBeenFavorited(false);
				user.setAge(0);
				user.setSex("男");
				user.setPhoneName(Build.MODEL);
				user.setAndroidRelease(Build.VERSION.RELEASE);
				user.signUp(RegisterActivity.this, new SaveListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
						Toast.makeText(RegisterActivity.this, R.string.msg_register_suc, Toast.LENGTH_SHORT).show();
						User user = BmobUser.getCurrentUser(RegisterActivity.this, User.class);
						if (user != null) {
							QuestionnaireActivityManager.getInstance().clearActivities();
							startActivity(new Intent(RegisterActivity.this, StudentActivity.class));
							finish();
						} else {
							Toast.makeText(RegisterActivity.this, R.string.msg_login_first, Toast.LENGTH_SHORT).show();
							onBackPressed();
						}
					}
					
					@Override
					public void onFailure(int code, String exception) {
						// TODO Auto-generated method stub
						if (pDlgUtl != null) {
							pDlgUtl.hide();
						}
						String toast = getString(R.string.msg_login_fail);
						
						switch (code) {
						case 209:
							toast = getString(R.string.msg_phone_exists);
							break;
						case 202:
							toast = getString(R.string.msg_username_exists);
							break;

						default:
							toast = getString(R.string.msg_login_fail);
							break;
						} 
						Toast.makeText(RegisterActivity.this, toast, Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				Toast.makeText(RegisterActivity.this, R.string.msg_check_network, Toast.LENGTH_SHORT).show();
			}
		break;		
				
				// 校验验证码
				/*BmobSMS.verifySmsCode(this, phone, chaptcha, new VerifySMSCodeListener() {

				    @Override
				    public void done(BmobException ex) {
				        // TODO Auto-generated method stub
				        if(ex==null){//短信验证码已验证成功
				            //Log.i("smile", "验证通过");
				        	
				        }else{
				            //Log.i("smile", "验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
				        	if (pDlgUtl != null) {
								pDlgUtl.hide();
							}
				        	Toast.makeText(RegisterActivity.this, "手机验证失败", Toast.LENGTH_SHORT).show();
				        }
				    }
				});
			}*/ 
			
			
			
		/*case R.id.btn_get_chaptcha:
			if (!TextUtils.isEmpty(phone)) {
				continueSend = true;
				btn_get_chaptcha.setEnabled(false);
				// 发送验证码
				BmobSMS.requestSMSCode(this, phone, "超级问卷短信模板", new RequestSMSCodeListener() {

					@Override
					public void done(Integer code, BmobException exception) {
						// TODO Auto-generated method stub
						if(exception == null){
				        	//验证码发送成功
				        	//用于查询本次短信发送详情
				            //Log.i("smile", "短信id："+smsId);
							Toast.makeText(RegisterActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									for (int i = 60; i > -1; i--) {
										if (!continueSend) {
											return;
										}
										
										Message message = Message.obtain();
										message.obj = i;
										mTimeHandler.sendMessage(message);
										try {
											Thread.sleep(1000);
										} catch (Exception e) {
											// TODO: handle exception
											message.obj = 1;
											mTimeHandler.sendMessage(message);
										}
										
									}
								}
							}).start();
				        } else {
							// 发送验证码失败
				        	continueSend = false;
				        	btn_get_chaptcha.setText("获取验证码");
				        	btn_get_chaptcha.setEnabled(true);
				        	if (code == 301) {
				        		Toast.makeText(RegisterActivity.this, "手机格式不正确", Toast.LENGTH_SHORT).show();
				        	}
						}
					}
				});
			}
			
			break;*/

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
		phone = et_phone.getText().toString();
		userName = et_name.getText().toString();
		password = et_password.getText().toString();
//		chaptcha = et_chaptcha.getText().toString();
		
		boolean canRegister = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password);
		btn_register.setEnabled(canRegister);
	}
}
