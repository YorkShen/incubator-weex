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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

abstract class DrawableLoaderImp implements Runnable {

  protected final Context context;
  private final String url;

  DrawableLoaderImp(Context context, String url) {
    this.context = context;
    this.url = url;
  }

  @Override
  public void run() {
    if (needLoadDrawable()) {
      if (!recycleDrawable(url)) {
        if (isImageSizeLegal()) {
          loadPlaceHolder();
          String imageURL = rewriteURL(url);
          RequestCreator requestCreator =
              Picasso.with(context).load(imageURL).transform(createTransform());
          loadDrawable(requestCreator, imageURL);
        }
      }
    }
  }

  abstract protected boolean needLoadDrawable();

  abstract protected boolean recycleDrawable(String original);

  abstract protected boolean isImageSizeLegal();

  abstract protected void loadPlaceHolder();

  abstract protected Transformation createTransform();

  abstract protected void loadDrawable(RequestCreator requestCreator, String url);

  private String rewriteURL(String original){
    if (original.startsWith("//")) {
      original = "http:" + original;
    }
    return original;
  }
}
