package com.alibaba.weex.richtext.span;


import android.text.style.ClickableSpan;
import android.view.View;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.common.Constants.Event;
import com.taobao.weex.common.Constants.Name;
import com.taobao.weex.utils.WXDataStructureUtil;
import java.util.Map;

public class ItemClickSpan extends ClickableSpan {

  private final String mPseudoRef;
  private final String mInstanceId;
  private final String mComponentRef;

  public ItemClickSpan(String instanceId, String componentRef, String pseudoRef) {
    this.mPseudoRef = pseudoRef;
    this.mInstanceId = instanceId;
    this.mComponentRef = componentRef;
  }

  @Override
  public void onClick(View widget) {
    WXSDKInstance instance = WXSDKManager.getInstance().getSDKInstance(mInstanceId);
    if (instance != null && !instance.isDestroy()) {
      Map<String, Object> param = WXDataStructureUtil.newHashMapWithExpectedSize(1);
      param.put(Name.PSEUDO_REF, mPseudoRef);
      instance.fireEvent(mComponentRef, Event.ITEM_CLICK, param);
    }
  }
}
