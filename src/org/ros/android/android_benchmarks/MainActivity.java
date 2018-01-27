/*
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_benchmarks;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.ros.address.InetAddressFactory;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import org.ros.rosjava_benchmarks.MessagesBenchmark;
import org.ros.android.view.RosImageView;

import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;


/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity {

  private RosTextView<std_msgs.String> rosTextView;

  private EditText editText;
  private TextView imagetopictext;
  private String imagetopicname = "/image_raw/compressed";
  private RosImageView<sensor_msgs.CompressedImage> image;


  private controllerclass controller;
  private double x,y,rx,ry,rt,lt,command_ax;

  private  int L_trig;

  public MainActivity() {
    super("android_cmdvel", "android_cmdvel");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    imagetopictext = (TextView) findViewById(R.id.sensor_msgs);
   // RosImageView<sensor_msgs.CompressedImage> image;



    image = (RosImageView<sensor_msgs.CompressedImage>) findViewById(R.id.image);
    image.setTopicName(imagetopicname);
    image.setMessageType(sensor_msgs.CompressedImage._TYPE);
    image.setMessageToBitmapCallable(new BitmapFromCompressedImage());
    imagetopictext.setText(imagetopicname);


    //controller.publishVelocity(x,y,0,0,0,0);

//    rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.text);
//    rosTextView.setTopicName("status");
//    rosTextView.setMessageType(std_msgs.String._TYPE);
//    rosTextView.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
//      @Override
//      public String call(std_msgs.String message) {
//        return message.getData();
//      }
//    });

  }

  public  void onCheckboxClicked(View view){
    final boolean checked = ((CheckBox) view).isChecked();
    if (checked) {
      // チェックボックス1がチェックされる
      controller.swap = true;
    } else {
      // チェックボックス1のチェックが外される
      controller.swap = false;
    }
  }
  public  void pressbutton(View view){

    Log.e("button", "push!!");
    editText = (EditText)findViewById(R.id.editText);
    imagetopicname = imagetopictext.getText().toString();

    image = (RosImageView<sensor_msgs.CompressedImage>) findViewById(R.id.image);
    image.setTopicName(imagetopicname);
    image.setMessageType(sensor_msgs.CompressedImage._TYPE);
    image.setMessageToBitmapCallable(new BitmapFromCompressedImage());

    Log.e("imagetopicname", imagetopicname);
//    image.setMessageToBitmapCallable(new BitmapFromCompressedImage());
    controller.cmd_settopicname(editText.getText().toString());
  }
  // ボタンのイベント
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event){
    boolean handled = true; //



    switch (keyCode){
      case 102://L1
        L_trig = 1;
        break;
      case 108://start
        controller.get_tsukasa_num((float)1.0);
        break;

      case 19://up
        controller.get_maxon_num((float)1.0);
        break;
      case 20://down
        controller.get_maxon_num((float)-1.0);
        break;

      default:
        break;
    }

    controller.pass_value(x,y,0,0,0,rx,L_trig);

    // キーコードをログ出力してみる
    String msg = "keyCode:" + keyCode;
    Log.e("GamePad", msg);

    return handled || super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event){
    boolean handled = true; // 処理したらtrueに

    switch (keyCode){
      case 102://L1
        L_trig = 0;
        break;
      case 108://start
        controller.get_tsukasa_num((float)0);
        break;

      case 19://up
        controller.get_maxon_num((float)0);
        break;
      case 20://down
        controller.get_maxon_num((float)0);
        break;

      default:
        break;
    }
    controller.pass_value(x,y,0,0,0,rx,L_trig);

    // キーコードをログ出力してみる
    String msg = "keyCode:" + keyCode;
    Log.e("GamePad", msg);

    return handled || super.onKeyUp(keyCode, event);
  }
  // アナログのイベント
  @Override
  public boolean onGenericMotionEvent(MotionEvent event){
//    TextView tex = (TextView)findViewById(R.id.textview2);

    boolean handled = true; // 処理したらtrueに

    // 左ジョイスティックの値をログ出力してみる
    x = event.getAxisValue(MotionEvent.AXIS_X);
    y = -1 * event.getAxisValue(MotionEvent.AXIS_Y);
    //右ジョイスティック
    rx = event.getAxisValue(MotionEvent.AXIS_Z);
    ry = event.getAxisValue(MotionEvent.AXIS_RZ);

    lt = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
    rt = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
    String msg = "(x,y)=" + x + "\n" + y+"\n" +"(rx,ry)=" + rx + "\n" + ry;
//    tex.setText(msg);

    Log.e("GamePad", msg);
    controller.pass_value(x,y,-1*ry,lt,rt,rx,L_trig);


    return handled || super.onGenericMotionEvent(event);
  }

  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {
    controller = new controllerclass();
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(), getMasterUri());
    nodeConfiguration.setMasterUri(getMasterUri());
    //nodeMainExecutor.execute(rosTextView, nodeConfiguration);
    nodeMainExecutor.execute(controller, nodeConfiguration);
    // TODO(damonkohler): Support launching different benchmarks via the UI.
    // nodeMainExecutor.execute(new PubsubBenchmark(), nodeConfiguration);
    // nodeMainExecutor.execute(new TransformBenchmark(), nodeConfiguration);
    //nodeMainExecutor.execute(new MessagesBenchmark(), nodeConfiguration);
    nodeMainExecutor.execute(image, nodeConfiguration);
  }
}
