/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.weex.commons.adapter;


import android.content.Context;
import android.widget.ImageView;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.DrawableStrategy;
import com.taobao.weex.adapter.IDrawableLoader;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

public class DrawableLoader implements IWXImgLoaderAdapter, IDrawableLoader {

  private Context mContext;

  public DrawableLoader(Context context) {
    this.mContext = context;
  }

  @Override
  public void setImage(String url, ImageView view, WXImageQuality quality,
      WXImageStrategy strategy) {
    post(new ImageViewLoader(mContext, url, view, quality, strategy));
  }

  @Override
  public void setDrawable(String url, DrawableTarget drawableTarget,
      DrawableStrategy drawableStrategy) {
    post(new DrawableTargetLoader(mContext, url, drawableTarget, drawableStrategy));
  }

  private void post(DrawableLoaderImp drawableLoaderImp) {
    WXSDKManager.getInstance().postOnUiThread(drawableLoaderImp, 0);
  }
}
