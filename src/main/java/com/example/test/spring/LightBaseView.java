package com.example.test.spring;

import ca.bc.webarts.tools.isy.ISYRestRequester;
import com.example.test.html.AbsoluteLayout;
import com.example.test.html.LightButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Push
@Route("push/baseLight")
public class LightBaseView extends Div {

   private FeederThread thread;
    List<LightButton> mainLights = Arrays.asList(

            new LightButton("family", "19 51 94 1", 300, 300),
            new LightButton("living", "19 51 94 1", 200, 200),
            new LightButton("kitchen", "19 21 94 1", 500, 500),
            new LightButton("master","19 23 45 1",200,500),
            new LightButton("guest","12 34 56 1",200,355)
    );



    private ISYRestRequester isyRestRequester = new ISYRestRequester("192.168.1.89", "admin", "admin");
    @Autowired
    private MessageBean bean;


    public ISYRestRequester getIsyRestRequester() {
        return isyRestRequester;
    }

    public List<LightButton> getMainLights() {
        return mainLights;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        add(new Span("Waiting for updates"));

        // Start the data feed thread
        Notification.show(attachEvent.getUI().toString());
        thread = new FeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        Notification.show(detachEvent.getUI().toString());
        thread.interrupt();
        thread = null;
    }

    private static class FeederThread extends Thread {
        //Push进来的，server发过来的
        private final UI ui;
        //我自己的UI，也将发给server，server再push 给大家
        private final LightBaseView view;

        private int count = 0;

        public FeederThread(UI ui, LightBaseView view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                // Update the data for a while
                while (true) {
                    // Sleep to emulate background work
                    Thread.sleep(5000);
                    String message = "This is update " + count++;

                    ui.access(() -> {
                        Notification.show(ui.toString());
                        Notification.show(message);
                        //     view.add(new Span(message));
                        //view.getLightButton1().toggleImage(view.getIsyRestRequester().deviceAddressStatus(view.getLightButton1().getInsteonId()));
                        //     view.getLightButton2().toggle();
                        //   view.getLightButton1().toggle1Image(view.getIsyRestRequester().deviceAddressStatus(view.getLightButton1().getInsteonId()));

                    });
                }

                // Inform that we are done
                //   ui.access(() -> {
                //       view.add(new Span("Done updating"));
                //  });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }





    public LightBaseView() {

       // Button refreshtButton = new Button("refresh");

        Image bgImage = new Image();
        bgImage.setSrc("/frontend/images/bkground1024X640.png");


        AbsoluteLayout lightLayout = new AbsoluteLayout();
        lightLayout.add(bgImage, 0, 65);
     //   lightLayout.add(refreshtButton, 20, 100);


   //     lightButton1.addClickListener(e -> {

    //            isyRestRequester.deviceAddressToggle(lightButton1.getInsteonId());

     //           try {
     //                   Thread.sleep(500);
     //                   }catch (InterruptedException ex){


     //           lightButton1.toggle1Image(isyRestRequester.deviceAddressStatus(this.lightButton1.getInsteonId()));

      //          });

     //    lightLayout.add(lightButton1, lightButton1.getLeft(), lightButton1.getTop());
     //    lightLayout.add(new Label(lightButton1.getName()), lightButton1.getLeft() + 15, lightButton1.getTop() + 60);

      for (int i =0; i < mainLights.size(); i++)

      {

         int j=i;
         mainLights.get(i).addClickListener(e ->{


            // isyRestRequester.deviceAddressToggle(mainLights.get(j).getInsteonId());

                   try {
                       Thread.sleep(500);
                   }catch (InterruptedException ex) {
                   }

                 //  mainLights.get(j).toggle1Image(isyRestRequester.deviceAddressStatus(mainLights.get(j).getInsteonId()));
                   Notification.show(mainLights.get(j).toString());





         });



        lightLayout.add(mainLights.get(i),mainLights.get(i).getLeft(),mainLights.get(i).getTop());
        lightLayout.add(new Label(mainLights.get(i).getName()),mainLights.get(i).getLeft()+15,mainLights.get(i).getTop()+60);


    }






        getElement().setAttribute("align", "center");
        add(lightLayout);
    }


}










