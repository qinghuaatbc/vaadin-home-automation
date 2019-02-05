package com.example.test.spring;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route
@PWA(name = "Project Base for Vaadin Flow with Spring", shortName = "Project Base")

public class MainView extends Div {


   private  String  lightFloor[] = {">main",">up",">basement"};
   private  int     floorLightCount = 0;
   private  Boolean initial = true;


    @Autowired MessageBean bean;
    public MainView() {

        Button button1 = new Button(lightFloor[floorLightCount]);
        button1.getStyle().set("font-size","15px");
        button1.getStyle().set("background-color","Transparent");

        Tab tab1 = new Tab("Light");
        Div light = new Div();
        LightMainView lightMainView = new LightMainView();
        LightUpView lighUpView = new LightUpView();
        LightBaseView lightBaseView = new LightBaseView();

        light.add(lightMainView,lighUpView,lightBaseView);
        lighUpView.setVisible(false);
        lightBaseView.setVisible(false);
       //  page1.setText("Page#1");
        tab1.add(button1);
        button1.addClickListener(e ->
        {

            if (!initial)
            {

                if (floorLightCount == 0) {
                    lightMainView.setVisible(true);
                    lighUpView.setVisible(false);
                    lightBaseView.setVisible(false);
                    button1.setText(lightFloor[floorLightCount]);
                    floorLightCount++;
                    initial = false;

                              }
                else {

                    if (floorLightCount == 1) {
                        lightMainView.setVisible(false);
                        lighUpView.setVisible(true);
                        lightBaseView.setVisible(false);
                        button1.setText(lightFloor[floorLightCount]);
                        floorLightCount++;
                        initial = false;
                    } else {
                        if (floorLightCount == 2) {
                            lightMainView.setVisible(false);
                            lighUpView.setVisible(false);
                            lightBaseView.setVisible(true);
                            button1.setText(lightFloor[floorLightCount]);
                            floorLightCount = 0;
                            initial = false;
                        }
                    }
                }}
            else
                    {

                    lightMainView.setVisible(false);
                    lighUpView.setVisible(true);
                    lightBaseView.setVisible(false);
                    button1.setText(lightFloor[1]);
                    floorLightCount=2;

                    initial =false;
                }


        });


        Tab tab2 = new Tab("Movie");
       MovieView  movie = new MovieView();
        //
        //  Div page2 = new Div();
       // page2.setText("Page#2");
        movie.setVisible(false);

        Tab tab3 = new Tab("Camera");
        CameraView cameraView = new CameraView();
        //page3.setText("Page#3");
        cameraView.setVisible(false);

        Tab tab4 = new Tab("Youtube");
     //   Div youtube = new Div();
        Anchor youtube = new Anchor("youtube://","");
        youtube.add(new Image("/frontend/images/lightOff40X40.png",""));
        //  page4.setText("Page#4");
        youtube.setVisible(false);

        Tab tab5 = new Tab("Tab five");
        Div page5 = new Div();
        page5.setText("Page#5");
        page5.setVisible(false);

       // Tab tab6 = new Tab("Tab six");
       // Div page6 = new Div();
       // page6.setText("Page#6");
       // page6.setVisible(false);


        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tab1, light);
        tabsToPages.put(tab2, movie);
        tabsToPages.put(tab3, cameraView);
        tabsToPages.put(tab4, youtube);
        tabsToPages.put(tab5, page5);
       // tabsToPages.put(tab6, page6);


        Tabs tabs = new Tabs(tab1, tab2, tab3, tab4, tab5);
        tabs.setFlexGrowForEnclosedTabs(1);


        Div pages = new Div(light, movie, cameraView, youtube, page5);

        Set<Component> pagesShown = Stream.of(light)
                .collect(Collectors.toSet());

        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });






        getElement().setAttribute("align", "center");
        add(tabs,pages);
    }

}
