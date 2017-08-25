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
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

class ImageViewLoader extends DrawableLoaderImp {

  private final ImageView view;
  private final WXImageStrategy strategy;

  ImageViewLoader(Context context, String url, ImageView view, WXImageQuality imageQuality,
      WXImageStrategy imageStrategy) {
    super(context, url);
    this.view = view;
    this.strategy = imageStrategy;
  }

  @Override
  protected boolean needLoadDrawable() {
    return view != null && view.getLayoutParams() != null;
  }

  @Override
  protected boolean recycleDrawable(String original) {
    boolean ret = false;
    if (TextUtils.isEmpty(original)) {
      view.setImageBitmap(null);
      ret = true;
    }
    return ret;
  }

  @Override
  protected boolean isImageSizeLegal() {
    return view.getLayoutParams().width > 0 && view.getLayoutParams().height > 0;
  }

  @Override
  protected void loadPlaceHolder() {
    if (!TextUtils.isEmpty(strategy.placeHolder)) {
      Picasso.Builder builder = new Picasso.Builder(WXEnvironment.getApplication());
      Picasso picasso = builder.build();
      picasso.load(Uri.parse(strategy.placeHolder)).into(view);
      view.setTag(strategy.placeHolder.hashCode(), picasso);
    }
  }

  @Override
  protected Transformation createTransform() {
    return new BlurTransformation(strategy.blurRadius);
  }

  @Override
  protected void loadDrawable(RequestCreator requestCreator, final String imageURL) {
    requestCreator.into(view, new Callback() {
      @Override
      public void onSuccess() {
        if (strategy.getImageListener() != null) {
          strategy.getImageListener().onImageFinish(imageURL, view, true, null);
        }
        if (!TextUtils.isEmpty(strategy.placeHolder)) {
          ((Picasso) view.getTag(strategy.placeHolder.hashCode())).cancelRequest(view);
        }
      }

      @Override
      public void onError() {
        if (strategy.getImageListener() != null) {
          strategy.getImageListener().onImageFinish(imageURL, view, false, null);
        }
      }
    });
  }
}
