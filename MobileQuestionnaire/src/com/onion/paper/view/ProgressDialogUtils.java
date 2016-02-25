package com.onion.paper.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.TextView;

import com.onion.paper.R;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
   自定义dialog
 * * * * * * * * * * * * * * * * * * * * * * *
 */
public class ProgressDialogUtils {
        private Activity ac;
        private Dialog dialog;
        private TextView tv;

        public ProgressDialogUtils(Activity activity) {
            ac = activity;
        	dialog = new Dialog(ac, R.style.progress_dialog);
        	dialog.setContentView(R.layout.dialog_progress);
        	dialog.setCancelable(true);
        	dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(false);
        
        
        }

        public boolean isShow() {
                if (dialog != null && ac != null && !ac.isFinishing()) {
                        return dialog.isShowing();
                } else {
                        return false;
                }

        }

        public void show() {
                try {
                        if (dialog != null && ac != null && !ac.isFinishing()) {
                                dialog.show();
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void show(final String text) {
                try {
                        if (dialog != null && ac != null && !ac.isFinishing()) {
                                ac.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                                tv.setText(text);
                                                dialog.show();
                                        }
                                });

                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void hide() {
                try {
                        if (dialog != null && ac != null && !ac.isFinishing()) {
                                dialog.dismiss();
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }

        }

        public void setBackfinishActivity(final Activity ac, boolean b) {

                if (!b) {
                        dialog.setOnCancelListener(null);
                } else if (ac != null) {
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                        ac.finish();
                                }
                        });
                }


        }
}
