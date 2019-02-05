package com.example.test.spring;


import com.example.test.html.Camera;
import com.example.test.html.IframeCamera;

import com.example.test.html.Movie;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Push;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class CameraView extends Div {

    List<Camera> cameras = Arrays.asList(
            new Camera("192.168.1.31","前院","front yard"),
            new Camera("192.168.1.32","后院","back yard")
            );
    String url1 = "http://admin:12345abcde@";
    String url2 = "/Streaming/channels/1/httppreview";
    String url = url1+"192.168.1.38"+url2;

    IframeCamera iframeCamera = new IframeCamera(url);
    @Autowired
    MessageBean bean;

   // public void chooseMovie(int ,IframeMovie video){

   //     String title = movies.get(number).getTitle();
   //     Notification.show("Welcome Watching "+title);
   //     url = url1+movies.get(number).getStreamId();
   //     video.setSrc(url);



    //}


    public CameraView() {

        Div cameraLLayout = new Div();
        cameraLLayout.add(iframeCamera);

        Div chooseLayout = new Div();

       Button frontCameraButton = new Button(cameras.get(0).getLocationEnglish());
       Button backCameraButton = new Button(cameras.get(1).getLocationEnglish());
        chooseLayout.add(frontCameraButton,backCameraButton);

        frontCameraButton.addClickListener(e ->{
           iframeCamera.setSrc(url1+cameras.get(0).getIP()+url2);

       });

        backCameraButton.addClickListener(e ->{
            iframeCamera.setSrc(url1+cameras.get(1).getIP()+url2);

        });



        chooseLayout.setWidth("800px");
        chooseLayout.add(frontCameraButton,backCameraButton);

        getElement().setAttribute("align","center");
       // getElement().setAttribute("padding-top","150px");
        getElement().getStyle().set("padding-top","50px");
        add(chooseLayout,cameraLLayout);










    }

}
