package com.yourcompany.yoursetting.receiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.yourcompany.yoursetting.Constants;
import com.yourcompany.yoursetting.TaskerPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import static com.yourcompany.yoursetting.receiver.MyIntentService.startActionFoo;
import static com.yourcompany.yoursetting.util.isServiceWork;

public class CallQpythonActivity extends Activity {
    private final String extPlgPlusName = "com.hipipal.qpyplus";
    private final int SCRIPT_EXEC_PY = 50001;
    private Intent fireIntentFromHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =getIntent();
        //getXxxExtra方法获取Intent传递过来的数据
        String filepath=intent.getStringExtra("filepath");
        fireIntentFromHost= intent.getParcelableExtra("fireIntentFromHost");
        Log.i(Constants.LOG_TAG,"收到参数"+filepath);
        QPyExec(filepath);

    }

    /*
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    */
    public static boolean checkAppInstalledByName(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);

            Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" found");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" not found");

            return false;
        }
    }

    public void QPyExec(String filepath) {

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
        //EditText codeTxt = (EditText)findViewById(R.id.edit_text);
        //String code = codeTxt.getText().toString();
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
        }
        Log.i(Constants.LOG_TAG, code);
        mBundle.putString("pycode", code);

        intent.putExtras(mBundle);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i(Constants.LOG_TAG,"启动Qpython Api");
        if (!isServiceWork(this,"com.yourcompany.yoursetting.receiver.MyIntentService")){
            startActionFoo(this, "111111", "2222222");}
        startActivityForResult(intent, SCRIPT_EXEC_PY);
        //startActivity(intent);
        //finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCRIPT_EXEC_PY) {
            if (data!=null) {
                Bundle bundle = data.getExtras();
                String flag = bundle.getString("flag"); // flag you set
                String param = bundle.getString("param"); // param you set
                String result = bundle.getString("result"); // Result your Pycode generate
                Bundle bundle1 = new Bundle();
                bundle1.putString("%errmsg","python error");
                Toast.makeText(this, "onQPyExec: return ("+result+")", Toast.LENGTH_SHORT).show();
                TaskerPlugin.Setting.signalFinish(this,fireIntentFromHost, TaskerPlugin.Setting.RESULT_CODE_FAILED, bundle1);
            } else {
                Toast.makeText(this, "onQPyExec: data is null", Toast.LENGTH_SHORT).show();

            }
            finish();
        }

    }

}
