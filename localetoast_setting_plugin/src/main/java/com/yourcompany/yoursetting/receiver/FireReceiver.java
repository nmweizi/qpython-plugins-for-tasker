/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.yourcompany.yoursetting.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Locale;
import com.yourcompany.yoursetting.Constants;
import com.yourcompany.yoursetting.TaskerPlugin;
import com.yourcompany.yoursetting.bundle.BundleScrubber;
import com.yourcompany.yoursetting.bundle.PluginBundleManager;
import com.yourcompany.yoursetting.ui.EditActivity;

/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class FireReceiver extends BroadcastReceiver
{

    /**
     * @param context {@inheritDoc}.
     * @param intent the incoming {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING} Intent. This
     *            should contain the {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was saved by
     *            {@link EditActivity} and later broadcast by Locale.
     */
    private final int SCRIPT_EXEC_PY = 50001;
    private final String extPlgPlusName = "com.hipipal.qpyplus";

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
            if (Constants.IS_LOGGABLE)
            {
                Log.e(Constants.LOG_TAG,
                      String.format(Locale.US, "Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
            }
            return;
        }
        Log.i(Constants.LOG_TAG, "收到" + "tasker消息");
        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        if (PluginBundleManager.isBundleValid(bundle))
        {
            final String filepath = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE);
            //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            if ( isOrderedBroadcast() )
                setResultCode( TaskerPlugin.Setting.RESULT_CODE_PENDING );
                /*
                import android
                droid = android.Android()

                task_name='Test Task'
                par1='test variable 1'
                par2='test variable 2'
                namelist=['%par1','%par2']
                valuelist=[par1,par2]

                extras = {'task_name': task_name}
                extras['varNames']=namelist
                extras['varValues']=valuelist
                "EXTRA_PLUGIN_COMPLETION_INTENT"
                taskIntent = droid.makeIntent('net.dinglisch.android.tasker.ACTION_TASK', None, None, extras).result
                droid.sendBroadcastIntent(taskIntent)
                TaskerPlugin.Setting.signalFinish( context, fireIntentFromHost, TaskerPlugin.Setting.RESULT_CODE_OK, varsBundle );

                import sys
                import android

                droid = android.Android()

                try:
                  email_name = droid.getIntent().result[u'extras'][u'%EMAIL_NAME']
                except:
                  email_name = ''

                try:
                  email_user = droid.getIntent().result[u'extras'][u'%EMAIL_USER']
                except:
                  droid.makeToast('EMAIL_USER missing')
                  sys.exit(1)

                try:
                  email_pswd = droid.getIntent().result[u'extras'][u'%EMAIL_PSWD']
                except:
                  droid.makeToast('EMAIL_PSWD missing')
                  sys.exit(1)

                try:
                  mailto = droid.getIntent().result[u'extras'][u'%EMAIL_TO']
                except:
                  droid.makeToast('EMAIL_TO missing')
                  sys.exit(1)

                try:
                  subject = droid.getIntent().result[u'extras'][u'%EMAIL_SUBJECT']
                except:
                  subject = ''

                try:
                  body = droid.getIntent().result[u'extras'][u'%EMAIL_BODY']
                except:
                  body = ''

                try:
                  attachments = droid.getIntent().result[u'extras'][u'%EMAIL_ATTACH']
                  attachments = attachments.split(',')
                except:
                  attachments = ''

                # Send email
                if (sendemail(email_name, email_user, email_pswd, mailto, subject, body, attachments)):
                  sys.exit(0)
                else:
                  # Exit with error if email is not sent successfully
                  droid.makeToast('email failed')

                 */
            QPyExec(context,filepath,intent);
        }
    }

    public static boolean checkAppInstalledByName(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);

            Log.d("QPYMAIN",  "checkAppInstalledByName:" + packageName + " found");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" not found");

            return false;
        }
    }

    public void QPyExec(Context context,String filepath,Intent fireIntentFromHost) {
        Log.i(Constants.LOG_TAG,filepath);
        if (checkAppInstalledByName(context.getApplicationContext(), extPlgPlusName)) {
            //---------------------------------------------------------------------
            /*
            Intent intent = new Intent();
            intent.setClassName(extPlgPlusName, extPlgPlusName+".MPyApi");
            intent.setAction(extPlgPlusName + ".action.MPyApi");

            Bundle mBundle = new Bundle();
            mBundle.putString("app", "myappid");
            mBundle.putString("act", "onPyApi");
            mBundle.putString("flag", "onQPyExec");            // any String flag you may use in your context
            mBundle.putString("param", "");          // param String param you may use in your context
	        /*
	         * The String Python code, you can put your py file in res or raw or intenet, so that you can get it the same way, which can make it scalable
	         */
            /*
            String code = "#qpy:qpyapp\nimport os,sys\nprint 'hello'\nsys.exit()";
            try {
                File urlFile = new File(new URI(filepath));
                Log.i("plugins", urlFile.getAbsolutePath());
                InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String str = "";
                String Line = null ;
                while ((Line = br.readLine()) != null) {
                    str = str+"\n"+Line;
                }
                code = str;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context,"read script file failed", Toast.LENGTH_SHORT).show();
            }
            mBundle.putString("pycode", code);
            intent.putExtras(mBundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            */
            //------------------------------------------------------------------
            Intent intent = new Intent(context,CallQpythonActivity.class);
            intent.putExtra("filepath", filepath);
            //Bundle bundle = new Bundle();
            intent.putExtra("fireIntentFromHost",fireIntentFromHost);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i(Constants.LOG_TAG, "启动无界面activity");
            context.startActivity(intent);
            //-------------------------------------------------------------------
            /*
            Log.i(Constants.LOG_TAG,"start qpython activity");
            Log.i(Constants.LOG_TAG,"start service");
            context.startActivity(intent);
            */
        } else {
            Toast.makeText(context.getApplicationContext(), "Please install QPython first", Toast.LENGTH_LONG).show();

            try {
                Uri uLink = Uri.parse("market://details?id=com.hipipal.qpyplus");
                Intent intent = new Intent( Intent.ACTION_VIEW, uLink);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Uri uLink = Uri.parse("http://qpython.com");
                Intent intent = new Intent( Intent.ACTION_VIEW, uLink);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        }
    }

    protected  boolean isAsync() {
        return true;
    }
}