package org.ros.android.android_benchmarks;



//import org.apache.commons.lang.ObjectUtils;
import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;

import java.util.Timer;
import java.util.TimerTask;

import geometry_msgs.Twist;
import nav_msgs.Odometry;
import std_msgs.Bool;
import std_msgs.Float32;
import std_msgs.Int32;
import std_msgs.Int64;
import std_msgs.Int8;


/**
 * Created by yukimakura on 17/09/17.
 */

public class controllerclass implements NodeMain{
    private Publisher<geometry_msgs.Twist> velPublisher;
    private Publisher<std_msgs.Float32> maxon_pub;
    private Publisher<std_msgs.Float32> tsukasa_pub;

//    public geometry_msgs.Twist vel;
    private Timer publisherTimer;
    private double linearX = 0;
    private double linearY = 0;
    private double linearZ = 0;
    private double angularX = 0;
    private double angularY = 0;
    private double angularZ = 0;
    private int L_trig = 0;
    public boolean swap = false;
    public  ConnectedNode cnode;
    private float maxon_float;

    private float tsukasa_float;
    public Node strnode;

    private String topicname = "cmd_vel";



    @Override
    public void onShutdown(Node arg0) {
    }

    @Override
    public void onShutdownComplete(Node arg0) {
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/cmd_vel");
    }


    @Override
    public void onError(Node arg0,java.lang.Throwable throwable){

    }

    public void cmd_settopicname(String str){
        topicname = str;
    }

    public void get_maxon_num(float num){
        maxon_float = num;
    }
    public void get_tsukasa_num(float num){
        tsukasa_float = num;
    }

    @Override
    public void onStart(ConnectedNode node){
        cnode = node;
        velPublisher = cnode.newPublisher(topicname, geometry_msgs.Twist._TYPE);
        maxon_pub = cnode.newPublisher("maxon", Float32._TYPE);
        tsukasa_pub = cnode.newPublisher("tsukasa", Float32._TYPE);


//        vel= velPublisher.newMessage();

        publisherTimer = new Timer();
        publisherTimer.scheduleAtFixedRate(
                new TimerTask() {
            @Override
            public void run() {
                //if (publishVelocity) {
                publishVelocity(linearX,linearY,linearZ,angularX,angularY,angularZ);
                publish_maxon(maxon_float);
                publish_tsukasa(tsukasa_float);

                //}
            }
        }, 0, 1);

    }

    public void publish_maxon(float num){
        if(maxon_pub != null){
            maxon_pub = cnode.newPublisher("maxon",Float32._TYPE);

            Float32 maxon_Float;

            maxon_Float = maxon_pub.newMessage();

            maxon_Float.setData(num);

            maxon_pub.publish(maxon_Float);
        }
    }

    public void publish_tsukasa(float num){
        if(maxon_pub != null){
            maxon_pub = cnode.newPublisher("tsukasa",Int32._TYPE);

            Float32 tsukasa_Float;

            tsukasa_Float = tsukasa_pub.newMessage();

            tsukasa_Float.setData(num);

            tsukasa_pub.publish(tsukasa_Float);
        }
    }

    public void publishzerovel() {
        if(velPublisher != null) {
            velPublisher = cnode.newPublisher(topicname, geometry_msgs.Twist._TYPE);



            geometry_msgs.Twist vel;

            vel = velPublisher.newMessage();

                vel.getLinear().setX(0);
                vel.getLinear().setY(0);
                vel.getLinear().setZ(0);
                vel.getAngular().setX(0);
                vel.getAngular().setY(0);
                vel.getAngular().setZ(0);

            Log.e("topicname", topicname);

//            Log.e("linearX", String.valueOf(linearx));
//            Log.e("linearY", String.valueOf(lineary));
//            Log.e("linearZ", String.valueOf(linearz));
//            Log.e("angularX", String.valueOf(angularx));
//            Log.e("angularY", String.valueOf(angulary));
//            Log.e("angularZ", String.valueOf(angularz));
            velPublisher.publish(vel);
        }

    }

    public void publishVelocity
            (double linearx, double lineary,double linearz ,double angularx,double angulary,double angularz) {
        if(velPublisher != null) {
            velPublisher = cnode.newPublisher(topicname, geometry_msgs.Twist._TYPE);



            geometry_msgs.Twist vel;

            vel = velPublisher.newMessage();

            if(swap == false) {
                if(L_trig == 0) {
                    vel.getLinear().setX(linearx);
                    vel.getLinear().setY(lineary);
                    vel.getLinear().setZ(linearz);
                    vel.getAngular().setX(angularx);
                    vel.getAngular().setY(angulary);
                    vel.getAngular().setZ(angularz);
                }else{
                    vel.getLinear().setX(Slow_Truncation(linearx));
                    vel.getLinear().setY(Slow_Truncation(lineary));
                    vel.getLinear().setZ(Slow_Truncation(linearz));
                    vel.getAngular().setX(Slow_Truncation(angularx));
                    vel.getAngular().setY(Slow_Truncation(angulary));
                    vel.getAngular().setZ(Slow_Truncation(angularz));
                }
            }else {
                if(L_trig == 0) {
                    //linearx = linearx*-1;
                    vel.getLinear().setX(lineary * -1);
                    vel.getLinear().setY(linearx);
                    vel.getLinear().setZ(linearz);
                    vel.getAngular().setX(angularx);
                    vel.getAngular().setY(angulary);
                    vel.getAngular().setZ(angularz * -1);
                }else{
                    vel.getLinear().setX(Slow_Truncation(lineary * -1));
                    vel.getLinear().setY(Slow_Truncation(linearx));
                    vel.getLinear().setZ(Slow_Truncation(linearz));
                    vel.getAngular().setX(Slow_Truncation(angularx));
                    vel.getAngular().setY(Slow_Truncation(angulary));
                    vel.getAngular().setZ(Slow_Truncation(angularz * -1));
                }
            }
            Log.e("topicname", topicname);

            Log.e("linearX", String.valueOf(Truncation(linearx)));
            Log.e("linearY", String.valueOf(Truncation(lineary)));
            Log.e("linearZ", String.valueOf(Truncation(linearz)));
            Log.e("angularX", String.valueOf(Truncation(angularx)));
            Log.e("angularY", String.valueOf(Truncation(angulary)));
            Log.e("angularZ", String.valueOf(Truncation(angularz)));

            velPublisher.publish(vel);
        }

    }

    public void pass_value
            (double linearx, double lineary,double linearz ,double angularx,double angulary,double angularz,int trig){
        linearX = linearx;
        linearY = lineary;
        linearZ = linearz;
        angularX = angularx;
        angularY = angulary;
        angularZ = angularz;

        L_trig = trig;
    }

    private double Truncation(double num){

        double return_num;

        int b;
        int c;

        b = (int)(num*100);
        c = b % 10;


        b -= c;

        return_num = ((double)b)/100;


        if(-0.2<return_num&&return_num<0.2){
            return 0.0;
        }else{
            return return_num;
        }
    }

    private double Slow_Truncation(double num) {

        double return_num;
        return_num = Truncation(num);
        if(return_num>0.2){
            return_num = 0.2;
        }
        else if(return_num<-0.2){
            return_num = -0.2;
        }
        return  return_num;


    }





}
