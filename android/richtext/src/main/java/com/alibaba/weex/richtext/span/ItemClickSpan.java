package com.alibaba.weex.richtext.span;


import android.text.style.ClickableSpan;
import android.view.View;
import com.alibaba.weex.richtext.node.RichTextNode;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
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
      param.put(RichTextNode.PSEUDO_REF, mPseudoRef);
      instance.fireEvent(mComponentRef, RichTextNode.ITEM_CLICK, param);
    }
  }
}
