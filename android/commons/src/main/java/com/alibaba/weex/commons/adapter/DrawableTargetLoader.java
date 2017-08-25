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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.taobao.weex.adapter.DrawableStrategy;
import com.taobao.weex.adapter.IDrawableLoader.AnimatedTarget;
import com.taobao.weex.adapter.IDrawableLoader.DrawableTarget;
import com.taobao.weex.adapter.IDrawableLoader.StaticTarget;
import com.taobao.weex.common.WXImageStrategy;

class DrawableTargetLoader extends DrawableLoaderImp {

  private final DrawableTarget drawableTarget;
  private final DrawableStrategy drawableStrategy;

  DrawableTargetLoader(Context context, String url, DrawableTarget drawableTarget,
      DrawableStrategy drawableStrategy) {
    super(context, url);
    this.drawableTarget = drawableTarget;
    this.drawableStrategy = drawableStrategy;
  }

  @Override
  protected boolean needLoadDrawable() {
    return drawableTarget != null;
  }

  @Override
  protected boolean recycleDrawable(String original) {
    boolean ret = false;
    if (TextUtils.isEmpty(original)) {
      if (drawableTarget instanceof StaticTarget) {
        ((StaticTarget) drawableTarget).setDrawable(null, false);
        ret = true;
      } else if (drawableTarget instanceof AnimatedTarget) {
        ((AnimatedTarget) drawableTarget).setAnimatedDrawable(null);
        ret = true;
      }
    }
    return ret;
  }

  @Override
  protected boolean isImageSizeLegal() {
    return drawableStrategy.height > 0 && drawableStrategy.width > 0;
  }

  @Override
  protected void loadPlaceHolder() {
    WXImageStrategy strategy = drawableStrategy.imageStrategy;
    if (!TextUtils.isEmpty(strategy.placeHolder)) {
      Picasso.Builder builder = new Picasso.Builder(context);
      Picasso picasso = builder.build();
      picasso.load(Uri.parse(strategy.placeHolder)).into(new PlaceHolderDrawableTarget());
    }
  }

  @Override
  protected Transformation createTransform() {
    return new BlurTransformation(drawableStrategy.imageStrategy.blurRadius);
  }

  @Override
  protected void loadDrawable(RequestCreator requestCreator, String url) {
    Picasso.with(context).load(url).into(new PlaceHolderDrawableTarget());
  }

  /**
   * This is a hack for picasso, as Picasso hold weakReference to Target.
   * http://stackoverflow.com/questions/24180805/onbitmaploaded-of-target-object-not-called-on-first-load
   */
  private class PlaceHolderDrawableTarget extends Drawable implements Target {

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
      BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
      bitmapDrawable.setGravity(Gravity.FILL);
      if (drawableTarget instanceof StaticTarget) {
        ((StaticTarget) drawableTarget).setDrawable(bitmapDrawable, true);
      }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
      if (drawableTarget instanceof StaticTarget) {
        ((StaticTarget) drawableTarget).setDrawable(this, true);
      }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
      return PixelFormat.UNKNOWN;
    }
  }
}
