/**
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
package com.taobao.weex.ui.flat.widget;


import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.view.View;
import com.taobao.weex.ui.view.border.BorderDrawable;

@RestrictTo(Scope.LIBRARY)
abstract class BaseWidget implements Widget {

  //TODO Reconsider the field parameter in this class and the operation during draw(); Make a CPU/Memory balance.
  //TODO use float to avoid 1px problem
  private BorderDrawable backgroundBorder;
  private int leftOffset, topOffset, rightOffset, bottomOffset;
  private Rect borderBox = new Rect();
  private Point offsetOfContainer = new Point();
  private View flatContainer;

  BaseWidget(@NonNull View flatContainer){
    //TODO maybe FlatUIImp is better.
    this.flatContainer = flatContainer;
  }

  @Override
  public void setLayout(int width, int height, int left, int right, int top, int bottom, Point offset) {
    this.offsetOfContainer = offset;
    borderBox.set(left, top, left + width, top + height);
    if (backgroundBorder != null) {
      setBackgroundAndBorder(backgroundBorder);
    }
    invalidate();
  }

  @Override
  public void setContentBox(int leftOffset, int topOffset, int rightOffset, int bottomOffset) {
    this.leftOffset = leftOffset;
    this.topOffset = topOffset;
    this.rightOffset = rightOffset;
    this.bottomOffset = bottomOffset;
    invalidate();
  }

  @Override
  public void setBackgroundAndBorder(@NonNull BorderDrawable backgroundBorder) {
    //TODO Change the code of BorderDrawable is more appropriate as it draws the borderLine from (0,0) not from getBounds
    //TODO If the above is finished, no more traslate in draw in needed, only clip is enough.
    this.backgroundBorder = backgroundBorder;
    Rect backgroundBox = new Rect(borderBox);
    backgroundBox.offset(-borderBox.left, -borderBox.top);
    backgroundBorder.setBounds(backgroundBox);
    setCallback(backgroundBorder);
    invalidate();
  }

  @Override
  public
  @NonNull
  Rect getBorderBox() {
    return borderBox;
  }

  @NonNull
  @Override
  public Point getLocInFlatContainer() {
    return offsetOfContainer;
  }

  @Nullable
  @Override
  public BorderDrawable getBackgroundAndBorder() {
    return backgroundBorder;
  }

  @Override
  public int getLeftOffset() {
    return leftOffset;
  }

  @Override
  public int getTopOffset() {
    return topOffset;
  }

  @Override
  public int getRightOffset() {
    return rightOffset;
  }

  @Override
  public int getBottomOffset() {
    return bottomOffset;
  }

  @Override
  public final void draw(@NonNull Canvas canvas) {
    canvas.save();
    canvas.clipRect(borderBox);
    canvas.translate(borderBox.left, borderBox.top);
    if (backgroundBorder != null) {
      backgroundBorder.draw(canvas);
    }
    canvas.clipRect(leftOffset, topOffset, borderBox.width()-rightOffset, borderBox.height() - bottomOffset);
    canvas.translate(leftOffset, topOffset);
    onDraw(canvas);
    canvas.restore();
  }

  @Override
  public void invalidate() {
    Rect dirtyRegion= new Rect(borderBox);
    dirtyRegion.offset(offsetOfContainer.x, offsetOfContainer.y);
    flatContainer.invalidate(dirtyRegion);
  }

  protected void setCallback(@NonNull Drawable drawable){
    if(flatContainer!=null){
      drawable.setCallback(flatContainer);
    }
  }
}

