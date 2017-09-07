package czb.com.tinker.application;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by AnmiLin on 2017/9/7.
 */
public class BaseApplication extends TinkerApplication {
    private  static final String TAG=BaseApplication.class.getSimpleName();
    public  BaseApplication(){
        super(
                //tinkerFlags, which types is supported
                //dex only, library only, all support
                ShareConstants.TINKER_ENABLE_ALL,
                // This is passed as a string so the shell application does not
                // have a binary dependency on your ApplicationLifeCycle class.
                "czb.com.tinker.application.BaseApplicationLike");
    }

}